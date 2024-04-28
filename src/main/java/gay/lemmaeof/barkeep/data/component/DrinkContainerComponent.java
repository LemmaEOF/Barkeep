package gay.lemmaeof.barkeep.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.data.Drink;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.dynamic.Codecs;

public record DrinkContainerComponent(RegistryKey<Drink> drink, int amount) {
	public static final Codec<DrinkContainerComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Drink.REGISTRY_KEY_CODEC.fieldOf("drink").forGetter(DrinkContainerComponent::drink),
			Codecs.POSITIVE_INT.fieldOf("amount").forGetter(DrinkContainerComponent::amount)
	).apply(instance, DrinkContainerComponent::new));

	public DrinkContainerComponent withPoured(int poured) {
		return new DrinkContainerComponent(drink, amount-poured);
	}

	public DrinkContainerComponent withAmount(int amount) {
		return new DrinkContainerComponent(drink, amount);
	}
}
