package gay.lemmaeof.barkeep.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

//TODO: muddling
public record MixerContentsComponent(Map<RegistryEntry<Drink>, Integer> drinks, boolean iced) implements TooltipAppender {
	public static final MixerContentsComponent EMPTY = new MixerContentsComponent(Map.of(), false);
	public static final Codec<MixerContentsComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codecs.strictUnboundedMap(Drink.REGISTRY_ENTRY_CODEC, Codecs.POSITIVE_INT).fieldOf("drinks").forGetter(MixerContentsComponent::drinks),
			Codec.BOOL.fieldOf("iced").forGetter(MixerContentsComponent::iced)
	).apply(instance, MixerContentsComponent::new));

	public Map<Drink, Integer> getDrinks() {
		Map<Drink, Integer> ret = new HashMap<>();
		for (RegistryEntry<Drink> drink : drinks.keySet()) {
			ret.put(drink.value(), drinks.get(drink));
		}
		return ret;
	}

	public static MixerContentsComponent empty() {
		return new MixerContentsComponent(new HashMap<>(), false);
	}

	public MixerContentsComponent withDrink(Drink drink, int quarters, DynamicRegistryManager manager) {
		Map<RegistryEntry<Drink>, Integer> map = new HashMap<>(drinks());
		RegistryEntry<Drink> e = manager.get(BarkeepRegistries.DRINKS).getEntry(drink);
		map.put(e, map.getOrDefault(e, 0) + quarters);
		return new MixerContentsComponent(map, iced());
	}

	public MixerContentsComponent withIce(boolean iced) {
		return new MixerContentsComponent(drinks(), iced);
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
	}
}
