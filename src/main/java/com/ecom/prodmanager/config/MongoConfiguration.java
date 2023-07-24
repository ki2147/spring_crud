package com.ecom.prodmanager.config;

import com.ecom.prodmanager.converter.ProductConverter;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfiguration {

    private final MappingMongoConverter mappingMongoConverter;
    private final ProductConverter.ProductToDocumentConverter productToDocumentConverter;
    private final ProductConverter.DocumentToProductConverter documentToProductConverter;

    public MongoConfiguration(
            MappingMongoConverter mappingMongoConverter,
            ProductConverter.ProductToDocumentConverter productToDocumentConverter,
            ProductConverter.DocumentToProductConverter documentToProductConverter) {
        this.mappingMongoConverter = mappingMongoConverter;
        this.productToDocumentConverter = productToDocumentConverter;
        this.documentToProductConverter = documentToProductConverter;
    }


    @PostConstruct
    public void setUpMongoConverter() {
        List<Object> converters = new ArrayList<>();
        converters.add(productToDocumentConverter);
        converters.add(documentToProductConverter);

        MongoCustomConversions customConversions = new MongoCustomConversions(converters);
        mappingMongoConverter.setCustomConversions(customConversions);
        mappingMongoConverter.afterPropertiesSet();
    }
}
