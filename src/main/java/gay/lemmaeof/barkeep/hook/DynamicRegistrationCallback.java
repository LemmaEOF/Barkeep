package gay.lemmaeof.barkeep.hook;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.HashMap;
import java.util.Map;

//I promise I know what I'm doing. Trust me.
@SuppressWarnings({"unchecked", "rawtypes"})
@FunctionalInterface
public interface DynamicRegistrationCallback<T> {
	void onRegistration(MutableRegistry<T> registry);

	static Map<RegistryKey<Registry>, Event<DynamicRegistrationCallback>> EVENTS = new HashMap<>();

	static <T> Event<DynamicRegistrationCallback<T>> event(RegistryKey<Registry<T>> registry) {
		return EVENTS.computeIfAbsent((RegistryKey) registry, r -> EventFactory.createArrayBacked(
				DynamicRegistrationCallback.class,
				callbacks -> reg -> {
					for (DynamicRegistrationCallback callback : callbacks) {
						callback.onRegistration(reg);
					}
				})
		);
	}
}
