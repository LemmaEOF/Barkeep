package gay.lemmaeof.barkeep.data;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

//TODO: muddling
public class RecipeCocktail implements Cocktail {
	private final Identifier id;
	private final Map<DrinkIngredient, Integer> drinks;
	private final List<Ingredient> preferredGarniture;
	private Preparation preparation;
	private int color;
	private int volume;
	private float alcohol;
	private Text name;
	private Map<FlavorNote, Integer> flavorProfile;
	private List<StatusEffectInstance> effects;

	public RecipeCocktail(Identifier id, Map<DrinkIngredient, Integer> drinks, List<Ingredient> preferredGarniture, Preparation preparation,
						  TextColor color, int volume, float alcohol, Text name, Map<FlavorNote, Integer> flavorProfile,
						  List<StatusEffectInstance> effects) {
		this.id = id;
		this.drinks = drinks;
		this.preferredGarniture = preferredGarniture;
		this.preparation = preparation;
		this.color = color.getRgb();
		this.volume = volume;
		this.alcohol = alcohol;
		this.name = name;
		this.flavorProfile = flavorProfile;
		this.effects = effects;
	}

	public boolean matches(Map<Drink, Integer> drinks, DynamicRegistryManager manager) {
		return false; //TODO: impl
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

	public enum Preparation {
		SHAKEN,
		STIRRED
	}
}
