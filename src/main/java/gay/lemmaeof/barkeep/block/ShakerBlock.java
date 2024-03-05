package gay.lemmaeof.barkeep.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ShakerBlock extends Block implements BlockEntityProvider {
	public ShakerBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return null;
	}
}
