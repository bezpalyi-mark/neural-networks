package org.khpi.neuro.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NeuronPair {
    List<Integer> positive;
    List<Integer> negative;
}
