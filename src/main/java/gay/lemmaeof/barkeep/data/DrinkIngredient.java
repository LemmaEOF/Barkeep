package gay.lemmaeof.barkeep.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.util.MoreCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.ArrayList;
import java.util.List;

public class DrinkIngredient {
	public static final Codec<DrinkIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.entryList(BarkeepRegistries.DRINKS).fieldOf("drinks").forGetter(DrinkIngredient::getDrinks),
			MoreCodecs.PARTS.fieldOf("parts").forGetter(DrinkIngredient::getQuarters)
	).apply(instance, DrinkIngredient::new));

	private final RegistryEntryList<Drink> drinks;
	private final int quarters;
	private Drink[] matchingDrinks = null;

	public DrinkIngredient(RegistryEntryList<Drink> drinks, int quarters) {
		this.drinks = drinks;
		this.quarters = quarters;
	}

	public Drink[] getMatchingDrinks() {
		if (matchingDrinks != null) return matchingDrinks;
		List<Drink> matching = new ArrayList<>();
		for (RegistryEntry<Drink> drink : drinks) {
			matching.add(drink.value());
		}
		matchingDrinks = matching.toArray(new Drink[0]);
		return matchingDrinks;
	}

	public RegistryEntryList<Drink> getDrinks() {
		return drinks;
	}

	public int getQuarters() {
		return quarters;
	}

	public boolean test(Drink drink, int quarters) {
		if (quarters != this.quarters) return false;
		Drink[] drinks = getMatchingDrinks();
		for (Drink test : drinks) {
			if (test == drink) return true;
		}
		return false;
	}

}
