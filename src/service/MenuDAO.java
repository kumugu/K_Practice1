package service;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    /**
     * 활성화된 메뉴 항목 가져오기
     */
    public List<Product> getVisibleMenuItems() {
        List<Product> products = new ArrayList<>();
        String query = """
            SELECT m.menu_id, p.product_id, c.category_name, p.name, p.price 
            FROM Menu m 
            JOIN Products p ON m.product_id = p.product_id 
            JOIN Product_Categories c ON p.category_id = c.category_id 
            WHERE m.is_visible = 'Y'
            ORDER BY m.display_order
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("menu_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("활성화된 메뉴 항목 조회 중 오류 발생: " + e.getMessage());
        }
        return products;
    }

    /**
     * 비활성화된 메뉴 항목 가져오기
     */
    public List<Product> getInactiveMenuItems() {
        List<Product> products = new ArrayList<>();
        String query = """
            SELECT m.menu_id, p.product_id, c.category_name, p.name, p.price 
            FROM Menu m 
            JOIN Products p ON m.product_id = p.product_id 
            JOIN Product_Categories c ON p.category_id = c.category_id 
            WHERE m.is_visible = 'N'
            ORDER BY m.display_order
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("menu_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("비활성화된 메뉴 항목 조회 중 오류 발생: " + e.getMessage());
        }
        return products;
    }

    /**
     * 메뉴 항목 비활성화 (is_visible = 'N')
     */
    public boolean deactivateMenuItem(int productId) {
        String query = "UPDATE Menu SET is_visible = 'N' WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("메뉴 항목 비활성화 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 메뉴 항목 활성화 (복구: is_visible = 'Y')
     */
    public boolean activateMenuItem(int productId) {
        String query = "UPDATE Menu SET is_visible = 'Y' WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("메뉴 항목 활성화 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 새로운 메뉴 항목 추가
     */
    public boolean addMenuItem(int productId, int displayOrder) {
        String query = "INSERT INTO Menu (menu_id, product_id, display_order, is_visible) " +
                "VALUES (menu_seq.NEXTVAL, ?, ?, 'Y')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, displayOrder);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("메뉴 항목 추가 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 다음 display_order 가져오기
     */
    public int getNextDisplayOrder() {
        String query = "SELECT COALESCE(MAX(display_order), 0) + 1 AS next_order FROM Menu";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("next_order");
            }
        } catch (SQLException e) {
            System.err.println("다음 display_order 조회 중 오류 발생: " + e.getMessage());
        }

        return 1; // 오류 발생 시 기본값 1 반환
    }

    /**
     * 특정 상품이 메뉴에 포함되어 있는지 확인
     */
    /**
     * 특정 상품이 메뉴에 포함되어 있는지 확인
     */
    public boolean isProductInMenu(int productId) {
        String query = "SELECT COUNT(*) FROM Menu WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("메뉴에서 상품 확인 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 특정 상품을 메뉴에서 제거
     */
    public boolean deleteMenuItemByProductId(int productId) {
        String query = "DELETE FROM Menu WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("메뉴에서 상품 제거 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 상품명으로 상품 ID 조회
     */
    public int getProductIdByName(String productName) {
        String query = "SELECT product_id FROM Products WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("상품명으로 상품 ID 조회 중 오류 발생: " + e.getMessage());
        }
        return -1; // 상품을 찾지 못했을 경우 -1 반환
    }
}
