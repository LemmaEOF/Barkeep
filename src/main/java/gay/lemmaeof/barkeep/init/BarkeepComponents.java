package gay.lemmaeof.barkeep.init;

import gay.lemmaeof.barkeep.Barkeep;
import gay.lemmaeof.barkeep.data.component.CocktailComponent;
import gay.lemmaeof.barkeep.data.component.DrinkContainerComponent;
import gay.lemmaeof.barkeep.data.component.MixerContentsComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class BarkeepComponents {
	public static final ComponentType<CocktailComponent> COCKTAIL = register("cocktail", ComponentType.<CocktailComponent>builder()
			.codec(CocktailComponent.CODEC).build());
	public static final ComponentType<MixerContentsComponent> MIXER_CONTENTS = register("mixer_contents", ComponentType.<MixerContentsComponent>builder()
			.codec(MixerContentsComponent.CODEC).build());
	public static final ComponentType<DrinkContainerComponent> DRINK_CONTAINER = register("drink_container", ComponentType.<DrinkContainerComponent>builder()
			.codec(DrinkContainerComponent.CODEC).build());

	public static void init() {}

	private static <T> ComponentType<T> register(String name, ComponentType<T> type) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Barkeep.MODID, name), type);
	}
}
