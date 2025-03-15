package com.api.app.repository;

import com.api.app.config.DatabaseConfig;
import com.api.app.exception.BusinessException;
import com.api.app.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final DatabaseConfig dbConfig;

    public ProductRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public Product save(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.name());
            stmt.setDouble(2, product.price());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new BusinessException("Creating product failed", "DB_ERROR", 500);
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Product(generatedKeys.getInt(1), product.name(), product.price());
                } else {
                    throw new BusinessException("Creating product failed, no ID obtained", "DB_ERROR", 500);
                }
            }
        } catch (SQLException e) {
            throw new BusinessException("Database error: " + e.getMessage(), "DB_ERROR", 500);
        }
    }

    
    public List<Product> findAll() {
        String sql = "SELECT id, name, price FROM products";
        List<Product> products = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));
                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            throw new BusinessException("Database error: " + e.getMessage(), "DB_ERROR", 500);
        }
    }
}