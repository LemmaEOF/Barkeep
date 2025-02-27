package gay.lemmaeof.barkeep.recipe;

import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.init.BarkeepComponents;
import gay.lemmaeof.barkeep.init.BarkeepItems;
import gay.lemmaeof.barkeep.init.BarkeepRecipes;
import gay.lemmaeof.barkeep.init.BarkeepRegistries;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PotionLabellingRecipe extends SpecialCraftingRecipe {

	public PotionLabellingRecipe(CraftingRecipeCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingRecipeInput inv, World world) {
		ItemStack potion = ItemStack.EMPTY;
		ItemStack paper = ItemStack.EMPTY;
		for (ItemStack stack : inv.getStacks()) {
			if (stack.getItem() == Items.POTION) {
				PotionContentsComponent comp = stack.get(DataComponentTypes.POTION_CONTENTS);
				if (comp != null && comp.potion().isPresent()) potion = stack;
			} else if (stack.getItem() == Items.PAPER) paper = stack;
		}
		return !potion.isEmpty() && !paper.isEmpty();
	}

	@Override
	public ItemStack craft(CraftingRecipeInput inv, RegistryWrapper.WrapperLookup lookup) {
		ItemStack res = new ItemStack(BarkeepItems.DRINK_BOTTLE);
		for (ItemStack stack : inv.getStacks()) {
			if (stack.getItem() == Items.POTION) {
				PotionContentsComponent comp = stack.get(DataComponentTypes.POTION_CONTENTS);
				if (comp != null && comp.potion().isPresent()) {
					Identifier potKey = comp.potion().get().getKey().get().getValue();
					RegistryKey<Drink> drinkKey = RegistryKey.of(BarkeepRegistries.DRINKS,
							Identifier.of(potKey.getNamespace(), potKey.getPath() + "_potion"));
					res.set(BarkeepComponents.DRINK_CONTAINER, new DrinkContainerComponent(drinkKey, 100));
					break;
				}
			}
		}
		return res;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BarkeepRecipes.POTION_LABELLING;
	}
}
