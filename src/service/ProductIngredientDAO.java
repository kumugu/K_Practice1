package service;

import db.DBConnection;
import model.ProductIngredient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductIngredientDAO {

    public void updateProductIngredients(int productId, DefaultTableModel ingredientTableModel, JComboBox<String> ingredientComboBox) {
        // 기존 재료 삭제
        deleteProductIngredients(productId);

        // 재료 목록에 추가된 데이터를 DB에 반영
        for (int i = 0; i < ingredientTableModel.getRowCount(); i++) {
            String ingredientName = (String) ingredientTableModel.getValueAt(i, 0);
            BigDecimal quantity = (BigDecimal) ingredientTableModel.getValueAt(i, 1);
            // 재료 이름을 기반으로 재료 ID 가져오기
            int ingredientId = ingredientComboBox.getSelectedIndex() + 1;

            ProductIngredient productIngredient = new ProductIngredient(productId, ingredientId, ingredientName, quantity);
            addProductIngredient(productIngredient);
        }
    }



    /**
     * 상품-재료 매핑 등록
     */
    public boolean addProductIngredient(ProductIngredient productIngredient) {
        String checkQuery = "SELECT COUNT(*) FROM Product_Ingredients WHERE product_id = ? AND ingredient_id = ?";
        String insertQuery = "INSERT INTO Product_Ingredients (product_id, ingredient_id, required_amount) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, productIngredient.getProductId());
            checkStmt.setInt(2, productIngredient.getIngredientId());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 중복된 데이터가 이미 존재하면 삽입을 하지 않음.
                    return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, productIngredient.getProductId());
                insertStmt.setInt(2, productIngredient.getIngredientId());
                insertStmt.setBigDecimal(3, productIngredient.getRequiredAmount());

                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 상품-재료 매핑 조회
     */
    public List<ProductIngredient> getProductIngredients(int productId) {
        List<ProductIngredient> productIngredients = new ArrayList<>();
        String query = "SELECT pi.product_id, pi.ingredient_id, i.name AS ingredient_name, pi.required_amount " +
                "FROM Product_Ingredients pi " +
                "JOIN Ingredients i ON pi.ingredient_id = i.ingredient_id " +
                "WHERE pi.product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductIngredient productIngredient = new ProductIngredient(
                            rs.getInt("product_id"),
                            rs.getInt("ingredient_id"),
                            rs.getString("ingredient_name"),
                            rs.getBigDecimal("required_amount")
                    );
                    productIngredients.add(productIngredient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productIngredients;
    }

    /**
     * 상품-재료 매핑 삭제
     */
    public boolean deleteProductIngredients(int productId) {
        String query = "DELETE FROM Product_Ingredients WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
