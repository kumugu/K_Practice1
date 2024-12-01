package service;

import db.DBConnection;
import model.ProductCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAO {

    /**
     * 상품 카테고리 전체 조회
     * @return 카테고리 목록
     */
    public List<ProductCategory> getAllCategories() {
        List<ProductCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM Product_Categories";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProductCategory category = new ProductCategory(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
