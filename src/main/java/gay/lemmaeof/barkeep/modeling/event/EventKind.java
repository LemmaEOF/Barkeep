package gay.lemmaeof.barkeep.modeling.event;

import net.minecraft.util.Identifier;

public record EventKind(Identifier name, String[] slotNames) {}
