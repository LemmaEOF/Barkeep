
package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.ArrayList;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.ConstantNode;
import gay.lemmaeof.barkeep.modeling.compute.ops.DivNode;
import gay.lemmaeof.barkeep.modeling.compute.ops.InverseNode;
import gay.lemmaeof.barkeep.modeling.compute.ops.MinusNode;
import gay.lemmaeof.barkeep.modeling.compute.ops.NegateNode;
import gay.lemmaeof.barkeep.modeling.compute.ops.PlusNode;
import gay.lemmaeof.barkeep.modeling.compute.ops.TimesNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;

public record ArithComputeListParser(
		Double value,
		Function<ComputeNode<Double>, ComputeNode<Double>> unary,
		Function<Iterable<ComputeNode<Double>>, ComputeNode<Double>> ctor
	) implements ComputeListParser<Double> {

	public static ArithComputeListParser ADD = new ArithComputeListParser(0.0, Function.identity(), PlusNode::new);
	public static ArithComputeListParser SUB = new ArithComputeListParser(null, NegateNode::new, MinusNode::new);
	public static ArithComputeListParser MUL = new ArithComputeListParser(1.0, Function.identity(), TimesNode::new);
	public static ArithComputeListParser DIV = new ArithComputeListParser(null, InverseNode::new, DivNode::new);
	
	@Override
	public ComputeNode<Double> parse(Iterable<JsonElement> children, Function<JsonElement, ComputeNode<Double>> recur, StaticContext _ignored) {
		if (!children.iterator().hasNext()) {
			if (value == null) {
				throw new JsonParseException("Math operator expected 1+ elements, got 0!");
			}
			return new ConstantNode<>(value);
		}
		var list = new ArrayList<ComputeNode<Double>>();
		for (var child : children) {
			list.add(recur.apply(child));
		}
		if (list.size() == 1) {
			return unary.apply(list.get(0));
		} else {
			return ctor.apply(list);
		}
	}
}