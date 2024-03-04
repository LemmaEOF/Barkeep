package gay.lemmaeof.barkeep.data;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public interface Cocktail {
	List<Item> getGarniture();
	int getColor();
	float getVolume();
	float getAlcohol();
	Text getName();
	Map<FlavorNote, Float> getFlavorProfile();
	List<StatusEffectInstance> getEffects();
}
