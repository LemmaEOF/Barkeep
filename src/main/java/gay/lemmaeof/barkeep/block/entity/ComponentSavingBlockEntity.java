package gay.lemmaeof.barkeep.block.entity;

import com.mojang.serialization.Codec;
import gay.lemmaeof.barkeep.Barkeep;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ComponentSavingBlockEntity extends BlockEntity {
	public static final Codec<ComponentMap> CODEC = ComponentMap.CODEC.optionalFieldOf("components", ComponentMap.EMPTY).codec();
	public ComponentSavingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		CODEC.encode(this.getComponents(), registryLookup.getOps(NbtOps.INSTANCE), nbt).resultOrPartial(error -> Barkeep.LOGGER.error("Failed to save components: {}", error));
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.setComponents(CODEC.decode(registryLookup.getOps(NbtOps.INSTANCE), nbt).resultOrPartial(error -> Barkeep.LOGGER.error("Failed to load components: {}", error)).orElseThrow().getFirst());
	}
}
