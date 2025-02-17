package com.api.app;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

public final class Utils {
    public static void writeResponse(String response, int statusCode, HttpExchange exchange) throws IOException {
        if (response.charAt(response.length() - 1) != '\n') {
            response += "\n";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
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