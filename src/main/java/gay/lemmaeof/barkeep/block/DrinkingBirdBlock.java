package gay.lemmaeof.barkeep.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import gay.lemmaeof.barkeep.block.entity.DrinkingBirdBlockEntity;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DrinkingBirdBlock extends BlockWithEntity {
	private static final MapCodec<DrinkingBirdBlock> CODEC = createCodec(DrinkingBirdBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public DrinkingBirdBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DrinkingBirdBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, BarkeepBlocks.DRINKING_BIRD_BE, DrinkingBirdBlockEntity::tick);
	}
}
