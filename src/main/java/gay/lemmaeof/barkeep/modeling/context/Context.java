package gay.lemmaeof.barkeep.modeling.context;

import org.jetbrains.annotations.Nullable;

import gay.lemmaeof.barkeep.modeling.event.Event;
import gay.lemmaeof.barkeep.modeling.parameter.Parameters;
import gay.lemmaeof.barkeep.modeling.schema.SchemaState;

public interface Context {
	@Nullable
	<T> T getComponent(Class<T> componentType);

	default Event getEvent() {
		return getComponent(Event.class);
	}

	default Parameters getParameters() {
		return getComponent(Parameters.class);
	}

	default SchemaState getState() {
		return getComponent(SchemaState.class);
	}
}
