package gay.lemmaeof.barkeep.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.lemmaeof.barkeep.data.Cocktail;
import gay.lemmaeof.barkeep.data.Drink;
import gay.lemmaeof.barkeep.data.FlavorNote;
import gay.lemmaeof.barkeep.util.TextUtils;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

//TODO: limit garniture?
public record CocktailComponent(Optional<Cocktail> cocktail, List<ItemStack> garniture) implements TooltipAppender {
	public static final CocktailComponent EMPTY = new CocktailComponent(Optional.empty(), List.of());
	public static final Codec<CocktailComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Cocktail.CODEC.optionalFieldOf("cocktail").forGetter(CocktailComponent::cocktail),
			ItemStack.CODEC.listOf().fieldOf("garniture").forGetter(CocktailComponent::garniture)
	).apply(instance, CocktailComponent::new));

	public CocktailComponent withCocktail(Cocktail cocktail) {
		return new CocktailComponent(Optional.of(cocktail), garniture);
	}

	public CocktailComponent withGarnish(ItemStack stack) {
		List<ItemStack> list = new ArrayList<>(garniture);
		list.add(stack);
		return new CocktailComponent(cocktail, list);
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		if (type.isAdvanced()) {
			tooltip.accept(Text.translatable("tooltip.barkeep.ingredients").formatted(Formatting.GRAY));
			if (this.cocktail.isPresent()) {
				Cocktail c = this.cocktail.get();
				for (RegistryEntry<Drink> drink : c.getDrinkEntries().keySet()) {
					int quarters = c.getDrinkEntries().get(drink);
					String parts = TextUtils.getPartNumber(quarters);
					String plural = quarters <= 4 ? "" : "s";
					Identifier id = new Identifier(drink.getIdAsString());
					tooltip.accept(Text.translatable("tooltip.barkeep.drink_amount", parts, plural).append(Text.translatable(id.toTranslationKey("drink"))).formatted(Formatting.GRAY));
				}
				tooltip.accept(Text.translatable("tooltip.barkeep.volume", TextUtils.getPartNumber(c.getVolume())).formatted(Formatting.GRAY));
				tooltip.accept(Text.translatable("tooltip.barkeep.alcohol", c.getAlcohol()).formatted(Formatting.GRAY));
				tooltip.accept(Text.translatable("tooltip.barkeep.color", TextColor.fromRgb(c.getColor()).getName()).formatted(Formatting.GRAY));
				tooltip.accept(Text.translatable("tooltip.barkeep.flavor_profile").formatted(Formatting.GRAY));
				for (FlavorNote note : c.getFlavorProfile().keySet()) {
					tooltip.accept(Text.translatable("tooltip.barkeep.flavor_amount", note.asString(), c.getFlavorProfile().get(note)).formatted(Formatting.GRAY));
				}
				PotionContentsComponent.buildTooltip(c.getEffects(), tooltip, 1.0F, context.getUpdateTickRate());
			}
		}
	}
}
