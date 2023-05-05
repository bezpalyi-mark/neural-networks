package org.khpi.neuro.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Neuron {
    int id;
    NeuronType type;
}
