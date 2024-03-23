package gay.lemmaeof.barkeep;

import gay.lemmaeof.barkeep.block.entity.CocktailGlassBlockEntity;
import gay.lemmaeof.barkeep.data.CocktailManager;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class BarkeepClient implements ClientModInitializer {
	private static final Identifier FILLED_ID = new Identifier(Barkeep.MODID, "filled");
	MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(BarkeepBlocks.TEST_COCKTAIL_GLASS, RenderLayer.getTranslucent());
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			if (tintIndex == 1 && world.getBlockEntity(pos) instanceof CocktailGlassBlockEntity glass) {
				if (glass.getCocktail() != null) return glass.getCocktail().getColor();
				return 0xFFFFFF;
			}
			return 0xFFFFFF;
		}, BarkeepBlocks.TEST_COCKTAIL_GLASS);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 1 && CocktailManager.INSTANCE.hasCocktail(stack)) {
				return CocktailManager.INSTANCE.getCocktailColor(stack);
			}
			return 0xFFFFFF;
		}, BarkeepItems.TEST_COCKTAIL);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (mc.world != null && stack.hasNbt() && stack.getNbt().contains("drink", NbtElement.STRING_TYPE) && tintIndex == 1) {
				Drink drink = mc.world.getRegistryManager().get(BarkeepRegistries.DRINKS).get(new Identifier(stack.getOrCreateNbt().getString("drink")));
				if (drink != null) return drink.color().getRgb();
			}
			return 0xFFFFFF;
		},
				BarkeepItems.QUARTER_PART_JIGGER_CUP,
				BarkeepItems.HALF_PART_JIGGER_CUP,
				BarkeepItems.THREE_QUARTER_PART_JIGGER_CUP,
				BarkeepItems.PART_JIGGER_CUP,
				BarkeepItems.TWO_PART_JIGGER_CUP
		);
		ModelPredicateProviderRegistry.register(BarkeepItems.TEST_COCKTAIL, FILLED_ID, filled("cocktail"));
		ModelPredicateProviderRegistry.register(BarkeepItems.QUARTER_PART_JIGGER_CUP, FILLED_ID, filled("drink"));
		ModelPredicateProviderRegistry.register(BarkeepItems.HALF_PART_JIGGER_CUP, FILLED_ID, filled("drink"));
		ModelPredicateProviderRegistry.register(BarkeepItems.THREE_QUARTER_PART_JIGGER_CUP, FILLED_ID, filled("drink"));
		ModelPredicateProviderRegistry.register(BarkeepItems.PART_JIGGER_CUP, FILLED_ID, filled("drink"));
		ModelPredicateProviderRegistry.register(BarkeepItems.TWO_PART_JIGGER_CUP, FILLED_ID, filled("drink"));
	}

	private ClampedModelPredicateProvider filled(String key) {
		return (stack, world, entity, seed) -> stack.getOrCreateNbt().contains(key)? 1 : 0;
	}
}
