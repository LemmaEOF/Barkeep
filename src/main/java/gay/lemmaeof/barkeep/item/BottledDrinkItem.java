package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public class BottledDrinkItem extends Item {
	private final int maxCapacity;

	public BottledDrinkItem(int maxCapacity, Settings settings) {
		super(settings);
		this.maxCapacity = maxCapacity;
	}

	public Drink getDrink(ItemStack stack, DynamicRegistryManager manager) {
		if (!stack.contains(BarkeepComponents.DRINK_CONTAINER)) return null;
		return manager.get(BarkeepRegistries.DRINKS).get(stack.get(BarkeepComponents.DRINK_CONTAINER).drink());
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public int getRemainingVolume(ItemStack stack) {
		if (!stack.contains(BarkeepComponents.DRINK_CONTAINER)) return 0;
		return stack.get(BarkeepComponents.DRINK_CONTAINER).amount();
	}

	public int pour(ItemStack stack, int amount) {
		if (getRemainingVolume(stack) == amount) {
			stack.remove(BarkeepComponents.DRINK_CONTAINER);
		} else {
			stack.set(BarkeepComponents.DRINK_CONTAINER, stack.get(BarkeepComponents.DRINK_CONTAINER).withPoured(amount));
		}
		return amount;
	}

	@Override
	public Text getName(ItemStack stack) {
		if (!stack.contains(BarkeepComponents.DRINK_CONTAINER)) return super.getName();
		RegistryKey<Drink> drink = stack.get(BarkeepComponents.DRINK_CONTAINER).drink();
		return Text.translatable("item.barkeep.bottled_drink", Text.translatable(drink.getValue().toTranslationKey("drink")));
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return getRemainingVolume(stack) != getMaxCapacity();
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round((float)getRemainingVolume(stack) * 13.0F / (float)getMaxCapacity());
	}

	//TODO: item bar color based on the drink - difficult due to needing to access the dynamic registry manager and client-side stuff
}
