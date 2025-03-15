package com.api.app;

i5mport com.api.app.product.ProductController;
import com.api.app.service.ProductService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) throws IOException {
        final int PORT = 8000;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            // Service registry - could be enhanced with dependency injection
            ProductController productController = new ProductController(new ProductService());

            while (true) {
                try {
                    // Accept incoming connection
                    Socket clientSocket = serverSocket.accept();

                    // Handle request in a new thread
                    handleRequest(clientSocket, productController);
                } catch (IOException e) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket clientSocket, ProductController productController) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();

            if (requestLine == null) return;

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            String header;
            int contentLength = 0;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.substring("content-length:".length()).trim());
                }
            }

            String requestBody = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                int bytesRead = in.read(bodyChars, 0, contentLength);
                if (bytesRead > 0) {
                    requestBody = new String(bodyChars, 0, bytesRead);
                }
            }

            if (path.startsWith("/products")) {
                productController.handle(method, path, requestBody, out);
            } else {
                Utils.writeResponse( "Not Found", 404,"Text not found", out);
            }
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}


