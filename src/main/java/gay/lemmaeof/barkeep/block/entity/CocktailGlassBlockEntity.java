package gay.lemmaeof.barkeep.block.entity;

import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.init.BarkeepBlocks;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.List;

//TODO: components on BEs are still kinda half-baked, put in real NBT
public class CocktailGlassBlockEntity extends BlockEntity {

	public CocktailGlassBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.COCKTAIL_GLASS_BE, pos, state);
	}

	public Cocktail getCocktail() {
		CocktailComponent comp = getCocktailComponent();
		if (comp == null) return null;
		return comp.cocktail();
	}

	public void setCocktail(Cocktail cocktail) {
		setCocktailComponent(new CocktailComponent(cocktail, getGarniture()));
	}

	public List<ItemStack> getGarniture() {
		return getCocktailComponent().garniture();
	}

	public void addGarnish(ItemStack stack) {
		setCocktailComponent(getCocktailComponent().withGarnish(stack));
	}

	public CocktailComponent getCocktailComponent() {
		return getComponents().get(BarkeepComponents.COCKTAIL);
	}

	public void setCocktailComponent(CocktailComponent component) {
		ComponentMap.Builder builder = ComponentMap.builder().addAll(getComponents());
		builder.add(BarkeepComponents.COCKTAIL, component);
		setComponents(builder.build());
	}
}
