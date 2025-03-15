package com.api.app;

import com.api.app.exception.BusinessException;
import com.api.app.model.Product;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static java.lang.System.in;

public final class Utils {
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

//    public static void writeErrorResponse(Exception e, HttpExchange exchange) throws IOException {
//        ErrorResponse error;
//        int statusCode;
//
//        if (e instanceof BusinessException) {
//            BusinessException be = (BusinessException) e;
//            error = new ErrorResponse(be.getMessage(), be.getCode(), be.getStatus());
//            statusCode = be.getStatus();
//        } else {
//            error = new ErrorResponse("Internal Server Error", "INTERNAL_ERROR", 500);
//            statusCode = 500;
//        }
//
//        writeResponse(jsonError, statusCode, exchange);
//    }

    public static Product parseJson(String request) {
        List<String> values = new ArrayList<>(); // Use ArrayList instead of immutable List.of()

        String[] fields = request.split(",");

        for (String field : fields) {
            String[] parts = field.split(":");
            if (parts.length > 1) {
                String value = parts[1];

                try {
                    var a = value.trim().replace("\"", "").replace("}", "");
                    values.add(a); // Add the cleaned value to the list
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        // Check if we have enough values before creating the Product
        if (values.size() >= 3) {
            var product = new Product(
                    Integer.valueOf(values.get(0)),
                    values.get(1),
                    Double.valueOf(values.get(2))
            );
            return product;

        } else {
            throw new IllegalArgumentException("Not enough values extracted from the request");
        }
    }

    public static StringBuilder convertToJson(Object object) {
        StringBuilder build = new StringBuilder();

        switch (object) {
            case Number n -> build.append(n);
            case String s -> build.append("\"").append(s).append("\"");
            case List l -> {
                return new StringBuilder(l.stream().map(Utils::convertToJson).toList().toString());
            }
            case Record r -> {
                return Arrays.stream(object.getClass().getDeclaredFields()).map(field -> {
                            try {
                                field.setAccessible(true);
                                return new AbstractMap.SimpleEntry<>(convertToJson(field.getName()), convertToJson(field.get(object)));
                            } catch (IllegalAccessException e) {
                                System.out.println(e);
                                throw new RuntimeException(e);
                            }
                        }).map(entry -> entry.getKey() + ":" + entry.getValue())
                        .collect(StringBuilder::new, (stringBuilder, s) -> {
                            if (stringBuilder.length() > 1) stringBuilder.append(",");
                            stringBuilder.append(s);
                        }, StringBuilder::append).insert(0, "{").append("}");
            }
            default -> throw new IllegalStateException("Unexpected value: " + object);
        }

        return build;
    }
}