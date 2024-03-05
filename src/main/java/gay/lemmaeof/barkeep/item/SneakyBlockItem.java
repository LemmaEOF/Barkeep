package gay.lemmaeof.barkeep.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class SneakyBlockItem extends BlockItem {
	public SneakyBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getPlayer() == null || !context.getPlayer().isSneaking()) return ActionResult.PASS;
		return super.useOnBlock(context);
	}
}
