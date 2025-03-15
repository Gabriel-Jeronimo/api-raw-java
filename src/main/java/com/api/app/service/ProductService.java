package com.api.app.service;

import com.api.app.model.Product;
import com.api.app.repository.ProductRepository;

import java.util.List;

import static com.api.app.Utils.convertToJson;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public String getProducts() {
        List<Product> products = this.productRepository.findAll();
        StringBuilder productsConverted = convertToJson(products);
        return productsConverted.toString();
    }

    public String createProduct(Product product) {
        Product productCreated = this.productRepository.save(product);

        return convertToJson(productCreated).toString();
    }
}
