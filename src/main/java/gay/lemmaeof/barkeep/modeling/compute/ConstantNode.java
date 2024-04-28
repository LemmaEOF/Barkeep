package gay.lemmaeof.barkeep.modeling.compute;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import gay.lemmaeof.barkeep.modeling.context.Context;

public record ConstantNode<T>(T value) implements ComputeNode<T> {
	@Override
	public T getResult(Context _ignored) {
		return value;
	}

	@Override
	public MethodHandle fastPath() {
		return MethodHandles.dropArguments(
			MethodHandles.constant(value.getClass(), value),
			0,
			Context.class
		);
	}
}
