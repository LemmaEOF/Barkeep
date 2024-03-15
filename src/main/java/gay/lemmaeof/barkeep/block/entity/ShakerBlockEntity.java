package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ShakerBlockEntity extends BlockEntity {
	public ShakerBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.SHAKER_BE, pos, state);
	}
}
