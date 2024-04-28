package gay.lemmaeof.barkeep.modeling.compute;

public interface CataComputeNode<T> extends ComputeNode<T> {
	Iterable<ComputeNode<T>> children();
}