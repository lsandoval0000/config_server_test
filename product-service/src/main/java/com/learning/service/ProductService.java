package com.learning.service;

import com.learning.dto.ProductRequest;
import com.learning.dto.ProductResponse;
import com.learning.dto.mapper.ProductResponseMapper;
import com.learning.model.Product;
import com.learning.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductResponseMapper productResponseMapper;

    public void createProduct(ProductRequest productRequest) {
        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productResponseMapper.convertToDtoList(products);
    }
}
