
package gay.lemmaeof.barkeep.modeling.codec.compute;

import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;
import gay.lemmaeof.barkeep.modeling.compute.EventLookupNode;
import gay.lemmaeof.barkeep.modeling.context.StaticContext;
import net.minecraft.util.Identifier;

public enum EventComputeListParser implements ComputeListParser<Double> {
	INSTANCE;
	
	@Override
	public ComputeNode<Double> parse(List<JsonElement> children, Function<JsonElement, ComputeNode<Double>> recur, StaticContext context) {
		var eventName = context.getNamesOfKind("event_name");
		if (eventName.isEmpty()) {
			throw new JsonParseException("Event operator used in non-event JSON!");
		}
		var name = eventName.get(0);
		if (children.size() != 1 || !(children.get(0) instanceof JsonPrimitive prim) || !prim.isString()) {
			throw new JsonParseException("Event operator expected 1 string!");
		}
		var slotName = prim.getAsString();
		List<? extends String> eventFields = context.getNamesOfKind("event");
		var slotId = eventFields.indexOf(slotName);
		if (slotId == -1) {
			throw new JsonParseException("Not a field on the event: " + slotName);
		}
		if (Identifier.tryParse(name) instanceof Identifier id) {
			return new EventLookupNode(id, slotId);
		} else {
			throw new IllegalStateException("(BUG) Malformed resourceid: " + eventName + ". This should not happen!!!");
		}
	}
}