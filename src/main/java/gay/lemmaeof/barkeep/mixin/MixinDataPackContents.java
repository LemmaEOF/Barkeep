package gay.lemmaeof.barkeep.mixin;

import gay.lemmaeof.barkeep.data.CocktailManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public class MixinDataPackContents {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void createCocktailManager(DynamicRegistryManager.Immutable registryManager, FeatureSet enabledFeatures,
									   CommandManager.RegistrationEnvironment environment, int functionPermissionLevel,
									   CallbackInfo info) {
		CocktailManager.register(registryManager);
	}
}
