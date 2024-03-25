package gay.lemmaeof.barkeep.api;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.Drink;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

/**
 * An interface representing bottled drinks for use in cocktails.
 *
 * Will be used with the context of a {@link DynamicRegistryManager} for accessing Drinks from registry keys.
 *
 * KEY TERMINOLOGY:
 * <b>Part</b> - Equivalent to one United States customary fluid ounce (~29.57 mL).
 * <b>Quarter</b> - One fourth of a part, equivalent to one half of a United States customary tablespoon (~7.30 mL).
 *
 * The base unit for drinks in a DrinkContainer is quarters.
 * Drinks are presented to the end-user as fractional parts.
 *
 * Why use fake unit names instead of real ones? <a href="https://en.wikipedia.org/wiki/Alcohol_measurements">You tell me!</a>
 */
public interface DrinkContainer {
	Identifier LOOKUP_ID = new Identifier(Barkeep.MODID, "drink_container");
	BlockApiLookup<DrinkContainer, DynamicRegistryManager> BLOCK_LOOKUP = BlockApiLookup.get(
			LOOKUP_ID,
			DrinkContainer.class,
			DynamicRegistryManager.class
	);
	ItemApiLookup<DrinkContainer, DynamicRegistryManager> ITEM_LOOKUP = ItemApiLookup.get(
			LOOKUP_ID,
			DrinkContainer.class,
			DynamicRegistryManager.class
	);

	/**
	 * @return The drink stored in this container.
	 */
	Drink getDrink();

	/**
	 * @return The amount of drink stored in this container, in quarters.
	 */
	int getVolume();

	/**
	 * Attempt to pour a given amount of quarters from this container.
	 * @param quarters The amount of drink to pour, in quarters.
	 * @return The amount of drink successfully poured, in quarter-parts.
	 */
	//TODO: this is a bit sloppy, refine API later or is it not a big deal?
	int tryPour(int quarters);
}
