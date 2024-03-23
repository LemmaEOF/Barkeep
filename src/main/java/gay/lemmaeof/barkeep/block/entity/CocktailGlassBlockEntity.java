package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.CocktailManager;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

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
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.cocktail = CocktailManager.INSTANCE.getCocktail(nbt);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (cocktail != null) {
			nbt.put("cocktail", cocktail.toTag(this.world.getRegistryManager()));
		}
	}
}
