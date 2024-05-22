package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.FlavorNote;
import gay.lemmaeof.barkeep.hook.DynamicRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.List;

public class BarkeepRegistries {
	public static final RegistryKey<Registry<Drink>> DRINKS = RegistryKey.ofRegistry(new Identifier(Barkeep.MODID, "drinks"));

	public static void init () {
		DynamicRegistries.registerSynced(DRINKS, Drink.CODEC);
		DynamicRegistrationCallback.event(BarkeepRegistries.DRINKS).register(registry -> {
			for (Identifier id : Registries.POTION.getIds()) {
				if (Registries.POTION.get(id) == Potions.WATER.value()) {
					Registry.register(registry, new Identifier("water"), new Drink(TextColor.fromRgb(0x385DC6), 0, 0, List.of(), List.of()));
				} else {
					Registry.register(registry, potionId(id), fromPotion(Registries.POTION.get(id)));
				}
			}
		});
	}

	private static Drink fromPotion(Potion potion) {
		//TODO: flavor notes of potions
		return new Drink(
				TextColor.fromRgb(PotionContentsComponent.getColor(potion.getEffects())),
				1,
				0,
				List.of(FlavorNote.DRY),
				potion.getEffects().stream().map(eff -> new StatusEffectInstance(
						eff.getEffectType(),
						eff.getDuration() / 100,
						eff.getAmplifier())
				).toList()
		);
	}

	private static Identifier potionId(Identifier old) {
		return new Identifier(old.getNamespace(), old.getPath() + "_potion");
	}
}
