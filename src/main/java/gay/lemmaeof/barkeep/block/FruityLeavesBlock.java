package gay.lemmaeof.barkeep.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class FruityLeavesBlock extends LeavesBlock {
	private final Block fruit;

	public FruityLeavesBlock(Settings settings, Block fruit) {
		super(settings);
		this.fruit = fruit;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		if (world.getBlockState(pos) == state) {
			if (random.nextInt(3) == 0 && world.getBlockState(pos.down()).isAir()) {
				world.setBlockState(pos.down(), fruit.getDefaultState());
			}
		}
	}
}
