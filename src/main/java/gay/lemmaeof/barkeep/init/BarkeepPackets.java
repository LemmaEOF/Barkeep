package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeManager;
import gay.lemmaeof.barkeep.networking.SynchronizeCocktailsS2CPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class BarkeepPackets {
	public static void init() {
		PayloadTypeRegistry.playS2C().register(SynchronizeCocktailsS2CPacket.ID, SynchronizeCocktailsS2CPacket.CODEC);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> CocktailRecipeManager.INSTANCE.sendPacket(player));
	}
}
