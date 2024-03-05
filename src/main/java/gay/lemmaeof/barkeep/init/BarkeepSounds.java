package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class BarkeepSounds {
	public static final SoundEvent DRINK_POUR = register("drink_pour");
	public static final SoundEvent SHAKER_OPEN = register("shaker_open");
	public static final SoundEvent SHAKER_SHAKE = register("shaker_shake");

	public static void init() {}

	private static SoundEvent register(String name) {
		Identifier id = new Identifier(Barkeep.MODID, name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}
}
