package gay.lemmaeof.barkeep.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.data.Cocktail;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//TODO: optional cocktail? you should probably just remove the component
public record CocktailComponent(Cocktail cocktail, List<ItemStack> garniture) implements TooltipAppender {
	public static final Codec<CocktailComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Cocktail.CODEC.fieldOf("cocktail").forGetter(CocktailComponent::cocktail),
			ItemStack.CODEC.listOf().fieldOf("garniture").forGetter(CocktailComponent::garniture)
	).apply(instance, CocktailComponent::new));

	public CocktailComponent withGarnish(ItemStack stack) {
		List<ItemStack> list = new ArrayList<>(garniture);
		list.add(stack);
		return new CocktailComponent(cocktail, list);
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {

	}
}
