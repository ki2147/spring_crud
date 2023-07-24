package com.ecom.prodmanager.repository;

import com.ecom.prodmanager.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    // Additional custom methods can be defined here if needed
}
