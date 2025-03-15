package com.api.app;

import com.api.app.controller.ProductController;
import com.api.app.service.ProductService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.api.app.utils.Http.handleRequest;

public class App {
    public static void main(String[] args) throws IOException {
        final int PORT = 8000;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            // Service registry - could be enhanced with dependency injection
            ProductController productController = new ProductController(new ProductService());

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    handleRequest(clientSocket, productController);
                } catch (IOException e) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }
}


