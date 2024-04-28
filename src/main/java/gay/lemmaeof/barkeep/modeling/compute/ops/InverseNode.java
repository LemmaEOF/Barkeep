package gay.lemmaeof.barkeep.modeling.compute.ops;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;

public class InverseNode extends UnaryNode<Double> {
	public InverseNode(ComputeNode<Double> child) {
		super(child);
	}

	@Override
	public Double applyUnary(Double in) {
		return 1 / in;
	}
}
