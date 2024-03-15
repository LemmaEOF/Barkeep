package gay.lemmaeof.barkeep.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.util.ColorUtil;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//TODO: proper cocktails and not just custom
public class CocktailManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static CocktailManager INSTANCE;
	private final DynamicRegistryManager registryManager;
	private final Map<Identifier, RecipeCocktail> cocktails = new HashMap<>();

	public static void register(DynamicRegistryManager registryManager) {
		INSTANCE = new CocktailManager(registryManager);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
	}

	public CocktailManager(DynamicRegistryManager registryManager) {
		super(GSON, Barkeep.MODID + "/cocktails");
		this.registryManager = registryManager;
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		cocktails.clear();

		//TODO: this is a bit of a mess due to how things need to be calculated lmao
		for (Identifier id : prepared.keySet()) {
			JsonObject json = prepared.get(id).getAsJsonObject();

			//drink ingredients define
			Map<DrinkIngredient, Integer> ingredients = new HashMap<>();
			if (JsonHelper.hasArray(json, "drinks")) {
				for (JsonElement elem : JsonHelper.getArray(json, "drink")) {
					JsonObject obj = JsonHelper.asObject(elem, "drink entry");
					DrinkIngredient ing = DrinkIngredient.fromJson(obj, registryManager);
					int amount = (int) Math.floor(JsonHelper.getFloat(obj, "parts") * 4);
					ingredients.put(ing, amount);
				}
			} else throw new IllegalArgumentException("Cocktail must have drinks in it");

			//preferred garniture define
			List<Ingredient> garniture = new ArrayList<>();
			if (JsonHelper.hasArray(json, "preferred_garniture")) {
				for (JsonElement elem : JsonHelper.getArray(json, "preferred_garniture")) {
					garniture.add(Ingredient.fromJson(elem));
				}
			}

			//preparation define
			RecipeCocktail.Preparation prep = RecipeCocktail.Preparation.forName(JsonHelper.getString(json, "preparation"));

			//name define/calc
			Text name;
			if (JsonHelper.hasElement(json, "name")) {
				name = Text.Serializer.fromJson(JsonHelper.getElement(json, "name"));
			} else {
				name = Text.translatable(id.toTranslationKey("cocktail"));
			}

			//color define/calc check
			int color = 0xFFFFFF;
			Map<TextColor, Integer> colorWeights = new HashMap<>();
			int colorVolume = 0;
			boolean calcColor = false;
			if (JsonHelper.hasString(json, "color")) {
				color = TextColor.parse(JsonHelper.getString(json, "color")).getRgb();
			} else {
				calcColor = true;
			}

			//volume define/calc check
			int volume = 0;
			boolean calcVolume = false;
			if (JsonHelper.hasNumber(json, "volume")) {
				volume = (int) Math.floor(JsonHelper.getFloat(json, "volume") * 4);
			} else {
				calcVolume = true;
			}

			//alcohol content define/calc check
			float alcohol = 0;
			boolean calcAlcohol = false;
			if (JsonHelper.hasNumber(json, "alcohol")) {
				alcohol = JsonHelper.getFloat(json, "alcohol");
			} else {
				calcAlcohol = true;
			}

			//flavor profile define/calc check
			Map<FlavorNote, Integer> flavorProfile = new HashMap<>();
			Map<FlavorNote, Integer> flavorWeights = new HashMap<>();
			boolean calcFlavorProfile = false;
			if (JsonHelper.hasJsonObject(json, "flavor_profile")) {
				for (String key : JsonHelper.getObject(json, "flavor_profile").keySet()) {
					flavorProfile.put(FlavorNote.byName(key), (int) Math.floor(JsonHelper.getFloat(json, key) * 4));
				}
			} else {
				calcFlavorProfile = true;
			}

			//do all the calcs if we need to!
			for (DrinkIngredient ing : ingredients.keySet()) {
				int drinkVol = ingredients.get(ing);
				Drink drink = ing.getMatchingDrinks(registryManager)[0];
				if (calcColor) {
					if (drink.color().isPresent()) {
						colorWeights.put(drink.color().get(), drinkVol);
						colorVolume += drinkVol;
					}
				}
				if (calcVolume) {
					volume += drinkVol;
				}
				if (calcAlcohol) {
					alcohol += (drinkVol/4f) * (drink.proof() / 200f);
				}
				if (calcFlavorProfile) {
					for (FlavorNote note : drink.flavorNotes()) {
						flavorWeights.put(note, flavorWeights.getOrDefault(note, 0) + drinkVol);
					}
				}
			}

			//post-calc for color
			if (calcColor) {
				color = ColorUtil.getDrinkColor(colorWeights, colorVolume);
			}

			//post-calc for flavor profile
			if (calcFlavorProfile) {
				for (FlavorNote note : flavorWeights.keySet()) {
					flavorProfile.put(note, flavorWeights.get(note) / volume);
				}
			}

			//status effect define/calc
			List<StatusEffectInstance> effects = new ArrayList<>();
			if (JsonHelper.hasArray(json, "effects")) {
				for (JsonElement elem : JsonHelper.getArray(json, "effects")) {
					if (elem.isJsonObject()) {
						JsonObject eff = elem.getAsJsonObject();
						Identifier effId = new Identifier(JsonHelper.getString(eff, "id"));
						StatusEffect effect = Registries.STATUS_EFFECT.get(effId);
						if (effect == null) throw new IllegalArgumentException("No status effect " + effId);
						int duration = JsonHelper.getInt(eff, "duration");
						int amplifier = JsonHelper.getInt(eff, "amplifier", 0);
						boolean ambient = JsonHelper.getBoolean(eff, "ambient", false);
						boolean showParticles = JsonHelper.getBoolean(eff, "show_particles", true);
						boolean showIcon = JsonHelper.getBoolean(eff, "show_icon", true);
						effects.add(new StatusEffectInstance(effect, duration, amplifier, ambient, showParticles, showIcon));
					} else {
						throw new IllegalArgumentException("Status effect instance must be a JSON object");
					}
				}
			} else {
				List<FlavorNote> allNotes = new ArrayList<>(List.of(FlavorNote.values()));
				allNotes.sort(Comparator.comparing(note -> flavorProfile.getOrDefault(note, 0)));
				for (int i = 0; i < Math.min(Math.ceil(alcohol), 8); i++) {
					FlavorNote note = allNotes.get(i);
					if (flavorWeights.containsKey(note)) {
						effects.add(new StatusEffectInstance(note.getEffect(), 300 * flavorWeights.get(note)));
					}
				}
			}

			//ASSEMBLE!
			cocktails.put(id, new RecipeCocktail(
					id,
					ingredients,
					garniture,
					prep,
					name,
					color,
					volume,
					alcohol,
					flavorProfile,
					effects
			));
		}
	}

	public Cocktail findCocktail(Map<Drink, Integer> drinks, RecipeCocktail.Preparation preparation) {
		Optional<RecipeCocktail> search = cocktails.values().stream().filter(cocktail -> cocktail.matches(drinks, preparation, registryManager)).findFirst();
		if (search.isPresent()) return search.get();
		else return new CustomCocktail(drinks);
	}

	public Cocktail getCocktail(Identifier id) {
		return cocktails.get(id);
	}

	//replace with components once those are real (thank god lmao)
	public boolean hasCocktail(ItemStack stack) {
		return (stack.hasNbt()
				&& (stack.getNbt().contains("cocktail", NbtElement.STRING_TYPE)
				|| stack.getNbt().contains("cocktail", NbtElement.COMPOUND_TYPE)));
	}

	@Nullable
	public Cocktail getCocktail(ItemStack stack) {
		NbtCompound tag = stack.getNbt();
		if (tag == null) return null;
		if (tag.contains("cocktail", NbtElement.STRING_TYPE)) return getCocktail(new Identifier(tag.getString("cocktail")));
		else if (tag.contains("cocktail", NbtElement.COMPOUND_TYPE)) {
			NbtCompound cocktailTag = tag.getCompound("cocktail");
			Registry<Drink> drinkRegistry = registryManager.get(BarkeepRegistries.DRINKS);
			Map<Drink, Integer> drinks = new HashMap<>();
			for (String key : cocktailTag.getKeys()) {
				Drink drink = drinkRegistry.getOrEmpty(new Identifier(key)).orElse(new Drink(Optional.empty(), 0, List.of()));
				int volume = cocktailTag.getInt(key);
				drinks.put(drink, volume);
			}
			return new CustomCocktail(drinks);
		}
		return null;
	}

	public int getCocktailColor(ItemStack stack) {
		NbtCompound tag = stack.getNbt();
		if (tag == null) return 0xFFFFFF;
		if (tag.contains("cocktail", NbtElement.STRING_TYPE)) return getCocktail(new Identifier(tag.getString("cocktail"))).getColor();
		else if (tag.contains("cocktail", NbtElement.COMPOUND_TYPE)) {
			NbtCompound cocktailTag = tag.getCompound("cocktail");
			Registry<Drink> drinkRegistry = registryManager.get(BarkeepRegistries.DRINKS);
			Map<TextColor, Integer> colorWeights = new HashMap<>();
			int colorVolume = 0;
			for (String key : cocktailTag.getKeys()) {
				Drink drink = drinkRegistry.getOrEmpty(new Identifier(key)).orElse(new Drink(Optional.empty(), 0, List.of()));
				if (drink.color().isPresent()) {
					int volume = cocktailTag.getInt(key);
					colorWeights.put(drink.color().get(), volume);
					colorVolume += volume;
				}
			}
			return ColorUtil.getDrinkColor(colorWeights, colorVolume);
		}
		return 0xFFFFFF;
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(Barkeep.MODID, "cocktails");
	}
}
