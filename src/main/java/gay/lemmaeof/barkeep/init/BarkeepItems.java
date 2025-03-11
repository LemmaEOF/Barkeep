package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeManager;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.impl.BottleDrinkContainer;
import gay.lemmaeof.barkeep.impl.JiggerCupDrinkContainer;
import gay.lemmaeof.barkeep.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BarkeepItems {
	//cocktail glasses!
	public static final CocktailGlassItem ROCKS_GLASS = register("rocks_glass", new CocktailGlassItem(BarkeepBlocks.ROCKS_GLASS, glass()));
	public static final CocktailGlassItem DOUBLE_ROCKS_GLASS = register("double_rocks_glass", new CocktailGlassItem(BarkeepBlocks.DOUBLE_ROCKS_GLASS, glass()));
	public static final CocktailGlassItem MARGARITA_GLASS = register("margarita_glass", new CocktailGlassItem(BarkeepBlocks.MARGARITA_GLASS, glass()));
	public static final CocktailGlassItem COUPE_GLASS = register("coupe_glass", new CocktailGlassItem(BarkeepBlocks.COUPE_GLASS, glass()));

	//mixing equipment!
	public static final ShakerItem SHAKER = register("shaker", new ShakerItem(BarkeepBlocks.SHAKER, new Item.Settings().maxCount(1)));

	//jigger cups!
	public static final JiggerCupItem QUARTER_PART_JIGGER_CUP = register("quarter_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.QUARTER_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem HALF_PART_JIGGER_CUP = register("half_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.HALF_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem THREE_QUARTER_PART_JIGGER_CUP = register("three_quarter_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.THREE_QUARTER_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem PART_JIGGER_CUP = register("part_jigger_cup", new JiggerCupItem(BarkeepBlocks.PART_JIGGER_CUP, new Item.Settings().maxCount(1)));
	public static final JiggerCupItem TWO_PART_JIGGER_CUP = register("two_part_jigger_cup", new JiggerCupItem(BarkeepBlocks.TWO_PART_JIGGER_CUP, new Item.Settings().maxCount(1)));

	//bottled drinks!
	public static final BottledDrinkItem DRINK_BOTTLE = register("drink_bottle", new BottledDrinkItem(100, new Item.Settings().maxCount(1)));
	public static final FixedBottledDrinkItem BOTTLED_AMARO_NONINO = register("bottled_amaro_nonino", new FixedBottledDrinkItem(Drink.key(Identifier.of(Barkeep.MODID, "amaro_nonino")), 104, new Item.Settings().maxCount(1)));
	public static final FixedBottledDrinkItem BOTTLED_APEROL = register("bottled_aperol", new FixedBottledDrinkItem(Drink.key(Identifier.of(Barkeep.MODID, "aperol")), 104, new Item.Settings().maxCount(1)));
	public static final FixedBottledDrinkItem BOTTLED_BOURBON = register("bottled_bourbon", new FixedBottledDrinkItem(Drink.key(Identifier.of(Barkeep.MODID, "bourbon")), 104, new Item.Settings().maxCount(1)));
	public static final FixedBottledDrinkItem BOTTLED_LEMON_JUICE = register("bottled_lemon_juice", new FixedBottledDrinkItem(Drink.key(Identifier.of(Barkeep.MODID, "lemon_juice")), 27, new Item.Settings().maxCount(1)));

	//ingredients!
	//TODO: should these fruits be blocks/blockitems?
	public static final Item LEMON = register("lemon", new Item(new Item.Settings()));
	public static final Item LIME = register("lime", new Item(new Item.Settings()));
	public static final Item ORANGE = register("orange", new Item(new Item.Settings()));
	public static final Item CHERRY = register("cherry", new Item(new Item.Settings()));


	public static final ItemGroup EQUIPMENT = Registry.register(Registries.ITEM_GROUP, Identifier.of(Barkeep.MODID, "equipment"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.barkeep.equipment"))
			.icon(() -> new ItemStack(SHAKER))
			.entries((context, entries) -> {
				entries.add(SHAKER);
				entries.add(QUARTER_PART_JIGGER_CUP);
				entries.add(HALF_PART_JIGGER_CUP);
				entries.add(THREE_QUARTER_PART_JIGGER_CUP);
				entries.add(PART_JIGGER_CUP);
				entries.add(TWO_PART_JIGGER_CUP);
				entries.add(BOTTLED_AMARO_NONINO);
				entries.add(BOTTLED_APEROL);
				entries.add(BOTTLED_BOURBON);
				entries.add(BOTTLED_LEMON_JUICE);
				entries.add(ROCKS_GLASS);
				entries.add(DOUBLE_ROCKS_GLASS);
				entries.add(MARGARITA_GLASS);
				entries.add(COUPE_GLASS);
				entries.add(BarkeepBlocks.DRINKING_BIRD);
			})
			.build());

	//TODO: make this a martini or such later!
	public static final ItemGroup COCKTAILS = Registry.register(Registries.ITEM_GROUP, Identifier.of(Barkeep.MODID, "cocktails"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.barkeep.cocktails"))
			.icon(() -> new ItemStack(ROCKS_GLASS))
			.entries((context, entries) -> {
				for (Identifier id : CocktailRecipeManager.INSTANCE.getCocktailIds()) {
					entries.add(CocktailRecipeManager.INSTANCE.getSampleCocktail(id));
				}
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
		DrinkContainer.ITEM_LOOKUP.registerForItems(BottleDrinkContainer::new,
				DRINK_BOTTLE,
				BOTTLED_AMARO_NONINO,
				BOTTLED_APEROL,
				BOTTLED_BOURBON,
				BOTTLED_LEMON_JUICE
		);
	}

	private static Item.Settings glass() {
		return new Item.Settings().component(BarkeepComponents.COCKTAIL, CocktailComponent.EMPTY);
	}

	private static <T extends Item> T register(String name, T item) {
		return Registry.register(Registries.ITEM, Identifier.of(Barkeep.MODID, name), item);
	}
}
