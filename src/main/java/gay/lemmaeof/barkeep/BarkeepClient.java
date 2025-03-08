package gay.lemmaeof.barkeep;

import gay.lemmaeof.barkeep.block.CocktailGlassBlock;
import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import gay.lemmaeof.barkeep.block.entity.CocktailGlassBlockEntity;
import gay.lemmaeof.barkeep.block.entity.JiggerCupBlockEntity;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.item.BottledDrinkItem;
import gay.lemmaeof.barkeep.item.CocktailGlassItem;
import gay.lemmaeof.barkeep.item.JiggerCupItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.Optional;

public class BarkeepClient implements ClientModInitializer {
	private static final Identifier FILLED_ID = Identifier.of(Barkeep.MODID, "filled");
	MinecraftClient mc = MinecraftClient.getInstance();

	public static int getDrinkColor(RegistryKey<Drink> drinkKey) {
		return ColorHelper.Argb.fullAlpha(MinecraftClient.getInstance().world.getRegistryManager().get(BarkeepRegistries.DRINKS).get(drinkKey).color().getRgb());
	}

	@Override
	public void onInitializeClient() {
		setupGlasses(
				BarkeepItems.ROCKS_GLASS,
				BarkeepItems.DOUBLE_ROCKS_GLASS,
				BarkeepItems.MARTINI_GLASS
		);
		setupJiggerCups(
				BarkeepItems.QUARTER_PART_JIGGER_CUP,
				BarkeepItems.HALF_PART_JIGGER_CUP,
				BarkeepItems.THREE_QUARTER_PART_JIGGER_CUP,
				BarkeepItems.PART_JIGGER_CUP,
				BarkeepItems.TWO_PART_JIGGER_CUP
		);
		//TODO: gonna need more of these yeah?
		setupDrinkBottles(
				BarkeepItems.DRINK_BOTTLE
		);
		ModelPredicateProviderRegistry.register(BarkeepItems.SHAKER, FILLED_ID,
				filled(BarkeepComponents.COCKTAIL));
	}

	private ClampedModelPredicateProvider filled(ComponentType<?> key) {
		return (stack, world, entity, seed) -> stack.contains(key)? 1 : 0;
	}

	private void setupGlasses(CocktailGlassItem... glasses) {
		CocktailGlassBlock[] blocks = new CocktailGlassBlock[glasses.length];
		for (int i = 0; i < glasses.length; i++) {
			CocktailGlassItem glass = glasses[i];
			blocks[i] = glass.getGlass();
			ModelPredicateProviderRegistry.register(glass, FILLED_ID,
					(stack, world, entity, seed) ->
							stack.getOrDefault(BarkeepComponents.COCKTAIL, CocktailComponent.EMPTY).cocktail().isPresent()? 1 : 0);
		}
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), blocks);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
					if (world == null) return 0xFFFFFFFF;
					if (tintIndex == 1 && world.getBlockEntity(pos) instanceof CocktailGlassBlockEntity glass) {
						if (glass.getCocktail() != null) return ColorHelper.Argb.fullAlpha(glass.getCocktail().getColor());
						return 0xFFFFFFFF;
					}
					return 0xFFFFFFFF;
				},
				blocks
		);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
					Optional<Cocktail> cocktail = stack.getOrDefault(BarkeepComponents.COCKTAIL, CocktailComponent.EMPTY).cocktail();
					if (tintIndex == 1 && cocktail.isPresent()) {
						return ColorHelper.Argb.fullAlpha(cocktail.get().getColor());
					}
					return 0xFFFFFFFF;
				},
				glasses
		);
	}

	private void setupJiggerCups(JiggerCupItem... cups) {
		JiggerCupBlock[] blocks = new JiggerCupBlock[cups.length];
		for (int i = 0; i < cups.length; i++) {
			JiggerCupItem cup = cups[i];
			blocks[i] = cup.getCup();
			ModelPredicateProviderRegistry.register(cup, FILLED_ID, filled(BarkeepComponents.DRINK_CONTAINER));
		}
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), blocks);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
					if (world == null) {
						return 0xFFFFFFFF;
					}
					if (tintIndex == 1 && world.getBlockEntity(pos) instanceof JiggerCupBlockEntity cup) {
						if (cup.getDrink() != null) return ColorHelper.Argb.fullAlpha(cup.getDrink().color().getRgb());
						return 0xFFFFFFFF;
					}
					return 0xFFFFFFFF;
				},
				blocks
		);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
					if (mc.world != null && stack.contains(BarkeepComponents.DRINK_CONTAINER) && tintIndex == 1) {
						Drink drink = mc.world.getRegistryManager().get(BarkeepRegistries.DRINKS).get(stack.get(BarkeepComponents.DRINK_CONTAINER).drink());
						if (drink != null) return ColorHelper.Argb.fullAlpha(drink.color().getRgb());
					}
					return 0xFFFFFFFF;
				},
				cups
		);
	}

	private void setupDrinkBottles(BottledDrinkItem... bottles) {
//		JiggerCupBlock[] blocks = new JiggerCupBlock[cups.length];
		for (int i = 0; i < bottles.length; i++) {
			BottledDrinkItem bottle = bottles[i];
//			blocks[i] = cup.getCup();
			ModelPredicateProviderRegistry.register(bottle, FILLED_ID, filled(BarkeepComponents.DRINK_CONTAINER));
		}
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
					if (mc.world != null && stack.contains(BarkeepComponents.DRINK_CONTAINER) && tintIndex == 0) {
						Drink drink = mc.world.getRegistryManager().get(BarkeepRegistries.DRINKS).get(stack.get(BarkeepComponents.DRINK_CONTAINER).drink());
						if (drink != null) return ColorHelper.Argb.fullAlpha(drink.color().getRgb());
					}
					return 0xFFFFFFFF;
				},
				bottles
		);
	}
}
