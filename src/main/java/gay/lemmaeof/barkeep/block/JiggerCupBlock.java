package gay.lemmaeof.barkeep.block;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.entity.JiggerCupBlockEntity;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JiggerCupBlock extends Block implements BlockEntityProvider {
	public static final BooleanProperty FILLED = BooleanProperty.of("filled");
	public static final VoxelShape SHAPE = Block.createCuboidShape(6, 0, 6, 10, 8, 10);

	private final int size;
	public JiggerCupBlock(int size, Settings settings) {
		super(settings);
		this.size = size;
		this.setDefaultState(this.getStateManager().getDefaultState().with(FILLED, false));
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FILLED);
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof JiggerCupBlockEntity cup) {
			DrinkContainer lookup = DrinkContainer.ITEM_LOOKUP.find(stack, world.getRegistryManager());
			if (lookup != null) {
				if (cup.getDrink() == null && lookup.getVolume() >= getSize()) {
					//TODO: sound
					Drink drink = lookup.getDrink();
					lookup.tryPour(getSize());
					cup.setComponent(new DrinkContainerComponent(world.getRegistryManager().get(BarkeepRegistries.DRINKS).getKey(drink).orElseThrow(), getSize()));
					world.setBlockState(pos, state.with(FILLED, true));
					return ItemActionResult.SUCCESS;
				}
			} else if (stack.isEmpty() && player.isSneaking() && cup.getDrink() != null && player.canModifyAt(world, pos)) {
				ItemStack giveStack = new ItemStack(this.asItem());
				giveStack.set(BarkeepComponents.DRINK_CONTAINER, cup.getComponent());
				player.setStackInHand(hand, giveStack);
				world.removeBlock(pos, false);
				return ItemActionResult.SUCCESS;
			}
		}
		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
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
