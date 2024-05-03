package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.block.DrinkingBirdBlock;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO: client animation tick stuff
public class DrinkingBirdBlockEntity extends BlockEntity {
	private static final int MAX_TICK_PERIOD = 100;
	private int currentTicks = 0;

	public DrinkingBirdBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.DRINKING_BIRD_BE, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, DrinkingBirdBlockEntity be) {
		be.currentTicks++;
		if (be.currentTicks >= MAX_TICK_PERIOD) {
			BlockPos forwardPos = pos.offset(state.get(DrinkingBirdBlock.FACING));
			BlockState forwardState = world.getBlockState(forwardPos);
			if (forwardState.getBlock() instanceof ButtonBlock button && forwardState.get(WallMountedBlock.FACE) == BlockFace.FLOOR) {
				button.powerOn(forwardState, world, forwardPos);
			}
			be.currentTicks = 0;
		}
	}
}
