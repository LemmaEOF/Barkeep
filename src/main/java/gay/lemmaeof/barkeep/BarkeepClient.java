package gay.lemmaeof.barkeep;

import gay.lemmaeof.barkeep.data.CocktailManager;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class BarkeepClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 0 && CocktailManager.INSTANCE.hasCocktail(stack)) {
				return CocktailManager.INSTANCE.getCocktailColor(stack);
			}
			return 0xFFFFFF;
		}, BarkeepItems.TEST_COCKTAIL);
	}
}
