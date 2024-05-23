package gay.lemmaeof.barkeep.util;

import com.scrtwpns.Mixbox;
import gay.lemmaeof.barkeep.BarkeepClient;
import gay.lemmaeof.barkeep.data.Drink;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.TextColor;

import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ColorUtil {
	private static final Supplier<ToIntFunction<RegistryKey<Drink>>> CLIENT_COLOR_GETTER = () -> {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return BarkeepClient::getDrinkColor;
		}
		return key -> 0xFFFFFF;
	};

	public static int getMixedColor(Map<TextColor, Float> colorWeights) {
		float colorVolume = 0;
		for (float weight : colorWeights.values()) {
			colorVolume += weight;
		}
		return getMixedColor(colorWeights, colorVolume);
	}

	public static int getMixedColor(Map<TextColor, Float> colorWeights, float colorVolume) {
		if (colorVolume == 0) return 0xFFFFFF;
		float[] colorMix = new float[Mixbox.LATENT_SIZE];
		for (TextColor color : colorWeights.keySet()) {
			float[] latent = Mixbox.rgbToLatent(color.getRgb());
			float weight = colorWeights.get(color) / colorVolume;
			for (int i = 0; i < colorMix.length; i++) {
				colorMix[i] += weight * latent[i];
			}
		}
		return Mixbox.latentToRgb(colorMix);
	}

	public static int getClientDrinkColor(RegistryKey<Drink> drinkKey) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return CLIENT_COLOR_GETTER.get().applyAsInt(drinkKey);
		}
		return 0xFFFFFF;
	}
}
