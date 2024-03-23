package gay.lemmaeof.barkeep.api;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.Drink;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

/**
 * An interface representing bottled drinks for use in cocktails.
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
	 * @return The amount of drink stored in this container, in quarter-parts.
	 */
	int getVolume();

	/**
	 * Attempt to pour a given amount of quarter-parts from this container.
	 * @param quarterParts The amount of drink to pour, in quarter-parts.
	 * @return The amount of drink successfully poured, in quarter-parts.
	 */
	//TODO: this is a bit sloppy, refine API later or is it not a big deal?
	int tryPour(int quarterParts);
}
