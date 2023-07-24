package com.ecom.prodmanager.controller;

import com.ecom.prodmanager.model.Product;
import com.ecom.prodmanager.util.JsonUtil;
import com.ecom.prodmanager.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    String productId = "0123456789abcdef01234567";
    @MockBean
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testGetProductById() throws Exception {
        // Mock the ProductService to return a document
        Product product = new Product(productId, "Wooden 2B Pencil", "Hexagonal cross-section with eraser", "Stationery", 15.0);
        when(productService.getProductById(anyString())).thenReturn(Optional.of(product));

        // Perform GET request to the endpoint
        MvcResult result = mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._id").value(productId))
                .andReturn();

    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Prepare mock data
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(new Product("0123456789ABCDEF01234567", "Product 1", "Description 1", "Category 1", 10.0));
        mockProducts.add(new Product("0123456789ABCDEF01234500", "Product 2", "Description 2", "Category 2", 20.0));

        // Mock the ProductService to return the list of products
        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Perform GET request to the endpoint
        mockMvc.perform(get("/api/products/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].category").value("Category 1"))
                .andExpect(jsonPath("$[0].price").value(10.0))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].category").value("Category 2"))
                .andExpect(jsonPath("$[1].price").value(20.0));

        // Verify that the productService.getAllProducts() was called
        verify(productService, times(1)).getAllProducts();
    }


    @Test
    public void testAddProduct_ValidRequest() throws Exception {
        // Create a valid product instance
        Product product = new Product();
        product.setName("Staedtler Mechanical Pencil");
        product.setDescription("0.7mm pencil");
        product.setPrice(240.0);
        product.setCategory("Stationery");

        System.out.println("Post request: " + JsonUtil.toJson(product));

        // Perform the POST request
        mockMvc.perform(post("/api/products/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.toJson(product)))
                    .andExpect(status().isCreated());

        // Verify that the addProduct method was called
        verify(productService).addProduct(any(Product.class));
    }


    @Test
    public void testAddProduct_InvalidRequest() throws Exception {

        // Create an invalid product instance without the required fields
        Product product = new Product();
        product.setName("Staedtler Mechanical Pencil");
        product.setPrice(240.0);
        product.setCategory("Stationery");

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(product)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Prepare mock data

        Product existingProduct = new Product(productId, "Product", "Description", "Category", 10.0);
        Product updatedProduct = new Product(productId, "Updated Product", "Updated Description", "Updated Category", 20.0);

        // Mock the behavior of ProductService to return the updated product
        when(productService.updateProduct(anyString(), any(Product.class))).thenReturn(Optional.of(updatedProduct));

        // Perform PUT request to the endpoint
        ResultActions result = mockMvc.perform(put("/api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedProduct)));

        result.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.category").value("Updated Category"))
                .andExpect(jsonPath("$.price").value(20.0));
    }

    @Test
    public void testUpdateProductNotFound() throws Exception {
        // Prepare mock data
        String productId = "0123456789ABCDEF01234567";
        Product updatedProduct = new Product(productId, "Updated Product", "Updated Description", "Updated Category", 20.0);

        // Mock the behavior of ProductService to return empty optional (not found)
        when(productService.updateProduct(anyString(), any(Product.class))).thenReturn(Optional.empty());

        // Perform PUT request to the endpoint
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Prepare mock data
        String productId = "0123456789ABCDEF01234567";

        // Perform DELETE request to the endpoint
        mockMvc.perform(delete("/api/products/{productId}", productId))
                .andExpect(status().isNoContent());

        // Verify that the ProductService deleteProduct method was called with the correct productId
        verify(productService, times(1)).deleteProduct(productId);
    }
}
