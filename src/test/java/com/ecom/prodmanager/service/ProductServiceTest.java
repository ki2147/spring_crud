package com.ecom.prodmanager.service;

import com.ecom.prodmanager.model.Product;
import com.ecom.prodmanager.repository.ProductRepository;
import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    public void testGetProductById() {
        // Prepare mock data
        String objectId = "648bf631486b00c1f20b0289";
        Product mockProduct = new Product();
        mockProduct.set_id(new ObjectId(objectId));
        mockProduct.setName("Test Product");
        mockProduct.setDescription("Test Description");
        mockProduct.setCategory("Test Category");
        mockProduct.setPrice(10.0);

        // Mock the behavior of ProductRepository
        when(productRepository.findById(objectId))
                .thenReturn(Optional.of(mockProduct));

        // Invoke the method under test
        Optional<Product> result = productService.getProductById(objectId);

        // Verify the result
        assertThat(result).isPresent();
        Product product = result.get();
        assertThat(product.get_id().toHexString()).isEqualTo(objectId);
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isEqualTo("Test Description");
        assertThat(product.getCategory()).isEqualTo("Test Category");
        assertThat(product.getPrice()).isEqualTo(10.0);
    }


    @Test
    public void testAddProduct() {
        // Prepare mock data
        Product mockProduct = new Product();
        mockProduct.setName("Test Product");
        mockProduct.setDescription("Test Description");
        mockProduct.setCategory("Test Category");
        mockProduct.setPrice(10.0);

        // Invoke the method under test
        productService.addProduct(mockProduct);

        // Verify that the product is passed to the repository save method
        verify(productRepository).save(productCaptor.capture());

        // Retrieve the captured product
        Product capturedProduct = productCaptor.getValue();

        // Assert the captured product matches the input product
        assertThat(capturedProduct).isEqualTo(mockProduct);
    }


    @Test
    public void testGetAllProducts() {
        // Prepare mock data
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(new Product("0123456789ABCDEF01234567", "Product 1", "Description 1", "Category 1", 10.0));
        mockProducts.add(new Product("0123456789ABCDEF01234500", "Product 2", "Description 2", "Category 2", 20.0));

        // Mock the behavior of ProductRepository
        when(productRepository.findAll()).thenReturn(mockProducts);

        // Invoke the method under test
        List<Product> result = productService.getAllProducts();

        // Verify the result
        assertThat(result).isEqualTo(mockProducts);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateProduct() {
        // Prepare mock data
        String productId = "0123456789ABCDEF01234567";
        Product existingProduct = new Product(productId, "Product", "Description", "Category", 10.0);
        Product updatedProduct = new Product(productId, "Updated Product", "Updated Description", "Updated Category", 20.0);

        // Mock the behavior of ProductRepository
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);

        // Invoke the method under test
        Optional<Product> result = productService.updateProduct(productId, updatedProduct);

        // Verify the result
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(updatedProduct);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(Mockito.any(Product.class));
    }


    @Test
    public void testDeleteProduct() {
        // Prepare mock data
        String productId = "0123456789ABCDEF01234567";

        // Mock the behavior of ProductRepository
        Mockito.doNothing().when(productRepository).deleteById(productId);

        // Invoke the method under test
        productService.deleteProduct(productId.toString());

        // Verify the interaction with ProductRepository
        verify(productRepository, times(1)).deleteById(productId);
    }
}
