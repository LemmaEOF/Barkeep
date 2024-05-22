package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.recipe.PotionLabellingRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BarkeepRecipes {
	public static final RecipeSerializer<PotionLabellingRecipe> POTION_LABELLING = register("potion_labelling", new SpecialRecipeSerializer<>(PotionLabellingRecipe::new));

	public static void init() {}

	private static <C extends Inventory, T extends Recipe<C>> RecipeSerializer<T> register(String name, RecipeSerializer<T> serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(Barkeep.MODID, name), serializer);
	}
}
