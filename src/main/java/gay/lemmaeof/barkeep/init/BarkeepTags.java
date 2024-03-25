package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BarkeepTags {
	public static final TagKey<Item> ICE = TagKey.of(RegistryKeys.ITEM, new Identifier(Barkeep.MODID, "ice"));

	public static void init() {}
}
