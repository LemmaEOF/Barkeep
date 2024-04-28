package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.Optional;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.StateLookupNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;

public enum StateLeafParser implements PartialComputeLeafParser<Double> {
	INSTANCE;

	@Override
	public Optional<ComputeNode<Double>> tryParse(String string, StaticContext context) {
		var stateVars = context.getNamesOfKind("state_variables");
		var index = stateVars.indexOf(string);
		if (index == -1) {
			return Optional.empty();
		} else {
			return Optional.of(new StateLookupNode(index));
		}
	}
}
