package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.Optional;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;

@FunctionalInterface
public interface PartialComputeLeafParser<T> {
	public Optional<ComputeNode<T>> tryParse(String string, StaticContext context);
}
