package service;

import db.DBConnection;
import model.Stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockLogDAO {

    public void addStockLog(String ingredient, double quantity, String reason) {
        String sql = "INSERT INTO Stock_Logs (ingredient_id, change_amount, log_date, reason) VALUES (?, ?, SYSDATE, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int ingredientId = getIngredientId(ingredient); // 재료 ID 찾기
            pstmt.setInt(1, ingredientId);
            pstmt.setDouble(2, quantity);
            pstmt.setString(3, reason);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIngredientId(String ingredient) {
        // 재료 이름에 해당하는 ID를 찾는 메서드
        String sql = "SELECT ingredient_id FROM Ingredients WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ingredient);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ingredient_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 해당 재료가 없으면 -1 반환
    }

    public List<Stock> searchStockByName(String name) {
        List<Stock> stockList = new ArrayList<>();
        String query = """
        SELECT 
            i.name AS ingredient_name, 
            COALESCE(s.current_stock, 0) AS current_stock
        FROM 
            Ingredients i
        LEFT JOIN 
            Stock s ON i.ingredient_id = s.ingredient_id
        WHERE 
            i.name LIKE ?
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, "%" + name + "%"); // 재료명을 포함 검색
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Stock stock = new Stock();
                    stock.setIngredientName(rs.getString("ingredient_name")); // 재료명 설정
                    stock.setCurrentStock(rs.getDouble("current_stock"));      // 현재 재고량 설정

                    stockList.add(stock); // 리스트에 추가
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stockList;
    }


}
