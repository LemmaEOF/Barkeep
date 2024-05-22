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

public class FixedBottledDrinkItem extends BottledDrinkItem {
	private final RegistryKey<Drink> drink;

	public FixedBottledDrinkItem(RegistryKey<Drink> drink, int maxCapacity, Settings settings) {
		super(maxCapacity, settings.component(BarkeepComponents.DRINK_CONTAINER, new DrinkContainerComponent(drink, maxCapacity)));
		this.drink = drink;
	}

	@Override
	public Drink getDrink(ItemStack stack, DynamicRegistryManager manager) {
		return manager.get(BarkeepRegistries.DRINKS).get(drink);
	}

	@Override
	public Text getName(ItemStack stack) {
		return Text.translatable("item.barkeep.bottled_drink", Text.translatable(drink.getValue().toTranslationKey("drink")));
	}

}
