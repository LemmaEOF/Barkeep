package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.CocktailManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CocktailItem extends Item {
	public CocktailItem(Settings settings) {
		super(settings);
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
		}
		return super.finishUsing(stack, world, user);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		//TODO: garniture stored on the glass - cocktails now have preferred garniture that reduce use time even more
		return 200 - (hasCocktail(stack)? 0 : getCocktail(stack).getPreferredGarniture().size() * 30);
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
}
