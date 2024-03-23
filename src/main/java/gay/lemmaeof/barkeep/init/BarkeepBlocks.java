package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.block.CocktailGlassBlock;
import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import gay.lemmaeof.barkeep.block.ShakerBlock;
import gay.lemmaeof.barkeep.block.entity.CocktailGlassBlockEntity;
import gay.lemmaeof.barkeep.block.entity.JiggerCupBlockEntity;
import gay.lemmaeof.barkeep.block.entity.ShakerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BarkeepBlocks {
	public static final ShakerBlock SHAKER = register("shaker", new ShakerBlock(FabricBlockSettings.create()));
	public static final JiggerCupBlock QUARTER_PART_JIGGER_CUP = register("quarter_part_jigger_cup", new JiggerCupBlock(1, jiggerCup()));
	public static final JiggerCupBlock HALF_PART_JIGGER_CUP = register("half_part_jigger_cup", new JiggerCupBlock(2, jiggerCup()));
	public static final JiggerCupBlock THREE_QUARTER_PART_JIGGER_CUP = register("three_quarter_part_jigger_cup", new JiggerCupBlock(3, jiggerCup()));
	public static final JiggerCupBlock PART_JIGGER_CUP = register("part_jigger_cup", new JiggerCupBlock(4, jiggerCup()));
	public static final JiggerCupBlock TWO_PART_JIGGER_CUP = register("two_part_jigger_cup", new JiggerCupBlock(8, jiggerCup()));
	public static final CocktailGlassBlock TEST_COCKTAIL_GLASS = register("test_cocktail_glass", new CocktailGlassBlock(
			FabricBlockSettings.create()
					.nonOpaque()
					.breakInstantly()
					.dynamicBounds()
					.offset(AbstractBlock.OffsetType.XZ)
	));

	public static final BlockEntityType<ShakerBlockEntity> SHAKER_BE = register("shaker", ShakerBlockEntity::new, SHAKER);

	public static final BlockEntityType<JiggerCupBlockEntity> JIGGER_CUP_BE = register("jigger_cup", JiggerCupBlockEntity::new,
			QUARTER_PART_JIGGER_CUP,
			HALF_PART_JIGGER_CUP,
			THREE_QUARTER_PART_JIGGER_CUP,
			PART_JIGGER_CUP,
			TWO_PART_JIGGER_CUP)
			;

	public static final BlockEntityType<CocktailGlassBlockEntity> COCKTAIL_GLASS_BE = register("cocktail_glass", CocktailGlassBlockEntity::new, TEST_COCKTAIL_GLASS);

	public static void init() {

	}

	private static <T extends Block> T register(String name, T block) {
		return Registry.register(Registries.BLOCK, new Identifier(Barkeep.MODID, name), block);
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<T> factory, Block... blocks) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Barkeep.MODID, name), BlockEntityType.Builder.create(factory, blocks).build(null));
	}

	private static AbstractBlock.Settings jiggerCup() {
		//TODO: sounds
		return FabricBlockSettings.create()
				.breakInstantly()
				.nonOpaque()
				.offset(AbstractBlock.OffsetType.XZ)
				.dynamicBounds();
	}
}
