package service;

import db.DBConnection;
import model.Ingredient;
import ui.EventManager;
import ui.EventTypes;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {

    // 모든 활성화된 재료 조회
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT i.ingredient_id, i.category_id, i.name, i.unit_price, i.unit, c.category_name " +
                "FROM Ingredients i " +
                "JOIN Ingredient_Categories c ON i.category_id = c.category_id " +
                "WHERE i.is_deleted = 'N' " +
                "ORDER BY i.ingredient_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("unit_price"),
                        rs.getString("unit"),
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }



    /**
     * 재료 비활성화 처리
     */
    public boolean deactivateIngredient(int ingredientId) {
        String query = "UPDATE Ingredients SET is_deleted = 'Y' WHERE ingredient_id = ?";

        // 매개변수 유효성 검사
        if (ingredientId <= 0) {
            throw new IllegalArgumentException("Invalid ingredient ID: " + ingredientId);
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // 디버깅: 실행 전 로그 출력
            System.out.println("Executing query: " + query + " with ingredientId: " + ingredientId);

            // 매개변수 바인딩
            stmt.setInt(1, ingredientId);

            // 쿼리 실행
            int rowsUpdated = stmt.executeUpdate();

            // 디버깅: 결과 로그 출력
            System.out.println("Rows updated: " + rowsUpdated);

            return rowsUpdated > 0; // 업데이트 성공 여부 반환
        } catch (SQLException ex) {
            System.err.println("SQLState: " + ex.getSQLState());
            System.err.println("Error Code: " + ex.getErrorCode());
            ex.printStackTrace();
        }
        return false;
    }



    /**
     * 비활성화된 재료 조회
     */
    public List<Ingredient> getInactiveIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        String query = """
            SELECT 
                i.ingredient_id, i.name, i.unit, i.unit_price, 
                c.category_id, c.category_name 
            FROM Ingredients i
            JOIN Ingredient_Categories c ON i.category_id = c.category_id
            WHERE i.is_deleted = 'Y'
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

    /**
     * 재료 활성화 처리 (복구)
     */
    public boolean activateIngredient(int ingredientId) {
        String query = "UPDATE Ingredients SET is_deleted = 'N' WHERE ingredient_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, ingredientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 재료 추가
     */
    public int addIngredient(Ingredient ingredient) {
        String query = "INSERT INTO Ingredients (name, unit, unit_price, category_id, is_deleted) VALUES (?, ?, ?, ?, 'N')";
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
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * 재료 수정
     */
    public boolean updateIngredient(Ingredient ingredient) {
        String query = "UPDATE Ingredients SET name = ?, unit = ?, unit_price = ?, category_id = ? WHERE ingredient_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, ingredient.getName());
            stmt.setString(2, ingredient.getUnit());
            stmt.setBigDecimal(3, ingredient.getUnitPrice());
            stmt.setInt(4, ingredient.getCategoryId());
            stmt.setInt(5, ingredient.getIngredientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 재료 이름으로 단가 조회
     */
    public double getUnitPriceByName(String ingredientName) {
        String query = "SELECT unit_price FROM Ingredients WHERE name = ? AND is_deleted = 'N'";
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
        return -1; // 단가를 찾지 못했을 경우 -1 반환
    }

    /**
     * 재료 비활성화 (논리적 삭제)
     */
    public boolean deleteIngredient(int ingredientId) {
        String query = "UPDATE Ingredients SET is_deleted = 'Y' WHERE ingredient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, ingredientId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                EventManager.getInstance().notifyListeners(EventTypes.INGREDIENT_UPDATED);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 비활성화된 재료 데이터를 가져오기
     */
    public List<Object[]> getInactiveIngredientsAsObjects() throws SQLException {
        String query = "SELECT ingredient_id, name FROM Ingredients WHERE is_deleted = 'Y'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<Object[]> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("ingredient_id"),
                        rs.getString("name"),
                        false // 체크박스 기본값
                });
            }
            return results;
        }
    }

    /**
     * 재료 복구 처리
     */
    public boolean reactivateIngredient(int ingredientId) throws SQLException {
        String query = "UPDATE Ingredients SET is_deleted = 'N' WHERE ingredient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, ingredientId);
            return stmt.executeUpdate() > 0; // 성공 여부 반환
        }
    }



}
