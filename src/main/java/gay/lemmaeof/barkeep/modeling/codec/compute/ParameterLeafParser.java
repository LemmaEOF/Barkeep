package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.Optional;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.ParameterLookupNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;

public enum ParameterLeafParser implements PartialComputeLeafParser<Double> {
	INSTANCE;

	@Override
	public Optional<ComputeNode<Double>> tryParse(String string, StaticContext context) {
		var stateVars = context.getNamesOfKind("parameters");
		var index = stateVars.indexOf(string);
		if (index == -1) {
			return Optional.empty();
		} else {
			return Optional.of(new ParameterLookupNode(index));
		}
	}
}
