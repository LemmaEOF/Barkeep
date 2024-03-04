package gay.lemmaeof.barkeep.data;

import gay.lemmaeof.barkeep.util.ColorUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.*;

public class CustomCocktail implements Cocktail {
	private final Map<Drink, Float> drinks;
	private final List<Item> garniture;
	private final int color;
	private float volume = 0;
	private final float alcohol;
	private final Map<FlavorNote, Float> flavorProfile = new HashMap<>();
	private final List<StatusEffectInstance> effects = new ArrayList<>();

	public CustomCocktail(Map<Drink, Float> drinks, List<Item> garniture) {
		this.drinks = drinks;
		this.garniture = garniture;
		float colorVolume = 0f;
		float currentAlcohol = 0f;
		Map<FlavorNote, Float> flavorWeights = new HashMap<>();
		Map<TextColor, Float> colorWeights = new HashMap<>();
		for (Drink drink : drinks.keySet()) {
			float units = drinks.get(drink);
			volume += units;
			currentAlcohol += units * (drink.proof() / 200f);
			if (drink.color().isPresent()) {
				colorVolume += units;
				colorWeights.put(drink.color().get(), colorWeights.getOrDefault(drink.color().get(), 0f) + units);
			}
			for (FlavorNote note : drink.flavorNotes()) {
				flavorWeights.put(note, flavorWeights.getOrDefault(note, 0f) + units);
			}
		}
		alcohol = currentAlcohol;
		color = ColorUtil.getDrinkColor(colorWeights, colorVolume);
		for (FlavorNote note : flavorWeights.keySet()) {
			flavorProfile.put(note, flavorWeights.get(note) / volume);
		}
		List<FlavorNote> allNotes = new ArrayList<>(List.of(FlavorNote.values()));
		allNotes.sort(Comparator.comparing(note -> flavorProfile.getOrDefault(note, 0f)));
		for (int i = 0; i < Math.min(Math.ceil(alcohol), 8); i++) {
			FlavorNote note = allNotes.get(i);
			if (flavorWeights.containsKey(note)) {
				effects.add(new StatusEffectInstance(note.getEffect(), (int) (300 * flavorWeights.get(note))));
			}
		}
	}

	public Map<Drink, Float> getDrinks() {
		return drinks;
	}

	public List<Item> getGarniture() {
		return garniture;
	}

	public int getColor() {
		return color;
	}

	public float getVolume() {
		return volume;
	}

	public float getAlcohol() {
		return alcohol;
	}

	@Override
	public Map<FlavorNote, Float> getFlavorProfile() {
		return flavorProfile;
	}

	@Override
	public List<StatusEffectInstance> getEffects() {
		return effects;
	}

	@Override
	public Text getName() {
		return Text.translatable("cocktail.barkeep.custom");
	}
}
