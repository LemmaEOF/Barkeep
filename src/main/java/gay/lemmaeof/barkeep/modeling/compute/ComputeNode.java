package gay.lemmaeof.barkeep.modeling.compute;

import gay.lemmaeof.barkeep.modeling.context.Context;

import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;

public interface ComputeNode<T> {
	T getResult(Context context);

	@Nullable
	default MethodHandle fastPath() {
		return null;
	}
}
