package com.api.app.product;

import com.api.app.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

import static com.api.app.Utils.convertToJson;
import static com.api.app.Utils.writeResponse;

public class ProductHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "POST":
                System.out.println("post");

                break;
            case "GET":

                String response = getProducts();
                Utils.writeResponse(response, 200, exchange);
                break;
            default:
                System.out.println("nao existe");

        }
    }


    private String getProducts() {
        var product = List.of(new Product(1, "produto foda", 12.1), new Product(1, "produto foda", 12.1));
        StringBuilder productResponse = convertToJson(product);;
        return productResponse.toString();
    }





    public record Product(long id, String name, double price) {
    }
}

//{
//        "id":1,
//        "name":"produto foda",
//        "price":12.1
//        }
