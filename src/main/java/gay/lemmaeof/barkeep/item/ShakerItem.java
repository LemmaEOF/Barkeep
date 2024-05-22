package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.ShakerBlock;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.data.component.MixerContentsComponent;
import gay.lemmaeof.barkeep.data.recipe.CocktailPreparation;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeManager;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepSounds;
import gay.lemmaeof.barkeep.init.BarkeepTags;
import gay.lemmaeof.barkeep.util.TextUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShakerItem extends SneakyBlockItem {
	public ShakerItem(ShakerBlock block, Settings settings) {
		super(block, settings);
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		ItemStack otherStack = slot.getStack();
		Optional<Cocktail> cocktail = stack.getOrDefault(BarkeepComponents.COCKTAIL, CocktailComponent.EMPTY).cocktail();
		if (cocktail.isPresent() && otherStack.getItem() instanceof CocktailGlassItem glass && !glass.hasCocktail(otherStack)) {
			if (glass.getCapacity() >= cocktail.get().getVolume()) {
				//TODO: sound
				glass.setCocktail(otherStack, cocktail.get());
				stack.remove(BarkeepComponents.COCKTAIL);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		DynamicRegistryManager manager = player.getWorld().getRegistryManager();
		DrinkContainer container = DrinkContainer.ITEM_LOOKUP.find(otherStack, manager);
		MixerContentsComponent comp = stack.get(BarkeepComponents.MIXER_CONTENTS);
		if (container != null) {
			Drink drink = container.getDrink();
			int poured = container.tryPour(container.getVolume());
			player.playSound(BarkeepSounds.DRINK_POUR, 0.5F, player.getWorld().random.nextFloat() * 0.1F + 0.9F);
			addDrink(stack, drink, poured, manager);
			return true;
		} else if (otherStack.isIn(BarkeepTags.ICE)) {
			if (comp != null) {
				if (!comp.iced()) {
					//TODO: sound
					stack.set(BarkeepComponents.MIXER_CONTENTS, comp.withIce(true));
					otherStack.decrement(1);
					return true;
				}
			}  else {
				stack.set(BarkeepComponents.MIXER_CONTENTS, new MixerContentsComponent(new HashMap<>(), true));
				otherStack.decrement(1);
				return true;
			}
		}
		return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (hasDrinks(stack)) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.pass(stack);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 60;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient) {
			boolean iced = stack.getOrDefault(BarkeepComponents.MIXER_CONTENTS, MixerContentsComponent.EMPTY).iced();
			Cocktail cocktail = CocktailRecipeManager.INSTANCE.createCocktail(getDrinks(stack), iced? CocktailPreparation.SHAKEN : CocktailPreparation.DRY_SHAKEN);
			world.playSound(null, user.getX(), user.getY(), user.getZ(), BarkeepSounds.SHAKER_OPEN, SoundCategory.PLAYERS, 0.5F, user.getWorld().random.nextFloat() * 0.1F + 0.9F);
			stack.set(BarkeepComponents.MIXER_CONTENTS, MixerContentsComponent.empty());
			stack.set(BarkeepComponents.COCKTAIL, new CocktailComponent(Optional.of(cocktail), new ArrayList<>()));
			return stack;
		}
		return super.finishUsing(stack, world, user);
	}

	@Override
	public SoundEvent getDrinkSound() {
		return BarkeepSounds.SHAKER_SHAKE;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		if (type.isAdvanced()) {
			//TODO: fix later once I figure out how wrapper lookup can not Fucking Suck
			DynamicRegistryManager manager = (DynamicRegistryManager) context.getRegistryLookup();
			Map<Drink, Integer> drinks = getDrinks(stack);
			for (Drink drink : drinks.keySet()) {
				//TODO: this is very much only designed for english rn, figure out a way to do proper pluralization later
				int quarters = drinks.get(drink);
				String parts = TextUtils.getPartNumber(quarters);
				String plural = quarters <= 4? "" : "s";
				tooltip.add(Text.translatable("tooltip.barkeep.drink_amount", parts, plural).append(Text.translatable(drink.getTranslationKey(manager))));
			}
		}
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		return super.postPlacement(pos, world, player, stack, state);
	}

	private Map<Drink, Integer> getDrinks(ItemStack stack) {
		return stack.getOrDefault(BarkeepComponents.MIXER_CONTENTS, MixerContentsComponent.EMPTY).getDrinks();
	}

	//TODO: switch to wrapperlookup when I figure out how to make that not suck (might be impossible it sucks)
	private void addDrink(ItemStack stack, Drink drink, int volume, DynamicRegistryManager manager) {
		MixerContentsComponent comp = stack.get(BarkeepComponents.MIXER_CONTENTS);
		if (comp == null) comp = new MixerContentsComponent(new HashMap<>(), false);
		stack.set(BarkeepComponents.MIXER_CONTENTS, comp.withDrink(drink, volume, manager));
	}

	private boolean hasDrinks(ItemStack stack) {
		return!stack.getOrDefault(BarkeepComponents.MIXER_CONTENTS, MixerContentsComponent.EMPTY).getDrinks().isEmpty();
	}
}
