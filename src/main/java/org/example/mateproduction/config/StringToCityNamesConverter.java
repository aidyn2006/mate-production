package org.example.mateproduction.config;

import org.example.mateproduction.util.CityNames;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCityNamesConverter implements Converter<String, CityNames> {

    @Override
    public CityNames convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return CityNames.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid value for CityNames enum: " + source);
            return null;
        }
    }

}