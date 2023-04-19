package org.khpi.neuro.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LearningPair {
    SymbolData positiveSymbolData;
    SymbolData negativeSymbolData;
    @Builder.Default
    int positiveValue = 1;
    @Builder.Default
    int negativeValue = -1;
}
