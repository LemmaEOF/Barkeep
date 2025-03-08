package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.api.DrinkContainer;
import gay.lemmaeof.barkeep.block.JiggerCupBlock;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

//TODO: implement
public class JiggerCupBlockEntity extends ComponentSavingBlockEntity implements DrinkContainer {

	public JiggerCupBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.JIGGER_CUP_BE, pos, state);
	}

	@Nullable
	@Override
	public Drink getDrink() {
		DrinkContainerComponent comp = getComponent();
		if (comp == null || getRegistry() == null) return null;
		return getRegistry().get(comp.drink());
	}

	@Override
	public int getVolume() {
		DrinkContainerComponent comp = getComponent();
		if (comp == null) return 0;
		return comp.amount();
	}

	@Override
	public int tryPour(int quarterParts) {
		removeComponent();
		world.setBlockState(pos, getCachedState().with(JiggerCupBlock.FILLED, false));
		return Math.min(quarterParts, getSize());
	}

	private int getSize() {
		return ((JiggerCupBlock) getCachedState().getBlock()).getSize();
	}

	public DrinkContainerComponent getComponent() {
		return getComponents().get(BarkeepComponents.DRINK_CONTAINER);
	}

	public void setComponent(DrinkContainerComponent component) {
		ComponentMap.Builder builder = ComponentMap.builder().addAll(getComponents());
		builder.add(BarkeepComponents.DRINK_CONTAINER, component);
		setComponents(builder.build());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void removeComponent() {
		ComponentMap.Builder builder = ComponentMap.builder();
		for (ComponentType<?> comp : getComponents().getTypes()) {
			if (comp == BarkeepComponents.DRINK_CONTAINER) continue;
			builder.add((ComponentType)comp, (Component)getComponents().get(comp));
		}
	}

	@Nullable
	private Registry<Drink> getRegistry() {
		if (world == null) return null;
		return world.getRegistryManager().get(BarkeepRegistries.DRINKS);
	}
}
