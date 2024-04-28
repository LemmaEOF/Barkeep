package gay.lemmaeof.barkeep.modeling.compute;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import gay.lemmaeof.barkeep.modeling.context.Context;
import gay.lemmaeof.barkeep.modeling.event.Event;
import net.minecraft.util.Identifier;

public record ParameterLookupNode(int index) implements ComputeNode<Double> {
	@Override
	public Double getResult(Context context) {
		var params = context.getParameters();
		return params.values()[index].getValue(context);
	}


	@Override
	public MethodHandle fastPath() {
		return null; // TODO stub
	}
}
