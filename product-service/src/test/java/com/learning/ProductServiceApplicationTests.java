package com.learning;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.dto.ProductRequest;
import com.learning.dto.ProductResponse;
import com.learning.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProductTest() throws Exception {
        // Given
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        // When
        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());

        // Then
        Assertions.assertEquals(productRepository.findAll().size(), 1);
    }

    @Test
    void shouldFailWhenRequiredFieldsAreMissingTest() throws Exception {
        // Given
        ProductRequest productRequest = getIncompleteProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        // When
        // Then
        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllRegisteredProductsTest() throws Exception {
        // Given
        // When
        MvcResult result = mockMvc.perform(get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<ProductResponse> productList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ProductResponse>>() {
        });

        // Then
        Assertions.assertEquals(1, productList.size());
    }

    private ProductRequest getIncompleteProductRequest() {
        return ProductRequest.builder()
                .name(null)
                .description(null)
                .price(BigDecimal.valueOf(124123.53))
                .build();
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Iphone 24")
                .description("Iphone 24")
                .price(BigDecimal.valueOf(124123.53))
                .build();
    }

}
