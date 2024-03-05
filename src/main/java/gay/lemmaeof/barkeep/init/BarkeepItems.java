package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.impl.JiggerCupDrinkContainer;
import gay.lemmaeof.barkeep.item.JiggerCupItem;
import gay.lemmaeof.barkeep.item.ShakerItem;
import gay.lemmaeof.barkeep.item.CocktailItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BarkeepItems {
	public static final Item TEST_COCKTAIL = register("test_cocktail", new CocktailItem(new Item.Settings()));
	public static final Item SHAKER = register("shaker", new ShakerItem(BarkeepBlocks.SHAKER, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem QUARTER_PART_JIGGER_CUP = register("quarter_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.QUARTER_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem HALF_PART_JIGGER_CUP = register("half_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.HALF_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem THREE_QUARTER_PART_JIGGER_CUP = register("three_quarter_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.THREE_QUARTER_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem PART_JIGGER_CUP = register("part_jigger_cup", new JiggerCupItem(BarkeepBlocks.PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem TWO_PART_JIGGER_CUP = register("two_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.TWO_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));

	public static final ItemGroup EQUIPMENT = Registry.register(Registries.ITEM_GROUP, new Identifier(Barkeep.MODID, "equipment"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.barkeep.equipment"))
			.icon(() -> new ItemStack(SHAKER))
			.entries((context, entries) -> {
				entries.add(SHAKER);
				entries.add(QUARTER_PART_JIGGER_CUP);
				entries.add(HALF_PART_JIGGER_CUP);
				entries.add(THREE_QUARTER_PART_JIGGER_CUP);
				entries.add(PART_JIGGER_CUP);
				entries.add(TWO_PART_JIGGER_CUP);
			})
			.build());

	//TODO: impl
	public static final ItemGroup COCKTAILS = Registry.register(Registries.ITEM_GROUP, new Identifier(Barkeep.MODID, "cocktails"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.barkeep.cocktails"))
			.icon(() -> new ItemStack(TEST_COCKTAIL))
			.entries((context, entries) -> {

			})
			.build());

	public static void init() {
		DrinkContainer.ITEM_LOOKUP.registerForItems(JiggerCupDrinkContainer::new,
				QUARTER_PART_JIGGER_CUP,
				HALF_PART_JIGGER_CUP,
				THREE_QUARTER_PART_JIGGER_CUP,
				PART_JIGGER_CUP,
				TWO_PART_JIGGER_CUP
		);
		DrinkContainer.ITEM_LOOKUP.registerForItems((stack, manager) -> new DrinkContainer() {
			@Override
			public Drink getDrink() {
				return manager.get(BarkeepRegistries.DRINKS).get(new Identifier(Barkeep.MODID, "water"));
			}

			@Override
			public int getVolume() {
				return 12;
			}

			@Override
			public int tryPour(int quarterParts) {
				return Math.min(quarterParts, getVolume());
			}
		}, Items.WATER_BUCKET);
	}

	private static <T extends Item> T register(String name, T item) {
		return Registry.register(Registries.ITEM, new Identifier(Barkeep.MODID, name), item);
	}
}
