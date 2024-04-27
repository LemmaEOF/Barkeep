package gay.lemmaeof.barkeep.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.dynamic.Codecs;

public class MoreCodecs {
	public static final Codec<Float> NONNEGATIVE_FLOAT = Codec.FLOAT.validate(
			value -> value >= 0 ? DataResult.success(value) : DataResult.error(() -> "Value must be non-negative: " + value)
	);
	public static final Codec<Integer> PROOF = Codecs.rangedInt(0, 200);
	public static final Codec<Integer> PARTS = NONNEGATIVE_FLOAT.xmap(f -> (int) Math.floor(f * 4), i -> i / (float) 4);
}
