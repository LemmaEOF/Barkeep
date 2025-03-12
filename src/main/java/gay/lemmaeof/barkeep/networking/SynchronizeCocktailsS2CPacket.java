package gay.lemmaeof.barkeep.networking;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipe;
import gay.lemmaeof.barkeep.data.recipe.CocktailRecipeEntry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SynchronizeCocktailsS2CPacket implements CustomPayload {
	public static final CustomPayload.Id<SynchronizeCocktailsS2CPacket> ID = new CustomPayload.Id<>(Identifier.of(Barkeep.MODID, "sync_cocktails"));
	//TODO: this is really inelegant! However, I'm *very* short on time and there are only eight cocktails in the BC demo so this will do
	public static final PacketCodec<RegistryByteBuf, SynchronizeCocktailsS2CPacket> CODEC = new PacketCodec<>() {
		@Override
		public SynchronizeCocktailsS2CPacket decode(RegistryByteBuf buf) {
			Map<CocktailRecipeEntry, CocktailComponent> recipeMap = new HashMap<>();
			int recipeCount = buf.readVarInt();
			for (int i = 0; i < recipeCount; i++) {
				Identifier id = buf.readIdentifier();
				CocktailRecipe recipe = CocktailRecipe.CODEC.decode(buf.getRegistryManager().getOps(NbtOps.INSTANCE), buf.readNbt()).getOrThrow().getFirst();
				CocktailComponent component = CocktailComponent.CODEC.decode(buf.getRegistryManager().getOps(NbtOps.INSTANCE), buf.readNbt()).getOrThrow().getFirst();
				recipeMap.put(new CocktailRecipeEntry(id, recipe), component);
			}
			return new SynchronizeCocktailsS2CPacket(recipeMap);
		}

		@Override
		public void encode(RegistryByteBuf buf, SynchronizeCocktailsS2CPacket value) {
			Map<CocktailRecipeEntry, CocktailComponent> map = value.recipeMap;
			buf.writeVarInt(map.size());
			for (CocktailRecipeEntry entry : map.keySet()) {
				buf.writeIdentifier(entry.id());
				buf.writeNbt(CocktailRecipe.CODEC.encodeStart(buf.getRegistryManager().getOps(NbtOps.INSTANCE), entry.recipe()).getOrThrow());
				buf.writeNbt(CocktailComponent.CODEC.encodeStart(buf.getRegistryManager().getOps(NbtOps.INSTANCE), map.get(entry)).getOrThrow());
			}
		}
	};

	public final Map<CocktailRecipeEntry, CocktailComponent> recipeMap;

	public SynchronizeCocktailsS2CPacket(Map<CocktailRecipeEntry, CocktailComponent> recipeMap) {
		this.recipeMap = recipeMap;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
