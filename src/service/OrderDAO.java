package service;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDAO {

    // 주문 추가
    public void addOrder(int ingredientId, String supplier, double quantity, double unitPrice) {
        String orderQuery = """
        INSERT INTO Orders (order_id, ingredient_id, supplier, order_date, quantity, total_price)
        VALUES (order_seq.NEXTVAL, ?, ?, SYSDATE, ?, ?)
    """;

        double totalPrice = quantity * unitPrice;

        try (Connection connection = DBConnection.getConnection()) {
            // Start transaction
            connection.setAutoCommit(false);

            // Insert order
            try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery)) {
                orderStmt.setInt(1, ingredientId);
                orderStmt.setString(2, supplier);
                orderStmt.setDouble(3, quantity);
                orderStmt.setDouble(4, totalPrice);
                orderStmt.executeUpdate();
            }

            // 직접 Stock 테이블 업데이트 (트리거와 중복 방지)
            String stockUpdateQuery = """
            UPDATE Stock
            SET current_stock = current_stock + ?
            WHERE ingredient_id = ?
        """;

            try (PreparedStatement stockStmt = connection.prepareStatement(stockUpdateQuery)) {
                stockStmt.setDouble(1, quantity);
                stockStmt.setInt(2, ingredientId);
                int updatedRows = stockStmt.executeUpdate();

                // 업데이트된 행이 없다면 예외 처리
                if (updatedRows == 0) {
                    throw new SQLException("Failed to update stock for ingredient ID: " + ingredientId);
                }
            }

            // Commit transaction
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Order processing failed", ex);
        }
    }

}
