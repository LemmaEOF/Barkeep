package gay.lemmaeof.barkeep.modeling.compute;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import gay.lemmaeof.barkeep.modeling.context.Context;
import gay.lemmaeof.barkeep.modeling.event.Event;
import net.minecraft.util.Identifier;

public record EventLookupNode(Identifier eventName, int index) implements ComputeNode<Double> {
	@Override
	public Double getResult(Context context) {
		var event = context.getEvent();
		// these checks shouldn't be necessary if invariants are upheld
		// not that it super matters - probably not hot??
		if (event == null) {
			throw new IllegalArgumentException("Event accessor where no event exists!");
		}
		if (event.kind().name() != eventName) {
			throw new IllegalArgumentException("Mismatched event! Expected: " + event.kind().name() + ", got: " + eventName);
		}
		return event.values()[index];
	}

	private static final MethodHandle GET_EVENT_VALUES;
	static {
		var lookup = MethodHandles.lookup();
		MethodHandle getEventValues = null;
		try {
			var getEvent = lookup.findVirtual(Context.class, "getEvent", MethodType.methodType(Event.class));
			var getValues = lookup.findVirtual(Event.class, "values", MethodType.methodType(double[].class));
			var arrayGetter = MethodHandles.arrayElementGetter(double[].class);
			getEventValues = MethodHandles.collectArguments(
					arrayGetter,
					0,
					MethodHandles.collectArguments(getValues, 0, getEvent));

		} catch (ReflectiveOperationException _ignored) {}
		GET_EVENT_VALUES = getEventValues;
	}

	@Override
	public MethodHandle fastPath() {
		return MethodHandles.insertArguments(GET_EVENT_VALUES, 1, index);
	}
}
