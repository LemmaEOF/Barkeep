package gay.lemmaeof.barkeep.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.util.ColorUtil;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
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
	}

	//TODO: impl
	public Cocktail findCocktail() {
		return null;
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
			NbtCompound cocktailCompound = tag.getCompound("cocktail");
			Registry<Drink> drinkRegistry = registryManager.get(BarkeepRegistries.DRINKS);
			NbtCompound drinksTag = cocktailCompound.getCompound("drinks");
			NbtList garnitureTag = cocktailCompound.getList("garniture", NbtElement.STRING_TYPE);
			Map<Drink, Float> drinks = new HashMap<>();
			List<Item> garniture = new ArrayList<>();
			for (String key : drinksTag.getKeys()) {
				Drink drink = drinkRegistry.getOrEmpty(new Identifier(key)).orElse(new Drink(Optional.empty(), 0, List.of()));
				float volume = drinksTag.getFloat(key);
				drinks.put(drink, volume);
			}
			for (int i = 0; i < garnitureTag.size(); i++) {
				garniture.add(Registries.ITEM.get(new Identifier(garnitureTag.getString(i))));
			}
			return new CustomCocktail(drinks, garniture);
		}
		return null;
	}

	public int getCocktailColor(ItemStack stack) {
		NbtCompound tag = stack.getNbt();
		if (tag == null) return 0xFFFFFF;
		if (tag.contains("cocktail", NbtElement.STRING_TYPE)) return getCocktail(new Identifier(tag.getString("cocktail"))).getColor();
		else if (tag.contains("cocktail", NbtElement.COMPOUND_TYPE)) {
			NbtCompound cocktailCompound = tag.getCompound("cocktail");
			Registry<Drink> drinkRegistry = registryManager.get(BarkeepRegistries.DRINKS);
			NbtCompound drinksTag = cocktailCompound.getCompound("drinks");
			Map<TextColor, Float> colorWeights = new HashMap<>();
			float colorVolume = 0f;
			for (String key : drinksTag.getKeys()) {
				Drink drink = drinkRegistry.getOrEmpty(new Identifier(key)).orElse(new Drink(Optional.empty(), 0, List.of()));
				if (drink.color().isPresent()) {
					float volume = drinksTag.getFloat(key);
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
