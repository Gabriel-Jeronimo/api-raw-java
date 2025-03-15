package com.api.app.utils;

import com.api.app.controller.ProductController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Http {
    public static void writeResponse(String body, int statusCode, String statusText, OutputStream out) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body + "\r\n";

        out.write(response.getBytes());
        out.flush();
    }

    private static HashMap<String, String> getHeaders(BufferedReader in) throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();
        String buffer;

        while ((buffer = in.readLine()) != null && !buffer.isEmpty()) {
            String[] header = buffer.split(": ");
            headers.put(header[0], header[1]);
        }

        return headers;
    }

    public static void handleRequest(Socket clientSocket, ProductController productController) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();

            if (requestLine == null) return;

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            HashMap<String, String> headers = getHeaders(in);

            int contentLength = Integer.parseInt(headers.get("Content-Length"));
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
                writeResponse("Not Found", 404, "Text not found", out);
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
