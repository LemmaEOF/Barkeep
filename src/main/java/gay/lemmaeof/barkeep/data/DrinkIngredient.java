package gay.lemmaeof.barkeep.data;

import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DrinkIngredient {
	Entry[] entries;
	Drink[] matchingDrinks;

	public Drink[] getMatchingDrinks(DynamicRegistryManager manager) {
		if (matchingDrinks != null) return matchingDrinks;
		List<Drink> drinks = new ArrayList<>();
		for (Entry entry : entries) {
			drinks.addAll(entry.getMatchingDrinks(manager));
		}
		matchingDrinks = drinks.toArray(new Drink[0]);
		return matchingDrinks;
	}

	public boolean test(Drink drink, DynamicRegistryManager manager) {
		for (Drink d : getMatchingDrinks(manager)) {
			if (d == drink) return true;
		}
		return false;
	}

	public interface Entry {
		Collection<Drink> getMatchingDrinks(DynamicRegistryManager manager);
	}

	private static class DrinkEntry implements Entry {
		private final Drink drink;

		public DrinkEntry(Drink drink) {
			this.drink = drink;
		}

		@Override
		public Collection<Drink> getMatchingDrinks(DynamicRegistryManager manager) {
			return Collections.singleton(drink);
		}
	}

	private static class TagEntry implements Entry {
		private final TagKey<Drink> tag;

		public TagEntry(TagKey<Drink> tag) {
			this.tag = tag;
		}

		@Override
		public Collection<Drink> getMatchingDrinks(DynamicRegistryManager manager) {
			List<Drink> ret = new ArrayList<>();
			for (RegistryEntry<Drink> drink : manager.get(BarkeepRegistries.DRINKS).iterateEntries(tag)) {
				ret.add(drink.value());
			}
			return ret;
		}
	}
}
