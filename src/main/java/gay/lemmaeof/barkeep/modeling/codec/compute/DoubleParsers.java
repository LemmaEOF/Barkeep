package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.ConstantNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;

public class DoubleParsers {
	public static Map<String, ComputeListParser<Double>> LIST_PARSERS = new HashMap<>();
	public static List<PartialComputeLeafParser<Double>> LEAF_PARSERS = new ArrayList<>();
	
	public static ComputeNode<Double> doParse(JsonElement elt, StaticContext staticContext) {
		try {
			switch (elt) {
			case JsonArray arr -> {
				if (arr.isEmpty()) {
					throw new JsonParseException("Illegal empty list in computation!");
				}
				var name = arr.get(0);
				if (!LIST_PARSERS.containsKey(name)) {
					throw new JsonParseException("Unknown computation function: " + name + "!");
				}
				var listParser = LIST_PARSERS.get(name);
				var args = arr.asList().subList(1, arr.size());
				return listParser.parse(args, v -> doParse(v, staticContext), staticContext);
			}
			case JsonPrimitive prim when prim.isString() -> {
				var string = prim.getAsString();
				for (var parser : LEAF_PARSERS) {
					var parsed = parser.tryParse(string, staticContext).orElse(null);
					if (parsed != null) {
						return parsed;
					}
				}
				throw new JsonParseException("Unknown variable found!");
			}
			case JsonPrimitive prim when prim.isNumber() -> {
				return new ConstantNode<>(prim.getAsDouble());
			}
			default -> throw new JsonParseException("Bad computation datum found!");
			}
		} catch (JsonParseException pe) {
			throw new IllegalArgumentException(pe.getMessage() + "\nOffending datum: " + elt, pe);
		}
	}
}
