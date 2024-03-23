package gay.lemmaeof.barkeep.impl;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.item.JiggerCupItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

public class JiggerCupDrinkContainer implements DrinkContainer {
	private final ItemStack stack;
	private final DynamicRegistryManager manager;
	private final int size;

	public JiggerCupDrinkContainer(ItemStack stack, DynamicRegistryManager manager) {
		this.stack = stack;
		this.manager = manager;
		if (this.stack.getItem() instanceof JiggerCupItem jigger) {
			this.size = jigger.getSize();
		} else {
			throw new IllegalArgumentException("Stack must be a jigger cup item");
		}
	}

	@Override
	public Drink getDrink() {
		if (!stack.hasNbt() || !stack.getNbt().contains("drink", NbtElement.STRING_TYPE)) return null;
		return manager.get(BarkeepRegistries.DRINKS).get(new Identifier(stack.getNbt().getString("drink")));
	}

	@Override
	public int getVolume() {
		return getDrink() != null? size : 0;
	}

	@Override
	public int tryPour(int quarterParts) {
		if (getDrink() == null) return 0;
		stack.getOrCreateNbt().remove("drink");
		return size;
	}
}
