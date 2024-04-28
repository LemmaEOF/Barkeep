package gay.lemmaeof.barkeep.modeling.compute.ops;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;

public class NegateNode extends UnaryNode<Double> {
	public NegateNode(ComputeNode<Double> child) {
		super(child);
	}

	@Override
	public Double applyUnary(Double in) {
		return -in;
	}
}
