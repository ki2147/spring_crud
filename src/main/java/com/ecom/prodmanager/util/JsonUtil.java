package com.ecom.prodmanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(json, valueType);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> valueType) throws JsonProcessingException {
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
        return objectMapper.readValue(json, listType);
    }
}
