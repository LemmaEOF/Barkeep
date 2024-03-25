package gay.lemmaeof.barkeep.block;

import gay.lemmaeof.barkeep.block.entity.CocktailGlassBlockEntity;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.CocktailManager;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

//TODO: multiple types of glass
public class CocktailGlassBlock extends Block implements BlockEntityProvider {
	//TODO: BER instead of block model? would let me do fancy animations and variable drink sizes, and we need one anyway for garniture
	//TODO: oh if it's a BER I should make shaken drinks more opaque bc they're aerated
	public static final BooleanProperty FILLED = BooleanProperty.of("filled");
	public static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 10, 11);

	//TODO: cup capacity?
	public CocktailGlassBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(FILLED, false));
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CocktailGlassBlockEntity glass) {
			if (stack.isOf(BarkeepItems.SHAKER) && glass.getCocktail() == null) {
				Cocktail cocktail = CocktailManager.INSTANCE.getCocktail(stack);
				if (cocktail != null) {
					//TODO: sound
					glass.setCocktail(cocktail);
					world.setBlockState(pos, state.with(FILLED, true));
					stack.getNbt().remove("cocktail");
					return ActionResult.SUCCESS;
				}
			} else if (stack.isEmpty() && player.isSneaking() && glass.getCocktail() != null) {
				ItemStack giveStack = new ItemStack(BarkeepItems.TEST_COCKTAIL);
				giveStack.getOrCreateNbt().put("cocktail", glass.getCocktail().toTag(world.getRegistryManager()));
				player.setStackInHand(hand, giveStack);
				world.removeBlock(pos, false);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d offset = state.getModelOffset(world, pos);
		return SHAPE.offset(offset.x, offset.y, offset.z);
	}
}
