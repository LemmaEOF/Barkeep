package gay.lemmaeof.barkeep.data.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum CocktailPreparation implements StringIdentifiable {
	SHAKEN("shaken"),
	STIRRED("stirred"),
	DRY_SHAKEN("dry_shaken"),
	BLENDED("blended");

	public static final Codec<CocktailPreparation> CODEC = StringIdentifiable.createCodec(CocktailPreparation::values);

	private final String name;

	CocktailPreparation(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return name;
	}

	public static CocktailPreparation forName(String name) {
		for (CocktailPreparation prep : values()) {
			if (prep.name.equals(name.toLowerCase(Locale.ROOT))) return prep;
		}
		throw new IllegalArgumentException("No known preparation named " + name);
	}
}
