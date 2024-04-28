package gay.lemmaeof.barkeep.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.util.MoreCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Optional;

public record Drink(TextColor color, float colorStrength, int proof, List<FlavorNote> flavorNotes) {
	public static final Codec<Drink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TextColor.CODEC.fieldOf("color").orElse(TextColor.fromRgb(0xFFFFFF)).forGetter(Drink::color),
			MoreCodecs.NONNEGATIVE_FLOAT.fieldOf("color_strength").orElse(1f).forGetter(Drink::colorStrength),
			MoreCodecs.PROOF.fieldOf("proof").orElse(0).forGetter(Drink::proof),
			FlavorNote.CODEC.listOf().fieldOf("flavor_notes").forGetter(Drink::flavorNotes)
	).apply(instance, Drink::new));
	public static final Codec<RegistryKey<Drink>> REGISTRY_KEY_CODEC = RegistryKey.createCodec(BarkeepRegistries.DRINKS);
	public static final Codec<RegistryEntry<Drink>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(BarkeepRegistries.DRINKS, CODEC, false);

	public static RegistryKey<Drink> key(Identifier id) {
		return RegistryKey.of(BarkeepRegistries.DRINKS, id);
	}

	public static Optional<Drink> get(RegistryWrapper.WrapperLookup lookup, Identifier id) {
		Optional<RegistryWrapper.Impl<Drink>> registry = lookup.getOptionalWrapper(BarkeepRegistries.DRINKS);
		if (registry.isPresent()) {
			Optional<RegistryEntry.Reference<Drink>> drinkRef = registry.get().getOptional(key(id));
			if (drinkRef.isPresent()) return Optional.of(drinkRef.get().value());
		}
		return Optional.empty();
	}

	public Identifier getId(DynamicRegistryManager manager) {
		Identifier id = manager.get(BarkeepRegistries.DRINKS).getId(this);
		if (id == null) return new Identifier(Barkeep.MODID, "water");
		return id;
	}

	public String getTranslationKey(DynamicRegistryManager manager) {
		return getId(manager).toTranslationKey("drink");
	}
}
