package gay.lemmaeof.barkeep.modeling.context;

import java.util.Collection;

public interface StaticContext {
	Collection<String> getParameters();

	Collection<String> getStateVariables();
}
