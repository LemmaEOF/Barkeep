package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.data.component.MixerContentsComponent;
import net.minecraft.component.DataComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class BarkeepComponents {
	public static final DataComponentType<CocktailComponent> COCKTAIL = register("cocktail", DataComponentType.<CocktailComponent>builder()
			.codec(CocktailComponent.CODEC).build());
	public static final DataComponentType<MixerContentsComponent> MIXER_CONTENTS = register("mixer_contents", DataComponentType.<MixerContentsComponent>builder()
			.codec(MixerContentsComponent.CODEC).build());
	public static final DataComponentType<DrinkContainerComponent> DRINK_CONTAINER = register("drink_component", DataComponentType.<DrinkContainerComponent>builder()
			.codec(DrinkContainerComponent.CODEC).build());

	public static void init() {}

	private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier(Barkeep.MODID, name), type);
	}
}
