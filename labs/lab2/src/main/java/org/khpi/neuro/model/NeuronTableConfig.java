package org.khpi.neuro.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NeuronTableConfig {
    double rangeMin;
    double rangeMax;
    double aNeuronsSize;
    double sNeuronsSize;
}
