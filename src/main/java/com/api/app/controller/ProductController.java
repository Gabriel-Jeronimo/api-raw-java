package com.api.app.controller;

import com.api.app.model.Product;
import com.api.app.service.ProductService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.api.app.utils.Http.writeResponse;
import static com.api.app.utils.Json.convertToJson;
import static com.api.app.utils.Json.parseJson;

public class ProductController {
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public void handle(String method, String path, String requestBody, OutputStream out) throws IOException {
        switch (method) {
            case "POST":
                Product requestProduct = parseJson(requestBody);
                Product product = productService.createProduct(requestProduct);
                String creationResponse = convertToJson(product).toString();
                writeResponse(creationResponse, 201, "CREATED", out);
                break;
            case "GET":
                List<Product> products = productService.getProducts();
                String response = convertToJson(products).toString();
                writeResponse(response, 200, "OK", out);
                break;
            default:
                writeResponse("Method Not Allowed", 405, "METHOD NOT ALLOWED", out);
        }

    }


}
