package com.ecom.prodmanager.service;

import com.ecom.prodmanager.model.Product;
import com.ecom.prodmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> updateProduct(String id, Product updatedProduct) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product existingProductData = existingProduct.get();

            // Update the required fields
            existingProductData.setName(updatedProduct.getName());
            existingProductData.setDescription(updatedProduct.getDescription());
            existingProductData.setCategory(updatedProduct.getCategory());
            existingProductData.setPrice(updatedProduct.getPrice());

            // Update the dynamic fields
            Map<String, Object> updatedFields = updatedProduct.getAdditionalFields();
            if (updatedFields != null) {
                // Remove missing fields
                existingProductData.getAdditionalFields().keySet().removeIf(fieldName -> !updatedFields.containsKey(fieldName));

                // Update the existing fields
                existingProductData.getAdditionalFields().putAll(updatedFields);
            }

            // Save the updated product
            return Optional.of(productRepository.save(existingProductData));
        }
        return Optional.empty();
    }


    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
