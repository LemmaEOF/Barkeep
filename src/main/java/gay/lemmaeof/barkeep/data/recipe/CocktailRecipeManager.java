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
import gay.lemmaeof.barkeep.networking.SynchronizeCocktailsS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//TODO: sync! Shouldn't be hard thanks to codec-y stuff
public class CocktailRecipeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static CocktailRecipeManager INSTANCE;
	//TODO: this is super fucked and having it exist makes me feel itchy but I think I have to?
	public static CocktailRecipeManager CLIENT_INSTANCE = null;
	private final DynamicRegistryManager registryManager;
	private final Map<Identifier, CocktailRecipeEntry> recipes = new HashMap<>();
	private final RegistryOps<JsonElement> ops;
	private final Map<Identifier, CocktailComponent> sampleCocktails = new HashMap<>();

	public static void register(DynamicRegistryManager registryManager) {
		INSTANCE = new CocktailRecipeManager(registryManager);
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
				sampleCocktails.put(id, new CocktailComponent(Optional.of(new Cocktail(sampleDrinks, recipe.preparation(), entry, Optional.of(entry.id()))), sampleGarniture));
			} else {
				Barkeep.LOGGER.info("Error parsing cocktail {}: {}", id, result.error().toString());
			}
		}
	}

	public void sendPacket(ServerPlayerEntity player) {
		Map<CocktailRecipeEntry, CocktailComponent> toSend = new HashMap<>();
		for (Identifier id : recipes.keySet()) {
			toSend.put(recipes.get(id), sampleCocktails.get(id));
		}
		ServerPlayNetworking.send(player, new SynchronizeCocktailsS2CPacket(toSend));
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
		return new Cocktail(drinkEntries, preparation, recipe.orElse(null), recipe.map(CocktailRecipeEntry::id));
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

	public void loadFromPacket(Map<CocktailRecipeEntry, CocktailComponent> recipeMap) {
		recipes.clear();
		sampleCocktails.clear();
		for (CocktailRecipeEntry entry : recipeMap.keySet()) {
			recipes.put(entry.id(), entry);
			sampleCocktails.put(entry.id(), recipeMap.get(entry));
		}
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(Barkeep.MODID, "cocktails");
	}

	//we need to make a new cocktail recipe manager every reload,
	//so in order to prevent polluting the resource manager with dead instances of it
	//we just register this and it calls the current instance!
	public static class ReloadWrapper implements IdentifiableResourceReloadListener {
		@Override
		public Identifier getFabricId() {
			return Identifier.of(Barkeep.MODID, "cocktails");
		}

		@Override
		public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
			return INSTANCE.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
		}
	}

}
