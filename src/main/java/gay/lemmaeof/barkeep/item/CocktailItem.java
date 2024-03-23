package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.block.CocktailGlassBlock;
import gay.lemmaeof.barkeep.block.entity.CocktailGlassBlockEntity;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.CocktailManager;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CocktailItem extends SneakyBlockItem {
	public CocktailItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (hasCocktail(stack)) {
			user.setCurrentHand(hand);
			return TypedActionResult.success(stack);
		}
		return TypedActionResult.pass(stack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (hasCocktail(stack)) {
			Cocktail cocktail = getCocktail(stack);
			for (StatusEffectInstance effect : cocktail.getEffects()) {
				user.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration()));
			}
			stack.getNbt().remove("cocktail");
			return stack;
		}
		return super.finishUsing(stack, world, user);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		if (!hasCocktail(stack)) return 200;
		int preferredGarniture = 0;
		Cocktail cocktail = getCocktail(stack);
		List<ItemStack> garniture = getGarniture(stack);
		for (ItemStack garnish : garniture) {
			for (Ingredient ing : cocktail.getPreferredGarniture()) {
				if (ing.test(garnish)) {
					preferredGarniture++;
					break;
				}
			}
		}
		return 200 - (garniture.size() * 30 + preferredGarniture * 30);
	}

	@Override
	public Text getName(ItemStack stack) {
		if (hasCocktail(stack)) return getCocktail(stack).getName();
		return super.getName(stack);
	}

	private boolean hasCocktail(ItemStack stack) {
		return CocktailManager.INSTANCE.hasCocktail(stack);
	}

	private Cocktail getCocktail(ItemStack stack) {
		return CocktailManager.INSTANCE.getCocktail(stack);
	}

	private List<ItemStack> getGarniture(ItemStack stack) {
		DefaultedList<ItemStack> stacks = DefaultedList.of();
		Inventories.readNbt(stack.getOrCreateNbt().getCompound("garniture"), stacks);
		return stacks;
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		if (BarkeepItems.TEST_COCKTAIL.hasCocktail(stack)) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof CocktailGlassBlockEntity glass) {
				glass.setCocktail(BarkeepItems.TEST_COCKTAIL.getCocktail(stack));
				world.setBlockState(pos, state.with(CocktailGlassBlock.FILLED, true));
				return true;
			}
		}
		return false;
	}
}
