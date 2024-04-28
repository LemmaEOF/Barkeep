package gay.lemmaeof.barkeep.modeling.compute.ops;

import gay.lemmaeof.barkeep.modeling.context.Context;
import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.CataComputeNode;

import java.util.List;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public abstract class NAryNode<T> implements CataComputeNode<T> {
	private Iterable<ComputeNode<T>> children;

	public abstract T applyBinary(T acc, T value);

	public NAryNode(Iterable<ComputeNode<T>> children) {
		if (!children.iterator().hasNext()) {
			throw new IllegalArgumentException("N-ary node must have at least one child!");
		}
		this.children = children;
	}

	@Override
	public Iterable<ComputeNode<T>> children() {
		return children;
	}

	@Override
	public T getResult(Context context) {
		var it = children.iterator();
		var acc = it.next().getResult(context);
		while (it.hasNext()) {
			acc = applyBinary(acc, it.next().getResult(context));
		}
		return acc;
	}

	@Override
	public MethodHandle fastPath() {
		// TODO: fill in fastpath
		return null;
	}
}
