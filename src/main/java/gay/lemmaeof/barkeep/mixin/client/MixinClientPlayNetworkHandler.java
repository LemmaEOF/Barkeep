package gay.lemmaeof.barkeep.mixin.client;

import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Inject(method="<init>", at = @At("RETURN"))
	public void captureDynamicRegistries(MinecraftClient client, ClientConnection connection, ClientConnectionState state, CallbackInfo info) {
		CocktailRecipeManager.CLIENT_INSTANCE = new CocktailRecipeManager(state.receivedRegistries());
	}

}
