package service;

import db.DBConnection;
import model.Ingredient;
import ui.EventManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {

    // 모든 재료 조회 (카테고리 포함)
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        String query = """
            SELECT 
                i.ingredient_id, i.name, i.unit, i.unit_price, 
                c.category_id, c.category_name 
            FROM Ingredients i
            JOIN Ingredient_Categories c ON i.category_id = c.category_id
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("unit_price"),
                        rs.getString("unit"),
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );
                ingredientList.add(ingredient);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ingredientList;
    }

    // 재료 추가
    public int addIngredient(Ingredient ingredient) {
        String query = "INSERT INTO Ingredients (name, unit, unit_price, category_id) VALUES (?, ?, ?, ?)";
        String generatedColumns[] = { "ingredient_id" }; // 반환할 열 지정

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, generatedColumns)) {

            stmt.setString(1, ingredient.getName());
            stmt.setString(2, ingredient.getUnit());
            stmt.setBigDecimal(3, ingredient.getUnitPrice());
            stmt.setInt(4, ingredient.getCategoryId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);

                        // 재료 추가 후 이벤트 발생
                        EventManager.getInstance().notifyListeners();

                        return generatedId; // 반환된 ID 가져오기
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // 실패 시 -1 반환
    }


    public double getUnitPriceByName(String ingredientName) {
        String query = """
        SELECT unit_price
        FROM Ingredients
        WHERE name = ?
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ingredientName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("unit_price");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0.0; // 기본값 반환 (단가가 없을 경우)
    }



    public boolean updateIngredient(Ingredient ingredient) {
        String query = "UPDATE Ingredients SET name = ?, unit = ?, unit_price = ?, category_id = ? WHERE ingredient_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ingredient.getName());
            stmt.setString(2, ingredient.getUnit());
            stmt.setBigDecimal(3, ingredient.getUnitPrice());
            stmt.setInt(4, ingredient.getCategoryId());
            stmt.setInt(5, ingredient.getIngredientId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteIngredient(int ingredientId) {
        String query = "DELETE FROM Ingredients WHERE ingredient_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ingredientId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public Ingredient getIngredientWithStock(String ingredientName) {
        String query = """
    SELECT i.ingredient_id, i.name, i.unit_price, i.unit, s.current_stock
    FROM Ingredients i
    LEFT JOIN Stock s ON i.ingredient_id = s.ingredient_id
    WHERE i.name = ?
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ingredientName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ingredient(
                            rs.getInt("ingredient_id"),
                            rs.getString("name"),
                            BigDecimal.valueOf(rs.getDouble("unit_price")),
                            rs.getString("unit"),
                            rs.getInt("current_stock")
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    // 특정 재료 검색 (카테고리 포함)
    public List<Ingredient> searchIngredientsByName(String name) {
        List<Ingredient> ingredientList = new ArrayList<>();
        String query = """
            SELECT 
                i.ingredient_id, i.name, i.unit, i.unit_price, 
                c.category_id, c.category_name 
            FROM Ingredients i
            JOIN Ingredient_Categories c ON i.category_id = c.category_id
            WHERE i.name LIKE ?
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getInt("ingredient_id"),
                            rs.getString("name"),
                            rs.getBigDecimal("unit_price"),
                            rs.getString("unit"),
                            rs.getInt("category_id"),
                            rs.getString("category_name")
                    );
                    ingredientList.add(ingredient);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ingredientList;
    }
}
