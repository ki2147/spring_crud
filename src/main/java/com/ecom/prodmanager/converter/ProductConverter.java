package com.ecom.prodmanager.converter;

import com.ecom.prodmanager.model.Product;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    @WritingConverter
    @Component
    public static class ProductToDocumentConverter implements Converter<Product, Document> {
        @Override
        public Document convert(Product product) {
            Document document = new Document();
            document.put("_id", product.get_id());
            document.put("name", product.getName());
            document.put("description", product.getDescription());
            document.put("category", product.getCategory());
            document.put("price", product.getPrice());
            product.getAdditionalFields().forEach(document::put);
            return document;
        }
    }

    @ReadingConverter
    @Component
    public static class DocumentToProductConverter implements Converter<Document, Product> {
        @Override
        public Product convert(Document document) {
            Product product = new Product();
            product.set_id(document.getObjectId("_id"));
            product.setName(document.getString("name"));
            product.setDescription(document.getString("description"));
            product.setCategory(document.getString("category"));
            product.setPrice(document.getDouble("price"));
            document.forEach((key, value) -> {
                if (!"_id".equals(key) && !"name".equals(key) && !"description".equals(key) && !"category".equals(key) && !"price".equals(key)) {
                    product.addField(key, value);
                }
            });
            return product;
        }
    }
}
