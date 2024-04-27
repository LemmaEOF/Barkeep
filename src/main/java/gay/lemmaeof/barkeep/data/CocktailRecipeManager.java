package gay.lemmaeof.barkeep.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CocktailRecipeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static CocktailRecipeManager INSTANCE;
	private final DynamicRegistryManager registryManager;
	private final Map<Identifier, CocktailRecipe> recipes = new HashMap<>();
	private final RegistryOps<JsonElement> ops;

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

		for (Identifier id : prepared.keySet()) {
			JsonObject json = prepared.get(id).getAsJsonObject();

			DataResult<Pair<CocktailRecipe, JsonElement>> result = CocktailRecipe.CODEC.decode(ops, json);
			if (result.isSuccess()) {
				recipes.put(id, result.getOrThrow().getFirst());
			} else {
				Barkeep.LOGGER.info("Error parsing cocktail {}: {}", id, result.error().toString());
			}
		}
	}

	public Optional<CocktailRecipe> findCocktail(Map<Drink, Integer> drinks, CocktailRecipe.Preparation preparation) {
		return recipes.values().stream().filter(cocktail -> cocktail.matches(drinks, preparation)).findFirst();
	}

	public CocktailComponent createSampleCocktail(Identifier id) {
		//TODO: impl, cache these
		return null;
	}

	public Cocktail createCocktail(Map<Drink, Integer> drinks, CocktailRecipe.Preparation preparation) {
		//TODO: impl
		return null;
	}

	public CocktailRecipe getRecipe(Identifier id) {
		return recipes.get(id);
	}

	public Collection<Identifier> getCocktailIds() {
		return recipes.keySet();
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(Barkeep.MODID, "cocktails");
	}
}
