package gay.lemmaeof.barkeep.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record CocktailRecipeEntry(Identifier id, CocktailRecipe recipe) {
	public static final Codec<CocktailRecipeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("id").forGetter(CocktailRecipeEntry::id),
			CocktailRecipe.CODEC.fieldOf("recipe").forGetter(CocktailRecipeEntry::recipe)
	).apply(instance, CocktailRecipeEntry::new));

	public Text getName() {
		if (recipe.nameOverride().isPresent()) return recipe.nameOverride().get();
		return Text.translatable(id.toTranslationKey("cocktail"));
	}
}
