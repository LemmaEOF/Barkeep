package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

//TODO: safe NBT
public class CocktailGlassBlockEntity extends BlockEntity {
	private Cocktail cocktail;

	public CocktailGlassBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.COCKTAIL_GLASS_BE, pos, state);
	}

	public Cocktail getCocktail() {
		return cocktail;
	}

	public void setCocktail(Cocktail cocktail) {
		this.cocktail = cocktail;
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		super.readNbt(nbt, lookup);
		this.cocktail = Cocktail.CODEC.decode(NbtOps.INSTANCE, nbt.get("cocktail")).getOrThrow().getFirst();
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		super.writeNbt(nbt, lookup);
		if (cocktail != null) {
			nbt.put("cocktail", Cocktail.CODEC.encodeStart(NbtOps.INSTANCE, cocktail).getOrThrow());
		}
	}
}
