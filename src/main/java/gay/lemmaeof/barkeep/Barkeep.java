package gay.lemmaeof.barkeep;

import gay.lemmaeof.barkeep.init.*;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Barkeep implements ModInitializer {
	public static final String MODID = "barkeep";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		BarkeepBlocks.init();
		BarkeepItems.init();
		BarkeepRegistries.init();
		BarkeepSounds.init();
		BarkeepTags.init();
	}
}