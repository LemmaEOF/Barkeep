package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class JiggerCupBlockEntity extends BlockEntity implements DrinkContainer {
	private Identifier currentDrink;
	private int currentVolume;

	public JiggerCupBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.JIGGER_CUP_BE, pos, state);
	}

	@Override
	public Drink getDrink() {
		Registry<Drink> registry = getRegistry();
		if (registry == null || currentDrink == null) return null;
		return registry.get(currentDrink);
	}

	@Override
	public int getVolume() {
		return currentVolume;
	}

	@Override
	public int tryPour(int quarterParts) {
		this.currentVolume = 0;
		this.currentDrink = null;
		return Math.min(quarterParts, getSize());
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
	}

	private int getSize() {
		return ((JiggerCupBlock) getCachedState().getBlock()).getSize();
	}

	@Nullable
	private Registry<Drink> getRegistry() {
		if (world == null) return null;
		return world.getRegistryManager().get(BarkeepRegistries.DRINKS);
	}
}
