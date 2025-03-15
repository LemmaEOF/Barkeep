package gay.lemmaeof.barkeep.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.recipe.CocktailPreparation;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipe;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeEntry;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeManager;
import gay.lemmaeof.barkeep.util.ColorUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Cocktail {
	public static final Codec<Cocktail> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codecs.strictUnboundedMap(Drink.REGISTRY_ENTRY_CODEC, Codecs.POSITIVE_INT).fieldOf("drinks").forGetter(Cocktail::getDrinkEntries),
			CocktailPreparation.CODEC.fieldOf("preparation").forGetter(Cocktail::getPreparation),
			Identifier.CODEC.optionalFieldOf("recipe").forGetter(Cocktail::getRecipeId)
	).apply(instance, Cocktail::new));

	private final Map<Drink, Integer> drinks;
	private final Map<RegistryEntry<Drink>, Integer> drinkEntries;
	private final CocktailPreparation preparation;
	private final CocktailRecipeEntry recipe;
	private final Identifier recipeId;
	private int color;
	private int volume = 0;
	private float alcohol;
	//integer percent, for Chart Rendering(tm)
	private Map<FlavorNote, Integer> flavorProfile = new HashMap<>();
	private List<StatusEffectInstance> effects = new ArrayList<>();
	private Text name = Text.translatable("cocktail.barkeep.custom");

	//Optional for making codecs convenient
	public Cocktail(Map<RegistryEntry<Drink>, Integer> drinkEntries, CocktailPreparation preparation, Optional<Identifier> recipe) {
		this(drinkEntries, preparation, recipe.map(identifier -> {
			if (CocktailRecipeManager.CLIENT_INSTANCE != null) {
				return CocktailRecipeManager.CLIENT_INSTANCE.getRecipeEntry(identifier);
			} else if (CocktailRecipeManager.INSTANCE != null) {
				return CocktailRecipeManager.INSTANCE.getRecipeEntry(identifier);
			} else {
				Barkeep.LOGGER.error("SERIOUS SOUNDNESS ERROR: Something is trying to decerialize a cocktail recipe ({}) while this side doesn't have an active cocktail manager! Something has gone VERY wrong!", identifier);
				return null;
			}
		}).orElse(null), recipe);
	}

	//wagh erasure means I can't make this also an Optional - nullable it is!
	//I'd have this also take in the Map<Drink, Integer> but you've still gotta call this() first in a ctor and you can't functional your way through map transforms afaik
	public Cocktail(Map<RegistryEntry<Drink>, Integer> drinkEntries, CocktailPreparation preparation, @Nullable CocktailRecipeEntry recipe, Optional<Identifier> recipeId) {
		this.drinks = new HashMap<>();
		this.drinkEntries = drinkEntries;
		this.preparation = preparation;
		this.recipe = recipe;
		this.recipeId = recipeId.orElse(null);

		//volume weighted by color strengths
		float colorVolume = 0;
		//raw quarter amounts for each flavor note
		Map<FlavorNote, Integer> flavorWeights = new HashMap<>();
		//color amounts by strength and volume
		Map<TextColor, Float> colorWeights = new HashMap<>();
		//iterate through the drink list
		//only iterate once for Optimization(tm)
		for (RegistryEntry<Drink> drinkEntry : drinkEntries.keySet()) {
			int units = drinkEntries.get(drinkEntry);
			Drink drink = drinkEntry.value();
			this.drinks.put(drink, units);
			//increase volumes and alcohol content
			volume += units;
			alcohol += (units/4f) * (drink.proof() / 200f);
			if (drink.colorStrength() > 0) {
				colorVolume += units * drink.colorStrength();
				colorWeights.put(drink.color(), colorWeights.getOrDefault(drink.color(), 0f) + units * drink.colorStrength());
			}
			for (FlavorNote note : drink.flavorNotes()) {
				flavorWeights.put(note, flavorWeights.getOrDefault(note, 0) + units);
			}
		}
		//throw it off to mixbox!
		color = ColorUtil.getMixedColor(colorWeights, colorVolume);
		for (FlavorNote note : flavorWeights.keySet()) {
			//TODO: I've decided that double-input flavor notes are legal which can put this over 100 (especially w/ syrup)
			//figure out mitigating that maybe?
			flavorProfile.put(note, (int) Math.floor((float) flavorWeights.get(note) / (float) volume * 100));
		}
		//process overrides! (except for status effects, those are later)
		//this is technically duplicate work but oh *god* it's pain to try and account for that early
		//TODO: needs some clean-up still, gotta deal with the fact that flavor profile override can change effects
		if (recipe != null) {
			CocktailRecipe r = recipe.recipe();
			name = recipe.getName();
			if (r.colorOverride().isPresent()) color = r.colorOverride().get().getRgb();
			if (r.volumeOverride().isPresent()) volume = r.volumeOverride().get();
			if (r.alcoholOverride().isPresent()) alcohol = r.alcoholOverride().get();
			if (r.flavorProfileOverride().isPresent()) flavorProfile = r.flavorProfileOverride().get();
		}
		//take just the <x> most present flavor notes for effects,
		//<x> being the number of half-ounces of alcohol rounded up
		//I tried before with full ounces of alcohol but that made getting more effects *really hard*
		//TODO: drink effects
		List<FlavorNote> drinkNotes = flavorWeights.keySet().stream()
				.sorted(
						Comparator.comparingInt(note -> -1 * flavorProfile.getOrDefault(note, 0))
				)
				.toList();
		for (int i = 0; i < Math.min(Math.ceil(alcohol * 2), drinkNotes.size()); i++) {
			FlavorNote note = drinkNotes.get(i);
			//30 seconds per quarter of that flavor note - this makes 'em last *long!*
			//TODO: look into figuring out balance for that
			effects.add(new StatusEffectInstance(note.getEffect(), 600 * flavorWeights.get(note)));
		}
		//*now* do effect overrides!
		if (recipe != null) {
			CocktailRecipe r = recipe.recipe();
			if (r.effectsOverride().isPresent()) effects = r.effectsOverride().get();
		}
	}

	public Optional<Identifier> getRecipeId() {
		if (recipeId != null) return Optional.of(recipeId);
		if (recipe == null) return Optional.empty();
		else return Optional.of(recipe.id());
	}

	public Map<RegistryEntry<Drink>, Integer> getDrinkEntries() {
		return drinkEntries;
	}

	public Map<Drink, Integer> getDrinks() {
		return drinks;
	}

	public CocktailPreparation getPreparation() {
		return preparation;
	}

	//TODO: expand this? multiple colors for layering?
	public int getColor() {
		return color;
	}

	//TODO: use for what glassware you can pour this in
	public int getVolume() {
		return volume;
	}

	//TODO: alwinfy's wild alcohol-processing ride
	public float getAlcohol() {
		return alcohol;
	}

	//TODO: render a cool stat octagon(?) for flavor profile
	public Map<FlavorNote, Integer> getFlavorProfile() {
		return flavorProfile;
	}

	public List<StatusEffectInstance> getEffects() {
		return effects;
	}

	public List<Ingredient> getPreferredGarniture() {
		if (recipe != null) {
			return recipe.recipe().preferredGarniture();
		}
		return List.of();
	}

	public Text getName() {
		return name;
	}

}
