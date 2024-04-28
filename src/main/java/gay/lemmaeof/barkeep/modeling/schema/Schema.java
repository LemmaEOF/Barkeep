package gay.lemmaeof.barkeep.modeling.schema;

import gay.lemmaeof.barkeep.modeling.compute.ComputeNode;

public record Schema(String[] stateVariables, String[] paramNames, ComputeNode<Double> compute) {}