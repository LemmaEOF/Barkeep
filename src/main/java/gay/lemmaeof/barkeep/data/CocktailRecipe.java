package gay.lemmaeof.barkeep.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.util.MoreCodecs;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

import java.util.*;
import java.util.function.UnaryOperator;

//TODO: muddling, packets
public record CocktailRecipe(List<DrinkIngredient> drinkInputs, List<Ingredient> preferredGarniture,
							 Preparation preparation, Optional<Text> name, Optional<TextColor> colorOverride,
							 Optional<Integer> volumeOverride, Optional<Float> alcoholOverride,
							 Optional<Map<FlavorNote, Integer>> flavorProfileOverride,
							 Optional<List<StatusEffectInstance>> effectsOverride) {
	public static final Codec<CocktailRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DrinkIngredient.CODEC.listOf().fieldOf("drinks").forGetter(CocktailRecipe::drinkInputs),
			Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("preferred_garniture").forGetter(CocktailRecipe::preferredGarniture),
			StringIdentifiable.createCodec(Preparation::values).fieldOf("preparation").forGetter(CocktailRecipe::preparation),
			TextCodecs.CODEC.optionalFieldOf("name").forGetter(CocktailRecipe::name),
			TextColor.CODEC.optionalFieldOf("color").forGetter(CocktailRecipe::colorOverride),
			Codecs.POSITIVE_INT.optionalFieldOf("volume").forGetter(CocktailRecipe::volumeOverride),
			MoreCodecs.NONNEGATIVE_FLOAT.optionalFieldOf("alcohol").forGetter(CocktailRecipe::alcoholOverride),
			Codecs.strictUnboundedMap(FlavorNote.CODEC, Codecs.POSITIVE_INT).xmap(x -> {
				Map<FlavorNote, Integer> map = new EnumMap<>(FlavorNote.class);
				map.putAll(x);
				return map;
			}, UnaryOperator.identity()).optionalFieldOf("flavor_profile").forGetter(CocktailRecipe::flavorProfileOverride),
			StatusEffectInstance.CODEC.listOf().optionalFieldOf("effects").forGetter(CocktailRecipe::effectsOverride)
	).apply(instance, CocktailRecipe::new));

	public boolean matches(Map<Drink, Integer> drinks, Preparation preparation) {
		//quick-exit if preparation is wrong or there's *clearly* mismatched drinks
		if (drinks.size() != drinkInputs.size() || preparation != this.preparation) return false;
		List<DrinkIngredient> toFind = new ArrayList<>(drinkInputs);
		Map<Drink, Integer> toTest = new HashMap<>(drinks);
		for (DrinkIngredient ing : drinkInputs) {
			for (Drink drink : toTest.keySet()) {
				if (ing.test(drink, drinks.get(drink))) {
					toFind.remove(ing);
					toTest.remove(drink);
					break;
				}
			}
		}
		return toFind.isEmpty() && toTest.isEmpty();
	}

	//TODO: other preparations?
	public enum Preparation implements StringIdentifiable {
		SHAKEN("shaken"),
		STIRRED("stirred"),
		DRY_SHAKEN("dry_shaken"),
		BLENDED("blended");

		private final String name;

		Preparation(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

		public static Preparation forName(String name) {
			for (Preparation prep : values()) {
				if (prep.name.equals(name.toLowerCase(Locale.ROOT))) return prep;
			}
			throw new IllegalArgumentException("No known preparation named " + name);
		}
	}
}
