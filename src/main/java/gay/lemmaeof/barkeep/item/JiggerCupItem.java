package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JiggerCupItem extends SneakyBlockItem {
	private final int size;
	public JiggerCupItem(JiggerCupBlock block, Settings settings) {
		super(block, settings);
		this.size = block.getSize();
	}

	public int getSize() {
		return size;
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		return super.postPlacement(pos, world, player, stack, state);
	}
}
