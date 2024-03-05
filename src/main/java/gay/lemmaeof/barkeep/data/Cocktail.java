package gay.lemmaeof.barkeep.data;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public interface Cocktail {
	List<Ingredient> getPreferredGarniture();
	int getColor();
	int getVolume();
	float getAlcohol();
	Text getName();
	Map<FlavorNote, Integer> getFlavorProfile();
	List<StatusEffectInstance> getEffects();
	NbtElement toTag(DynamicRegistryManager manager);
}
