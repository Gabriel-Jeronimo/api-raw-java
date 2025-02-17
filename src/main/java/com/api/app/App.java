package com.api.app;

import com.api.app.product.ProductHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        final int PORT = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // TODO: Inject productService into productHandler?
        server.createContext("/products", new ProductHandler());

        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on port " + PORT);
    }


}


