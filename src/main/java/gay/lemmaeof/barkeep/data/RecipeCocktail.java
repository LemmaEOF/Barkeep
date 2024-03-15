package gay.lemmaeof.barkeep.data;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//TODO: muddling, packets
public class RecipeCocktail implements Cocktail {
	private final Identifier id;
	private final Map<DrinkIngredient, Integer> drinkInputs;
	private final List<Ingredient> preferredGarniture;
	private final Preparation preparation;
	private final int color;
	private final int volume;
	private final float alcohol;
	private final Text name;
	private final Map<FlavorNote, Integer> flavorProfile;
	private final List<StatusEffectInstance> effects;

	public RecipeCocktail(Identifier id, Map<DrinkIngredient, Integer> drinkInputs, List<Ingredient> preferredGarniture, Preparation preparation,
						  Text name, int color, int volume, float alcohol, Map<FlavorNote, Integer> flavorProfile,
						  List<StatusEffectInstance> effects) {
		this.id = id;
		this.drinkInputs = drinkInputs;
		this.preferredGarniture = preferredGarniture;
		this.preparation = preparation;
		this.name = name;
		this.color = color;
		this.volume = volume;
		this.alcohol = alcohol;
		this.flavorProfile = flavorProfile;
		this.effects = effects;
	}

	//TODO: oh god what have I done - can this be optimized? feels O(n^2)-ish
	//TODO: this might get fucky with tags? that might be a skill issue on the data packer's side though
	//TODO: just have lists of ingredients and drinks that need to be checked off and say no if there's any left after
	public boolean matches(Map<Drink, Integer> drinks, Preparation preparation, DynamicRegistryManager manager) {
		//quick-exit if preparation is wrong or there's *clearly* mismatched drinks
		if (drinks.size() != drinkInputs.size() || preparation != this.preparation) return false;
		//iterate through all ingredients - they need to test each drink so they're higher-prio
		INGREDIENTS: for (DrinkIngredient ing : drinkInputs.keySet()) {
			//iterate all drinks in the container
			for (Drink drink : drinks.keySet()) {
				//test to make sure they're the right drink
				if (ing.test(drink, manager)) {
					//they're the right drink! test to make sure it's the right amount
					//if they are then continue to the next outer loop iter, otherwise keep looking
					if (Objects.equals(drinkInputs.get(ing), drinks.get(drink))) continue INGREDIENTS;
				}
			}
			//went through all the drinks and didn't find the right one
			return false;
		}
		//should be good!
		return true;
	}

	@Override
	public List<Ingredient> getPreferredGarniture() {
		return preferredGarniture;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public int getVolume() {
		return volume;
	}

	@Override
	public float getAlcohol() {
		return alcohol;
	}

	@Override
	public Text getName() {
		return name;
	}

	@Override
	public Map<FlavorNote, Integer> getFlavorProfile() {
		return flavorProfile;
	}

	@Override
	public List<StatusEffectInstance> getEffects() {
		return effects;
	}

	@Override
	public NbtElement toTag(DynamicRegistryManager manager) {
		return NbtString.of(id.toString());
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
