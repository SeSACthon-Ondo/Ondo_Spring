package com.ondo.ondo_back.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MenuConvertor implements AttributeConverter<Map<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> menu) {

        if (menu == null) {

            return null;
        }

        try {

            return objectMapper.writeValueAsString(menu);
        } catch (JsonProcessingException e) {

            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {

        if (dbData == null) {

            return new HashMap<>();
        }

        try {

            return objectMapper.readValue(dbData, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {

            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
}
