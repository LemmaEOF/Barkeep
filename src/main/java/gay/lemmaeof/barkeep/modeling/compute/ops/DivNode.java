package gay.lemmaeof.barkeep.modeling.compute.ops;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;

public class DivNode extends NAryNode<Double> {
	public DivNode(Iterable<ComputeNode<Double>> children) {
		super(children);
	}

	@Override
	public Double applyBinary(Double left, Double right) {
		return left / right;
	}
}
