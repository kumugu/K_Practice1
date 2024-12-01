package service;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    /**
     * Menu 테이블에서 표시할 상품 목록 가져오기
     */
    public List<Product> getVisibleMenuItems() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT m.menu_id, p.product_id, c.category_name, p.name, p.price " +
                "FROM Menu m " +
                "JOIN Products p ON m.product_id = p.product_id " +
                "JOIN Product_Categories c ON p.category_id = c.category_id " +
                "WHERE m.is_visible = 'Y' " +
                "ORDER BY m.display_order";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("menu_id"), // menu_id를 사용하려면 Product 클래스 수정 필요
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("메뉴 항목 가져오기 중 오류 발생: " + e.getMessage());
        }
        return products;
    }

    /**
     * 특정 상품이 메뉴에 등록되어 있는지 확인
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
            System.err.println("메뉴 항목 확인 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 특정 상품에 해당하는 메뉴 항목 삭제
     */
    public boolean deleteMenuItemByProductId(int productId) {
        String query = "DELETE FROM Menu WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("메뉴 항목 삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * product_id로 메뉴 항목 삭제하기
     */
    public boolean deleteMenuByProductId(int productId) {
        String query = "DELETE FROM Menu WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0; // 성공적으로 삭제된 경우 true 반환

        } catch (SQLException e) {
            System.err.println("메뉴 항목 삭제 중 오류 발생: " + e.getMessage());
        }

        return false; // 실패 시 false 반환
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
            return false;
        }
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

    public Connection getConnection() {
        try {
            return DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("데이터베이스 연결 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null; // 예외 발생 시 null을 반환하거나 예외를 다시 던질 수 있음
        }
    }



    public int getProductIdByName(String productName) {
        String query = "SELECT product_id FROM Products WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        } catch (SQLException e) {
            System.err.println("상품 ID 조회 중 오류 발생: " + e.getMessage());
        }

        return -1; // 찾을 수 없을 경우 -1 반환
    }

}
