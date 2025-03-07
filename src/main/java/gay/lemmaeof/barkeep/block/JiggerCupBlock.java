package gay.lemmaeof.barkeep.block;

import gay.lemmaeof.barkeep.block.entity.JiggerCupBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

//TODO: filled property
public class JiggerCupBlock extends Block implements BlockEntityProvider {
	public static final VoxelShape SHAPE = Block.createCuboidShape(5.5, 0, 5.5, 11, 7, 11);

	private final int size;
	public JiggerCupBlock(int size, Settings settings) {
		super(settings);
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new JiggerCupBlockEntity(pos, state);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d offset = state.getModelOffset(world, pos);
		return SHAPE.offset(offset.x, offset.y, offset.z);
	}

	@Override
	protected float getMaxHorizontalModelOffset() {
		return 0.1f;
	}
}
