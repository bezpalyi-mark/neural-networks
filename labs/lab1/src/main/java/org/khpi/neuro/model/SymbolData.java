package org.khpi.neuro.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@Getter
public class SymbolData {
    List<Integer> data;
}
