package gay.lemmaeof.barkeep.impl;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.item.BottledDrinkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;

public class BottleDrinkContainer implements DrinkContainer {
	private final ItemStack stack;
	private final DynamicRegistryManager manager;
	private final BottledDrinkItem bottle;

	public BottleDrinkContainer(ItemStack stack, DynamicRegistryManager manager) {
		this.stack = stack;
		this.manager = manager;
		if (stack.getItem() instanceof BottledDrinkItem bottle) {
			this.bottle = bottle;
		} else {
			throw new IllegalArgumentException("Stack must be a drink bottle item");
		}
	}

	@Override
	public Drink getDrink() {
		return bottle.getDrink(manager);
	}

	@Override
	public int getVolume() {
		return bottle.getRemainingVolume(stack);
	}

	@Override
	public int tryPour(int quarterParts) {
		int ret = Math.min(quarterParts, getVolume());
		stack.getOrCreateNbt().putInt("amount_poured", stack.getOrCreateNbt().getInt("amount_poured") + ret);
		return ret;
	}
}
