package com.example.rag.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class DoubleArrayToStringConverter implements AttributeConverter<double[], String> {

    @Override
    public String convertToDatabaseColumn(double[] attribute) {
        if (attribute == null || attribute.length == 0) {
            return "";
        }
        return Arrays.stream(attribute)
                .mapToObj(Double::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public double[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new double[0];
        }
        String[] parts = dbData.split(",");
        double[] values = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Double.parseDouble(parts[i]);
        }
        return values;
    }
}
