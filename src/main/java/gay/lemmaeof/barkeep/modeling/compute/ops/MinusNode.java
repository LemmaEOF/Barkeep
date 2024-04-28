package gay.lemmaeof.barkeep.modeling.compute.ops;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;

public class MinusNode extends NAryNode<Double> {
	public MinusNode(Iterable<ComputeNode<Double>> children) {
		super(children);
	}

	@Override
	public Double applyBinary(Double left, Double right) {
		return left - right;
	}
}
