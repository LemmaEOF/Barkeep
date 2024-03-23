package gay.lemmaeof.barkeep.item;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.block.BlockState;
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
	private final int size;
	public JiggerCupItem(JiggerCupBlock block, Settings settings) {
		super(block, settings);
		this.size = block.getSize();
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
			stack.getOrCreateNbt().putString("drink", manager.get(BarkeepRegistries.DRINKS).getId(targetContainer.getDrink()).toString());
			targetContainer.tryPour(size);
			player.playSound(SoundEvents.BLOCK_BREWING_STAND_BREW, 0.5f, player.getWorld().random.nextFloat() * 0.1F + 0.9F);
			return true;
		}
		return super.onStackClicked(stack, slot, clickType, player);
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		return super.postPlacement(pos, world, player, stack, state);
	}
}
