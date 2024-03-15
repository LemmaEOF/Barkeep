package gay.lemmaeof.barkeep.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DrinkIngredient {
	Entry[] entries;
	Drink[] matchingDrinks;

	private DrinkIngredient(Entry[] entries) {
		this.entries = entries;
	}

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

	public static DrinkIngredient fromJson(JsonElement json, DynamicRegistryManager manager) {
		List<Entry> entries = new ArrayList<>();
		if (json.isJsonArray()) {
			for (JsonElement elem : json.getAsJsonArray()) {
				entries.add(getEntry(elem.getAsJsonObject(), manager));
			}
		} else if (json.isJsonObject()) {
			entries.add(getEntry(json.getAsJsonObject(), manager));
		} else {
			throw new IllegalArgumentException("Drink ingredient must be an array or object of arrays");
		}
		return new DrinkIngredient(entries.toArray(new Entry[0]));
	}

	private static Entry getEntry(JsonObject json, DynamicRegistryManager manager) {
		if (JsonHelper.hasJsonObject(json, "drink") && !json.has("tag")) {
			String value = JsonHelper.getString(json, "drink");
			Drink drink = manager.get(BarkeepRegistries.DRINKS).get(new Identifier(value));
			if (drink == null) throw new IllegalArgumentException("Unknown drink: " + value);
			return new DrinkEntry(drink);
		} else if (JsonHelper.hasJsonObject(json, "tag") && !json.has("drink")) {
			TagKey<Drink> tag = TagKey.of(BarkeepRegistries.DRINKS, new Identifier(JsonHelper.getString(json, "tag")));
			return new TagEntry(tag);
		} else {
			throw new IllegalArgumentException("Drink ingredient must contain either 'drink' or 'tag' value");
		}
	}
}
