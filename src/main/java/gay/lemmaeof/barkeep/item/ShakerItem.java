package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.ShakerBlock;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.CocktailManager;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.RecipeCocktail;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import gay.lemmaeof.barkeep.init.BarkeepSounds;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShakerItem extends SneakyBlockItem {
	public ShakerItem(ShakerBlock block, Settings settings) {
		super(block, settings);
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		DynamicRegistryManager manager = player.getWorld().getRegistryManager();
		DrinkContainer container = DrinkContainer.ITEM_LOOKUP.find(otherStack, manager);
		if (container != null) {
			Drink drink = container.getDrink();
			int poured = container.tryPour(container.getVolume());
			player.playSound(BarkeepSounds.DRINK_POUR, 0.5F, player.getWorld().random.nextFloat() * 0.1F + 0.9F);
			addDrink(stack, drink, poured, manager);
			return true;
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
			DynamicRegistryManager manager = user.getWorld().getRegistryManager();
			Cocktail cocktail = CocktailManager.INSTANCE.findCocktail(getDrinks(stack, manager), RecipeCocktail.Preparation.SHAKEN);
			world.playSound(null, user.getX(), user.getY(), user.getZ(), BarkeepSounds.SHAKER_OPEN, SoundCategory.PLAYERS, 0.5F, user.getWorld().random.nextFloat() * 0.1F + 0.9F);
			stack.getNbt().put("cocktail", cocktail.toTag(manager));
			stack.getNbt().remove("drinks");
			return stack;
		}
		return super.finishUsing(stack, world, user);
	}

	@Override
	public SoundEvent getDrinkSound() {
		return BarkeepSounds.SHAKER_SHAKE;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		if (world != null && context.isAdvanced()) {
			DynamicRegistryManager manager = world.getRegistryManager();
			Map<Drink, Integer> drinks = getDrinks(stack, manager);
			for (Drink drink : drinks.keySet()) {
				tooltip.add(Text.literal("  - ").append(Text.translatable(drink.getTranslationKey(manager))));
			}
		}
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		return super.postPlacement(pos, world, player, stack, state);
	}

	//replace all these with item components when we get to 1.20.5

	private Map<Drink, Integer> getDrinks(ItemStack stack, DynamicRegistryManager manager) {
		if (!stack.hasNbt() || !stack.getNbt().contains("drinks", NbtElement.COMPOUND_TYPE)) return new HashMap<>();
		Registry<Drink> drinkRegistry = manager.get(BarkeepRegistries.DRINKS);
		NbtCompound tag = stack.getNbt().getCompound("drinks");
		Map<Drink, Integer> ret = new HashMap<>();
		for (String key : tag.getKeys()) {
			ret.put(drinkRegistry.get(new Identifier(key)), tag.getInt(key));
		}
		return ret;
	}

	private void addDrink(ItemStack stack, Drink drink, int volume, DynamicRegistryManager manager) {
		NbtCompound tag = stack.getOrCreateSubNbt("drinks");
		String key = drink.getId(manager).toString();
		tag.putInt(key, tag.getInt(key) + volume);
	}

	private boolean hasDrinks(ItemStack stack) {
		return (stack.hasNbt() && stack.getNbt().contains("drinks", NbtElement.COMPOUND_TYPE));
	}
}
