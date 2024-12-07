package service;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /**
     * 상품 등록
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Products (product_id, category_id, name, price) VALUES (product_seq.NEXTVAL, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"product_id"})) {

            stmt.setInt(1, product.getCategoryId());
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setProductId(rs.getInt(1)); // 생성된 product_id 설정
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 모든 상품 조회
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = """
                SELECT p.product_id, p.category_id, c.category_name, p.name, p.price
                FROM Products p
                JOIN Product_Categories c ON p.category_id = c.category_id
                WHERE p.is_deleted = 'N'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("상품 조회 중 오류 발생: " + e.getMessage());
        }
        return products;
    }

    /**
     * 카테고리 ID로 상품 조회
     */
    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> products = new ArrayList<>();
        String query = """
                SELECT p.product_id, p.category_id, c.category_name, p.name, p.price
                FROM Products p
                JOIN Product_Categories c ON p.category_id = c.category_id
                WHERE c.category_name = ? AND p.is_deleted = 'N'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categoryName); // 카테고리 이름 설정
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("product_id"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("name"),
                            rs.getBigDecimal("price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("상품 조회 중 오류 발생: " + e.getMessage());
        }
        return products;
    }

    /**
     * 비활성화된 상품 조회
     */
    public List<Product> getInactiveProducts() {
        List<Product> inactiveProducts = new ArrayList<>();
        String query = """
                SELECT p.product_id, p.category_id, c.category_name, p.name, p.price
                FROM Products p
                JOIN Product_Categories c ON p.category_id = c.category_id
                WHERE p.is_deleted = 'Y'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inactiveProducts.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("비활성화 상품 조회 중 오류 발생: " + e.getMessage());
        }
        return inactiveProducts;
    }


    /**
     * 카테고리 목록 조회
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT category_name FROM Product_Categories";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            System.err.println("카테고리 조회 중 오류 발생: " + e.getMessage());
        }

        return categories;
    }

    /**
     * 카테고리 이름으로 ID 조회
     */
    public int getCategoryIdByName(String categoryName) {
        String query = "SELECT category_id FROM Product_Categories WHERE category_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("카테고리 ID 조회 중 오류 발생: " + e.getMessage());
        }
        return -1; // 존재하지 않을 경우 -1 반환
    }

    /**
     * 상품 수정
     */
    public boolean updateProduct(Product product) {
        String query = "UPDATE Products SET category_id = ?, name = ?, price = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, product.getCategoryId());
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getProductId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("상품 수정 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 상품 논리적 삭제
     */
    public boolean deleteProduct(int productId) {
        String query = "UPDATE Products SET is_deleted = 'Y' WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("상품 삭제 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 활성화된 상품 조회
     */
    public List<Product> getActiveProducts() {
        List<Product> activeProducts = new ArrayList<>();
        String query = """
                SELECT p.product_id, p.category_id, c.category_name, p.name, p.price
                FROM Products p
                JOIN Product_Categories c ON p.category_id = c.category_id
                WHERE p.is_deleted = 'N'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                activeProducts.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("활성화된 상품 조회 중 오류 발생: " + e.getMessage());
        }
        return activeProducts;
    }

    /**
     * 특정 상품 조회 (이름 기준)
     */
    public Product getProductByName(String name) {
        String query = """
                SELECT p.product_id, p.category_id, c.category_name, p.name, p.price
                FROM Products p
                JOIN Product_Categories c ON p.category_id = c.category_id
                WHERE p.name = ? AND p.is_deleted = 'N'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                );
            }
        } catch (SQLException e) {
            System.err.println("상품 이름으로 조회 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    /**
     * 상품명으로 product_id 가져오기
     */
    public int getProductIdByName(String productName) {
        String query = "SELECT product_id FROM Products WHERE name = ? AND is_deleted = 'N'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, productName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("상품 ID 가져오는 중 오류 발생: " + e.getMessage());
        }
        return -1; // 상품을 찾지 못했을 경우 -1 반환
    }

    /**
     * 상품 비활성화 (논리적 삭제)
     */
    public boolean deactivateProduct(int productId) {
        String query = "UPDATE Products SET is_deleted = 'Y' WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("상품 비활성화 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 비활성화된 상품 데이터를 가져오기
     */
    public List<Object[]> getInactiveProductsAsObjects() throws SQLException {
        String query = "SELECT product_id, name FROM Products WHERE is_deleted = 'Y'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<Object[]> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("product_id"),
                        rs.getString("name"),
                        false // 체크박스 기본값
                });
            }
            return results;
        }
    }

    /**
     * 상품 복구 처리
     */
    public boolean reactivateProduct(int productId) throws SQLException {
        String query = "UPDATE Products SET is_deleted = 'N' WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0; // 성공 여부 반환
        }
    }

}
