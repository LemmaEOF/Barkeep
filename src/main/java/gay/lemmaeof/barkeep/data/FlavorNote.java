package gay.lemmaeof.barkeep.data;


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: More flavor notes? Aromatic?
public enum FlavorNote implements StringIdentifiable {
	SWEET("sweet", StatusEffects.SPEED),
	SOUR("sour", StatusEffects.REGENERATION),
	BITTER("bitter", StatusEffects.NIGHT_VISION),
	SALTY("salty", StatusEffects.HASTE),
	SPICY("spicy", StatusEffects.FIRE_RESISTANCE),
	UMAMI("umami", StatusEffects.SATURATION),
	DRY("dry", StatusEffects.JUMP_BOOST),
	SPARKLING("sparkling", StatusEffects.WATER_BREATHING);

	public static final com.mojang.serialization.Codec<FlavorNote> CODEC = StringIdentifiable.createCodec(FlavorNote::values);
	private static final Map<String, FlavorNote> BY_NAME = Arrays.stream(values())
			.collect(Collectors.toMap(f -> sanitize(f.name), f -> f));
	private final String name;
	private final StatusEffect effect;

	private static String sanitize(String name) {
		return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}

	FlavorNote(String name, StatusEffect effect) {
		this.name = name;
		this.effect = effect;
	}

	@Override
	public String asString() {
		return name;
	}

	public StatusEffect getEffect() {
		return effect;
	}

	@Nullable
	public static FlavorNote byName(@Nullable String name) {
		return name == null ? null : BY_NAME.get(sanitize(name));
	}
}
