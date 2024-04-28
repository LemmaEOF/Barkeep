package gay.lemmaeof.barkeep.modeling.compute.ops;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

import gay.lemmaeof.barkeep.modeling.compute.CataComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.context.Context;

public abstract class UnaryNode<T> implements CataComputeNode<T> {
	private ComputeNode<T> child;

	public abstract T applyUnary(T value);

	public UnaryNode(ComputeNode<T> child) {
		this.child = child;
	}

	@Override
	public Iterable<ComputeNode<T>> children() {
		return List.of(child);
	}

	@Override
	public T getResult(Context context) {
		return applyUnary(this.child.getResult(context));
	}

	@Override
	public MethodHandle fastPath() {
		var childHandle = child.fastPath();
		if (childHandle == null) {
			return null;
		}
		try {
			var self = MethodHandles.lookup().bind(this, "applyUnary", MethodType.methodType(Object.class, Object.class));
			return MethodHandles.collectArguments(
				self,
				0,
				childHandle
			);
		} catch (ReflectiveOperationException ignored) {
			return null;
		}
	}
}
