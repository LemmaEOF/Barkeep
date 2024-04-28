package gay.lemmaeof.barkeep.modeling.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaticContext {
	private Map<String, List<String>> strings;
	
	public StaticContext(Map<String, List<String>> strings) {
		this.strings = strings;
	}
	
	public List<? extends String> getNamesOfKind(String kind) {
		return strings.computeIfAbsent(kind, $ -> new ArrayList<>());
	}
}
