package gay.lemmaeof.barkeep.block;

import gay.lemmaeof.barkeep.block.entity.CocktailGlassBlockEntity;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import gay.lemmaeof.barkeep.init.BarkeepTags;
import net.minecraft.block.*;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CocktailGlassBlock extends Block implements BlockEntityProvider {
	//TODO: BER instead of block model? would let me do fancy animations and variable drink sizes, and we need one anyway for garniture
	//TODO: oh if it's a BER I should make shaken drinks more opaque bc they're aerated
	public static final BooleanProperty FILLED = BooleanProperty.of("filled");
	//TODO: change based on the glass, probably just pass to ctor
	public static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 8, 11);

	private final int capacity;

	//TODO: make cup capacity real?
	public CocktailGlassBlock(int capacity, Settings settings) {
		super(settings);
		this.capacity = capacity;
		this.setDefaultState(this.getStateManager().getDefaultState().with(FILLED, false));
	}

	public int getCapacity() {
		return this.capacity;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CocktailGlassBlockEntity(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FILLED);
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CocktailGlassBlockEntity glass) {
			if (stack.isOf(BarkeepItems.SHAKER) && glass.getCocktail() == null) {
				CocktailComponent cocktail = stack.getOrDefault(BarkeepComponents.COCKTAIL, CocktailComponent.EMPTY);
				if (cocktail.cocktail().isPresent()) {
					//TODO: sound
					glass.setCocktail(cocktail.cocktail().get());
					world.setBlockState(pos, state.with(FILLED, true));
					stack.remove(BarkeepComponents.COCKTAIL);
					return ItemActionResult.SUCCESS;
				}
			} else if (stack.isIn(BarkeepTags.GARNITURE)) {
				glass.addGarnish(stack.split(1));
				return ItemActionResult.SUCCESS;
			} else if (stack.isEmpty() && player.isSneaking() && glass.getCocktail() != null) {
				ItemStack giveStack = new ItemStack(this.asItem());
				giveStack.set(BarkeepComponents.COCKTAIL, glass.getCocktailComponent());
				player.setStackInHand(hand, giveStack);
				world.removeBlock(pos, false);
				return ItemActionResult.SUCCESS;
			}
		}
		return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d offset = state.getModelOffset(world, pos);
		return SHAPE.offset(offset.x, offset.y, offset.z);
	}
}
