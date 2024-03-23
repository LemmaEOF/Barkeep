package gay.lemmaeof.barkeep.block;

import gay.lemmaeof.barkeep.block.entity.JiggerCupBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class JiggerCupBlock extends Block implements BlockEntityProvider {
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
}
