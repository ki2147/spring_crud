package com.ecom.prodmanager.controller;

import com.ecom.prodmanager.model.Product;
import com.ecom.prodmanager.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product)  {

        productService.addProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT update an existing product
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId,
                                                 @Valid @RequestBody Product updatedProduct) {
        Optional<Product> product = productService.updateProduct(productId, updatedProduct);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE a product
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();

            for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errors);
        }
    }

}
