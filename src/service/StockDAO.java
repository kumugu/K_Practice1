package service;

import db.DBConnection;
import model.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    // 전체 재고 조회
    public List<Stock> getAllStocks() {
        List<Stock> stockList = new ArrayList<>();
        String query = """
        SELECT 
            i.ingredient_id, i.name AS ingredient_name,
            COALESCE(s.current_stock, 0) AS current_stock,
            i.unit,
            MAX(o.order_date) AS last_order_date
        FROM 
            Ingredients i
        LEFT JOIN 
            Stock s ON i.ingredient_id = s.ingredient_id
        LEFT JOIN 
            Orders o ON i.ingredient_id = o.ingredient_id
        GROUP BY 
            i.ingredient_id, i.name, s.current_stock, i.unit
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Stock stock = new Stock();
                stock.setIngredientId(rs.getInt("ingredient_id"));
                stock.setIngredientName(rs.getString("ingredient_name"));
                stock.setCurrentStock(rs.getDouble("current_stock"));
                stock.setUnit(rs.getString("unit"));
                stock.setLastOrderDate(rs.getTimestamp("last_order_date"));

                stockList.add(stock);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stockList;
    }

    public List<Stock> searchStockByName(String name) {
        List<Stock> stockList = new ArrayList<>();
        String query = """
        SELECT i.name AS ingredient_name, s.current_stock, i.unit
        FROM Ingredients i
        LEFT JOIN Stock s ON i.ingredient_id = s.ingredient_id
        WHERE i.name LIKE ?
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Stock stock = new Stock();
                    stock.setIngredientName(rs.getString("ingredient_name"));
                    stock.setCurrentStock(rs.getDouble("current_stock"));
                    stock.setUnit(rs.getString("unit"));
                    stockList.add(stock);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stockList;
    }

    public void addStockForNewIngredient(int ingredientId) {
        String query = "INSERT INTO Stock (ingredient_id, current_stock) VALUES (?, 0)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, ingredientId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }





    // 재료명 검색
    public Stock getStockByName(String name) {
        String query = """
        SELECT 
            i.ingredient_id, i.name AS ingredient_name,
            COALESCE(s.current_stock, 0) AS current_stock,
            i.unit,
            MAX(o.order_date) AS last_order_date
        FROM 
            Ingredients i
        LEFT JOIN 
            Stock s ON i.ingredient_id = s.ingredient_id
        LEFT JOIN 
            Orders o ON i.ingredient_id = o.ingredient_id
        WHERE 
            i.name LIKE ?
        GROUP BY 
            i.ingredient_id, i.name, s.current_stock, i.unit
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Stock stock = new Stock();
                    stock.setIngredientId(rs.getInt("ingredient_id"));
                    stock.setIngredientName(rs.getString("ingredient_name"));
                    stock.setCurrentStock(rs.getDouble("current_stock"));
                    stock.setUnit(rs.getString("unit"));
                    stock.setLastOrderDate(rs.getTimestamp("last_order_date"));

                    return stock;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public void updateStock(int ingredientId, double quantity) {
        String query = """
        UPDATE Stock
        SET current_stock = current_stock + ?
        WHERE ingredient_id = ?
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDouble(1, quantity);
            stmt.setInt(2, ingredientId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("유효하지 않은 재료 ID: 재료가 존재하지 않습니다.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
