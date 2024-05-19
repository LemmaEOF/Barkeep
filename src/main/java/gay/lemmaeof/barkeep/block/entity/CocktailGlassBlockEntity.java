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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CocktailGlassBlockEntity extends ComponentSavingBlockEntity {

	public CocktailGlassBlockEntity(BlockPos pos, BlockState state) {
		super(BarkeepBlocks.COCKTAIL_GLASS_BE, pos, state);
	}

	public Cocktail getCocktail() {
		CocktailComponent comp = getCocktailComponent();
		if (comp == null) return null;
		return comp.cocktail().orElse(null);
	}

	public void setCocktail(Cocktail cocktail) {
		setCocktailComponent(new CocktailComponent(Optional.of(cocktail), getGarniture()));
	}

	public List<ItemStack> getGarniture() {
		if (getCocktailComponent() == null) return new ArrayList<>();
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
