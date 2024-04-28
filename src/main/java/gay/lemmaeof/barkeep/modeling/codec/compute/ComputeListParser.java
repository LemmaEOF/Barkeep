package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.function.Function;

import com.google.gson.JsonElement;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;

@FunctionalInterface
public interface ComputeListParser<T> {
	public ComputeNode<T> parse(Iterable<JsonElement> children, Function<JsonElement, ComputeNode<T>> recur, StaticContext context);
}
