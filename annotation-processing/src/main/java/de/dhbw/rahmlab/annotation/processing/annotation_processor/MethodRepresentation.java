package de.dhbw.rahmlab.annotation.processing.annotation_processor;

import java.util.List;

public record MethodRepresentation(String identifier, String returnType, List<ParameterRepresentation> parameters) {

}
