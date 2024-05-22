package gay.lemmaeof.barkeep.data.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.DrinkIngredient;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.*;

//TODO: sync! Shouldn't be hard thanks to codec-y stuff
public class CocktailRecipeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static CocktailRecipeManager INSTANCE;
	private final DynamicRegistryManager registryManager;
	private final Map<Identifier, CocktailRecipeEntry> recipes = new HashMap<>();
	private final RegistryOps<JsonElement> ops;
	private final Map<Identifier, CocktailComponent> sampleCocktails = new HashMap<>();

	public static void register(DynamicRegistryManager registryManager) {
		INSTANCE = new CocktailRecipeManager(registryManager);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
	}

	public CocktailRecipeManager(DynamicRegistryManager registryManager) {
		super(GSON, Barkeep.MODID + "/cocktails");
		this.registryManager = registryManager;
		this.ops = RegistryOps.of(JsonOps.INSTANCE, registryManager);
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		recipes.clear();
		sampleCocktails.clear();

		for (Identifier id : prepared.keySet()) {
			JsonObject json = prepared.get(id).getAsJsonObject();

			DataResult<Pair<CocktailRecipe, JsonElement>> result = CocktailRecipe.CODEC.decode(ops, json);
			if (result.isSuccess()) {
				CocktailRecipe recipe = result.getOrThrow().getFirst();
				CocktailRecipeEntry entry = new CocktailRecipeEntry(id, recipe);
				recipes.put(id, entry);
				Map<RegistryEntry<Drink>, Integer> sampleDrinks = new HashMap<>();
				List<ItemStack> sampleGarniture = new ArrayList<>();
				for (DrinkIngredient ing : recipe.drinkInputs()) {
					if (ing.getDrinks().size() > 0) sampleDrinks.put(ing.getDrinks().get(0), ing.getQuarters());
				}
				for (Ingredient i : recipe.preferredGarniture()) {
					ItemStack[] stacks = i.getMatchingStacks();
					if (stacks.length > 0) sampleGarniture.add(i.getMatchingStacks()[0]);
				}
				sampleCocktails.put(id, new CocktailComponent(Optional.of(new Cocktail(sampleDrinks, recipe.preparation(), entry)), sampleGarniture));
			} else {
				Barkeep.LOGGER.info("Error parsing cocktail {}: {}", id, result.error().toString());
			}
		}
	}

	public Optional<CocktailRecipeEntry> findCocktail(Map<Drink, Integer> drinks, CocktailPreparation preparation) {
		return recipes.values().stream().filter(cocktail -> cocktail.recipe().matches(drinks, preparation)).findFirst();
	}

	public ItemStack getSampleCocktail(Identifier id) {
		CocktailRecipe recipe = getRecipe(id);
		ItemStack stack = new ItemStack(recipe.standardGlass());
		CocktailComponent cocktail = sampleCocktails.get(id);
		stack.set(BarkeepComponents.COCKTAIL, cocktail);
		return stack;
	}

	public Cocktail createCocktail(Map<Drink, Integer> drinks, CocktailPreparation preparation) {
		Optional<CocktailRecipeEntry> recipe = findCocktail(drinks, preparation);
		Map<RegistryEntry<Drink>, Integer> drinkEntries = new HashMap<>();
		Registry<Drink> registry = registryManager.get(BarkeepRegistries.DRINKS);
		for (Drink drink : drinks.keySet()) {
			drinkEntries.put(registry.getEntry(drink), drinks.get(drink));
		}
		return new Cocktail(drinkEntries, preparation, recipe.orElse(null));
	}

	public CocktailRecipeEntry getRecipeEntry(Identifier id) {
		return recipes.get(id);
	}

	public CocktailRecipe getRecipe(Identifier id) {
		return recipes.get(id).recipe();
	}

	public Collection<Identifier> getCocktailIds() {
		return recipes.keySet();
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(Barkeep.MODID, "cocktails");
	}

}
