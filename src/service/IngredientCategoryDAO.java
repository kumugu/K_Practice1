package service;

import db.DBConnection;
import model.IngredientCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientCategoryDAO {

    // 모든 카테고리 조회
    public List<IngredientCategory> getAllCategories() {
        List<IngredientCategory> categories = new ArrayList<>();
        String query = "SELECT category_id, category_name FROM Ingredient_Categories";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new IngredientCategory(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categories;
    }

    public List<String> getCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        String query = "SELECT category_name FROM Ingredient_Categories";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categoryNames.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryNames;
    }


    // 카테고리 추가
    public void addCategory(String categoryName) {
        String query = "INSERT INTO Ingredient_Categories (category_name) VALUES (?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 카테고리 삭제
    public void deleteCategory(int categoryId) {
        String query = "DELETE FROM Ingredient_Categories WHERE category_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
