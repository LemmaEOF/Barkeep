package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.item.ShakerItem;
import gay.lemmaeof.barkeep.item.CocktailItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BarkeepItems {
	public static final Item TEST_COCKTAIL = register("test_cocktail", new CocktailItem(new Item.Settings()));
	public static final Item SHAKER = register("shaker", new ShakerItem(new Item.Settings().maxCount(1)));

	public static final ItemGroup EQUIPMENT = Registry.register(Registries.ITEM_GROUP, new Identifier(Barkeep.MODID, "equipment"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.barkeep.equipment"))
			.icon(() -> new ItemStack(SHAKER))
			.entries((context, entries) -> {
				entries.add(SHAKER);
			})
			.build());

	public static void init() {}

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Barkeep.MODID, name), item);
	}
}
