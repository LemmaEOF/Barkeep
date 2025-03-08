package gay.lemmaeof.barkeep.block.entity;

import com.mojang.serialization.Codec;
import gay.lemmaeof.barkeep.Barkeep;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ComponentSavingBlockEntity extends BlockEntity {
	public static final Codec<ComponentMap> CODEC = ComponentMap.CODEC.optionalFieldOf("components", ComponentMap.EMPTY).codec();
	public ComponentSavingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), this.getComponents())
				.resultOrPartial(error -> Barkeep.LOGGER.warn("Failed to save components: {}", error))
				.ifPresent(c -> nbt.copyFrom((NbtCompound)c));
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.setComponents(CODEC.decode(registryLookup.getOps(NbtOps.INSTANCE), nbt).resultOrPartial(error -> Barkeep.LOGGER.error("Failed to load components: {}", error)).orElseThrow().getFirst());
		if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		NbtCompound nbt = super.toInitialChunkDataNbt(registryLookup);
		writeNbt(nbt, registryLookup);
		return nbt;
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
}
