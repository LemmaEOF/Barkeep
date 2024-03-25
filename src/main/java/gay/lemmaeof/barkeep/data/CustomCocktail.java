package gay.lemmaeof.barkeep.data;

import gay.lemmaeof.barkeep.util.ColorUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.*;

//TODO: track preparation for aesthetic/mechanical differences
public class CustomCocktail implements Cocktail {
	private final Map<Drink, Integer> drinks;
	private final int color;
	private int volume = 0;
	private final float alcohol;
	private final Map<FlavorNote, Integer> flavorProfile = new HashMap<>();
	private final List<StatusEffectInstance> effects = new ArrayList<>();

	public CustomCocktail(Map<Drink, Integer> drinks) {
		this.drinks = drinks;
		float colorVolume = 0;
		float currentAlcohol = 0;
		Map<FlavorNote, Integer> flavorWeights = new HashMap<>();
		Map<TextColor, Float> colorWeights = new HashMap<>();
		for (Drink drink : drinks.keySet()) {
			int units = drinks.get(drink);
			volume += units;
			currentAlcohol += (units/4f) * (drink.proof() / 100f);
			if (drink.colorStrength() > 0) {
				colorVolume += units * drink.colorStrength();
				colorWeights.put(drink.color(), colorWeights.getOrDefault(drink.color(), 0f) + units * drink.colorStrength());
			}
			for (FlavorNote note : drink.flavorNotes()) {
				flavorWeights.put(note, flavorWeights.getOrDefault(note, 0) + units);
			}
		}
		alcohol = currentAlcohol;
		color = ColorUtil.getDrinkColor(colorWeights, colorVolume);
		for (FlavorNote note : flavorWeights.keySet()) {
			flavorProfile.put(note, flavorWeights.get(note) / volume);
		}
		List<FlavorNote> allNotes = new ArrayList<>(List.of(FlavorNote.values()));
		allNotes.sort(Comparator.comparing(note -> flavorProfile.getOrDefault(note, 0)));
		for (int i = 0; i < Math.min(Math.ceil(alcohol), 8); i++) {
			FlavorNote note = allNotes.get(i);
			if (flavorWeights.containsKey(note)) {
				effects.add(new StatusEffectInstance(note.getEffect(), 600 * flavorWeights.get(note)));
			}
		}
	}

	public Map<Drink, Integer> getDrinks() {
		return drinks;
	}

	public int getColor() {
		return color;
	}

	public int getVolume() {
		return volume;
	}

	public float getAlcohol() {
		return alcohol;
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
	public List<Ingredient> getPreferredGarniture() {
		return List.of();
	}

	@Override
	public Text getName() {
		return Text.translatable("cocktail.barkeep.custom");
	}

	@Override
	public NbtElement toTag(DynamicRegistryManager manager) {
		NbtCompound ret = new NbtCompound();
		for (Drink drink : drinks.keySet()) {
			ret.putInt(drink.getId(manager).toString(), drinks.get(drink));
		}
		return ret;
	}
}
