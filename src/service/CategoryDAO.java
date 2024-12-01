package service;

import db.DBConnection;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT category_id, category_name FROM Product_Categories";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("category_id"), rs.getString("category_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
}
