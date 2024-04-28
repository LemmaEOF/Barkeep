package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public class BottledDrinkItem extends Item {
	private final RegistryKey<Drink> drink;
	private final int maxCapacity;

	public BottledDrinkItem(RegistryKey<Drink> drink, int maxCapacity, Settings settings) {
		super(settings.component(BarkeepComponents.DRINK_CONTAINER, new DrinkContainerComponent(drink, maxCapacity)));
		this.drink = drink;
		this.maxCapacity = maxCapacity;
	}

	public Drink getDrink(DynamicRegistryManager manager) {
		return manager.get(BarkeepRegistries.DRINKS).get(drink);
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
