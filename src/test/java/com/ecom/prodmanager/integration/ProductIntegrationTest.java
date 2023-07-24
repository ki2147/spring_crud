package com.ecom.prodmanager.integration;

import com.ecom.prodmanager.model.Product;
import com.ecom.prodmanager.repository.ProductRepository;

import com.ecom.prodmanager.service.ProductService;
import com.ecom.prodmanager.util.JsonUtil;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ComponentScan("com.ecom.prodmanager.config")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductIntegrationTest {

    private static final String prodId = "0123456789ABCDEF01234567";
    Product product;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    public void addTestProduct() {
        // Clear the product with the ID of a specific product
        productRepository.deleteById(prodId);

        product = new Product(prodId, "Test Product", "Test Description", "Test Category", 10.0);
        productRepository.save(product);

    }

    @AfterEach
    public void cleanup() {
        // Remove the document with the test ID
        productRepository.deleteAll();
    }

    @Test
    public void getProductById_ExistingId_ReturnsProduct() throws Exception {

        addTestProduct();

        mockMvc.perform(get("/api/products/{productId}", prodId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("Test Category"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10));

    }

    @Test
    public void getProductById_NonExistingId_Returns404() throws Exception {
        String productId = "abcdef999999990000000000";

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Insert test products into the database
        addTestProduct();
        //Add a second product
        String prodTwoId = "0123456789ABCDEF01234500";
        Product productTwo = new Product(prodTwoId, "Product 2", "Description 2", "Category 2", 20.0);
        productRepository.save(productTwo);

        ResultActions result = mockMvc.perform(get("/api/products/all"));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name").value(Matchers.containsInAnyOrder("Test Product", "Product 2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].description").value(Matchers.containsInAnyOrder("Test Description", "Description 2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].category").value(Matchers.containsInAnyOrder("Test Category", "Category 2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].price").value(Matchers.containsInAnyOrder(10.0, 20.0)));

    }

    @Test
    public void testAddProduct_ValidRequest() throws Exception {
        // Create a valid product instance
        Product product = new Product();
        product.setName("Staedtler Mechanical Pencil");
        product.setDescription("0.7mm pencil");
        product.setPrice(240.0);
        product.setCategory("Stationery");

        // Perform the POST request to add the product
        mockMvc.perform(post("/api/products/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(product)))
                .andExpect(status().isCreated());

        // Perform a GET request to retrieve all products
        MvcResult mvcResult = mockMvc.perform(get("/api/products/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Convert the response to a list of products
        String responseContent = mvcResult.getResponse().getContentAsString();
        List<Product> products = JsonUtil.fromJsonToList(responseContent, Product.class);

        // Verify that a single product is returned
        assertThat(products).hasSize(1);

        // Verify the contents of the returned product
        Product returnedProduct = products.get(0);
        assertThat(returnedProduct.getName()).isEqualTo("Staedtler Mechanical Pencil");
        assertThat(returnedProduct.getDescription()).isEqualTo("0.7mm pencil");
        assertThat(returnedProduct.getPrice()).isEqualTo(240.0);
        assertThat(returnedProduct.getCategory()).isEqualTo("Stationery");
    }


    @Test
    public void testAddProduct_InvalidRequest() throws Exception {
        // Create an invalid product instance without a name
        Product product = new Product();
        product.setDescription("Test Description");
        product.setPrice(240.0);
        product.setCategory("Stationery");

        // Perform the POST request with the invalid product
        ResultActions result = mockMvc.perform(post("/api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(product)));

        // Verify that the response is a 'bad request'
        result.andExpect(status().isBadRequest());
    }


    @Test
    public void testUpdateProduct_ValidRequest() throws Exception {
        // Add a test product to the database
        addTestProduct();
        // Get the test product from the database
        Product existingProduct = productRepository.findById(prodId)
                .orElseThrow(NoSuchElementException::new);

        // Update the existing product with new values
        existingProduct.set_id(new ObjectId());
        existingProduct.setName("Updated Product");
        existingProduct.setDescription("Updated Description");
        existingProduct.setPrice(200.0);
        existingProduct.setCategory("Updated Category");

        // Perform the PUT request to update the product
        ResultActions result = mockMvc.perform(put("/api/products/{productId}", prodId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(existingProduct)));

        // Verify that the response is successful (200 OK)
        result.andExpect(status().isOk());

        // Retrieve the updated product from the database
        Optional<Product> optionalProduct = productRepository.findById(prodId);
        assertThat(optionalProduct).isPresent();

        // Verify that the product was updated with the new values
        Product retrievedProduct = optionalProduct.get();
        assertThat(retrievedProduct.getName()).isEqualTo("Updated Product");
        assertThat(retrievedProduct.getDescription()).isEqualTo("Updated Description");
        assertThat(retrievedProduct.getPrice()).isEqualTo(200.0);
        assertThat(retrievedProduct.getCategory()).isEqualTo("Updated Category");
    }


    @Test
    public void testDeleteProduct() throws Exception {
        // Insert a test product into the database
        addTestProduct();

        // Perform DELETE request to the endpoint
        mockMvc.perform(delete("/api/products/{productId}", prodId))
                .andExpect(status().isNoContent());

        // Verify that the product was deleted by trying to retrieve it
        mockMvc.perform(get("/api/products/{productId}", prodId))
                .andExpect(status().isNotFound());
    }



}
