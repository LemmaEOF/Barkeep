package gay.lemmaeof.barkeep.modeling.parameter;

public record Parameters(String[] names, Parameter[] values) {
	public Parameters {
		if (values.length != names.length) {
			throw new IllegalArgumentException("Want " + names.length + " parameters on object, got " + values.length + "!");
		}
	}
}
