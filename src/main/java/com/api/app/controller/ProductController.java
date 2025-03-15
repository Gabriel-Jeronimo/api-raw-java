package com.api.app.product;

import com.api.app.Utils;
import com.api.app.model.Product;
import com.api.app.service.ProductService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import static com.api.app.Utils.*;

public class ProductController {
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public void handle(String method, String path, String requestBody, OutputStream out) throws IOException {
        switch (method) {
            case "POST":
                Product requestProduct = parseJson(requestBody);
                String creationResponse = productService.createProduct(requestProduct);
                Utils.writeResponse(creationResponse, 201, "CREATED", out);
                break;
            case "GET":
                String response = productService.getProducts();
                Utils.writeResponse(response, 200, "OK", out);
                break;
            default:
                Utils.writeResponse("Method Not Allowed", 405, "METHOD NOT ALLOWED", out);

        }

    }


}
