package gay.lemmaeof.barkeep.data;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.List;
import java.util.Map;

//TODO: muddling
public class RecipeCocktail implements Cocktail {
	private Map<DrinkIngredient, Integer> drinks;
	private List<Ingredient> garniture;
	private Preparation preparation;
	private int color;
	private float volume;
	private float alcohol;
	private Text name;
	private Map<FlavorNote, Float> flavorProfile;
	private List<StatusEffectInstance> effects;

	public RecipeCocktail(Map<DrinkIngredient, Integer> drinks, List<Ingredient> garniture, Preparation preparation,
						  TextColor color, float volume, float alcohol, Text name, Map<FlavorNote, Float> flavorProfile,
						  List<StatusEffectInstance> effects) {
		this.drinks = drinks;
		this.garniture = garniture;
		this.preparation = preparation;
		this.color = color.getRgb();
		this.volume = volume;
		this.alcohol = alcohol;
		this.name = name;
		this.flavorProfile = flavorProfile;
		this.effects = effects;
	}

	public boolean matches() {
		return false; //TODO: impl
	}

	@Override
	public List<Item> getGarniture() {
		return garniture.stream().map(ing -> ing.getMatchingStacks()[0].getItem()).toList();
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public float getVolume() {
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
	public Map<FlavorNote, Float> getFlavorProfile() {
		return flavorProfile;
	}

	@Override
	public List<StatusEffectInstance> getEffects() {
		return effects;
	}

	public enum Preparation {
		SHAKEN,
		STIRRED,
		SHAKEN_ROCKS,
		STIRRED_ROCKS
	}
}
