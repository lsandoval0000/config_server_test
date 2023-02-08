package com.learning.dto.mapper;

import com.learning.dto.ProductResponse;
import com.learning.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper extends BaseMapper<Product, ProductResponse> {
    @Override
    public Product convertToEntity(ProductResponse dto, Object... args) {
        Product entity = new Product();
        if (dto != null) {
            BeanUtils.copyProperties(dto, entity);
        }
        return entity;
    }

    @Override
    public ProductResponse convertToDto(Product entity, Object... args) {
        ProductResponse dto = new ProductResponse();
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }
}
