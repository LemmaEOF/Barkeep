package gay.lemmaeof.barkeep.modeling.compute;

import java.lang.invoke.MethodHandle;

import org.jetbrains.annotations.Nullable;

import gay.lemmaeof.barkeep.modeling.context.Context;

public interface ComputeNode<T> {
	T getResult(Context context);

	@Nullable
	default MethodHandle fastPath() {
		return null;
	}
}
