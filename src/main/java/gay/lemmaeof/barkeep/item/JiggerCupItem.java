package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import gay.lemmaeof.barkeep.block.entity.JiggerCupBlockEntity;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JiggerCupItem extends SneakyBlockItem {
	private final JiggerCupBlock cup;
	private final int size;

	public JiggerCupItem(JiggerCupBlock block, Settings settings) {
		super(block, settings);
		this.cup = block;
		this.size = block.getSize();
	}

	public JiggerCupBlock getCup() {
		return cup;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		DynamicRegistryManager manager = player.getWorld().getRegistryManager();
		DrinkContainer thisContainer = DrinkContainer.ITEM_LOOKUP.find(stack, manager);
		DrinkContainer targetContainer = DrinkContainer.ITEM_LOOKUP.find(slot.getStack(), manager);
		if (thisContainer.getDrink() == null && targetContainer != null && targetContainer.getVolume() >= size) {
			stack.set(BarkeepComponents.DRINK_CONTAINER, new DrinkContainerComponent(manager.get(BarkeepRegistries.DRINKS).getKey(targetContainer.getDrink()).get(), size));
			targetContainer.tryPour(size);
			player.playSound(SoundEvents.BLOCK_BREWING_STAND_BREW, 0.5f, player.getWorld().random.nextFloat() * 0.1F + 0.9F);
			return true;
		}
		return super.onStackClicked(stack, slot, clickType, player);
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof JiggerCupBlockEntity j && stack.contains(BarkeepComponents.DRINK_CONTAINER)) {
			j.setComponent(stack.get(BarkeepComponents.DRINK_CONTAINER));
			world.setBlockState(pos, state.with(JiggerCupBlock.FILLED, true));
		}
		return super.postPlacement(pos, world, player, stack, state);
	}
}
