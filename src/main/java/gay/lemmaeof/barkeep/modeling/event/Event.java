package gay.lemmaeof.barkeep.modeling.event;

public record Event(EventKind kind, double[] values) {
	public Event {
		if (kind.slotNames().length != values.length) {
			throw new IllegalArgumentException("Event of kind " + kind.name() + " expected " + kind.slotNames().length
					+ " values, got " + values.length + "!");
		}
	}
}
