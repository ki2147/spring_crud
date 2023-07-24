package com.ecom.prodmanager.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "products")
public class Product {
    @Field("fields")
    private Map<String, Object> fields;

    private ObjectId _id;
    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Description is required")
    private String description;

    @NotEmpty(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private Double price;

    public Product() {
        this.fields = new HashMap<>();
    }

    public Product(String id, String name, String description, String category, double price) {
        this._id = new ObjectId(id);
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.fields = new HashMap<>();
    }


    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId id) {
        this._id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //fields.put("name", name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        //fields.put("description", description);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        //fields.put("category", category);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
        //fields.put("price", price);
    }

    public void addField(String fieldName, Object fieldValue) {
        fields.put(fieldName, fieldValue);
    }

    @JsonAnySetter
    public void setFields(String key, Object value) {
        fields.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return fields;
    }


    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

