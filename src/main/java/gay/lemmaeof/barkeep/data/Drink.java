package gay.lemmaeof.barkeep.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.TextColor;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Optional;

public record Drink(Optional<TextColor> color, int proof, List<FlavorNote> flavorNotes) {
	public static final Codec<Integer> PROOF = Codecs.rangedInt(0, 200);
	public static final Codec<Optional<TextColor>> DRINK_COLOR = Codec.STRING.comapFlatMap(color -> {
		if (color.equals("clear")) return DataResult.success(Optional.empty());
		TextColor textColor = TextColor.parse(color);
		return textColor != null ? DataResult.success(java.util.Optional.of(textColor)) : DataResult.error(() -> "String is not a valid color name or hex color code");
	}, color -> {
		if (color.isEmpty()) return "clear";
		else return color.get().getName();
	});
	public static final Codec<Drink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DRINK_COLOR.fieldOf("color").orElse(Optional.empty()).forGetter(Drink::color),
			PROOF.fieldOf("proof").orElse(0).forGetter(Drink::proof),
			FlavorNote.CODEC.listOf().fieldOf("flavor_notes").forGetter(Drink::flavorNotes)
	).apply(instance, Drink::new));
	public static final Codec<RegistryEntry<Drink>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(BarkeepRegistries.DRINKS, CODEC);
}