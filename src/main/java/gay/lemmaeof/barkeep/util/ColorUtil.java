package gay.lemmaeof.barkeep.util;

import com.scrtwpns.Mixbox;
import net.minecraft.text.TextColor;

import java.util.Map;

public class ColorUtil {
	public static int getDrinkColor(Map<TextColor, Integer> colorWeights) {
		int colorVolume = 0;
		for (float weight : colorWeights.values()) {
			colorVolume += weight;
		}
		return getDrinkColor(colorWeights, colorVolume);
	}

	public static int getDrinkColor(Map<TextColor, Integer> colorWeights, int colorVolume) {
		float[] colorMix = new float[Mixbox.LATENT_SIZE];
		for (TextColor color : colorWeights.keySet()) {
			float[] latent = Mixbox.rgbToLatent(color.getRgb());
			float weight = colorWeights.get(color) / (float)colorVolume;
			for (int i = 0; i < colorMix.length; i++) {
				colorMix[i] += weight * latent[i];
			}
		}
		return Mixbox.latentToRgb(colorMix);
	}

}
