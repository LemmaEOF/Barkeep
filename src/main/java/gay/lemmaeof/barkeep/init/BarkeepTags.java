package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BarkeepTags {
	public static final TagKey<Item> ICE = TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "ice"));
	public static final TagKey<Item> COCKTAIL_GLASSES = TagKey.of(RegistryKeys.ITEM, Identifier.of(Barkeep.MODID, "cocktail_glasses"));
	public static final TagKey<Item> GARNITURE = TagKey.of(RegistryKeys.ITEM, Identifier.of(Barkeep.MODID, "garniture"));


	public static void init() {}
}
