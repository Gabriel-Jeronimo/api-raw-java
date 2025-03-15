package com.api.app.utils;

import com.api.app.model.Product;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Json {
    public static Product parseJson(String request) {
        List<String> values = new ArrayList<>();

        String[] fields = request.split(",");

        for (String field : fields) {
            String[] parts = field.split(":");
            if (parts.length > 1) {
                String value = parts[1];

                try {
                    var a = value.trim().replace("\"", "").replace("}", "");
                    values.add(a);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        if (values.size() >= 3) {
            return new Product(
                    Integer.parseInt(values.get(0)),
                    values.get(1),
                    Double.parseDouble(values.get(2))
            );

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
                return new StringBuilder(l.stream().map(Json::convertToJson).toList().toString());
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
