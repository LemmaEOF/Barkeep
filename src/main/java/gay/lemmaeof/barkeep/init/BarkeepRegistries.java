package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.Drink;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class BarkeepRegistries {
	public static final RegistryKey<Registry<Drink>> DRINKS = RegistryKey.ofRegistry(new Identifier(Barkeep.MODID, "drinks"));

	public static void init () {
		DynamicRegistries.registerSynced(DRINKS, Drink.CODEC);
	}
}
