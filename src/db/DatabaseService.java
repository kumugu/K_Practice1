package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseService {

    /**
     * 사용자 역할 ID 가져오기
     * @param username 사용자 이름
     * @param password 비밀번호
     * @return role_id (로그인 성공 시), -1 (실패 시)
     */
    public int getUserRoleId(String username, String password) {
        String query = "SELECT role_id FROM Employees WHERE username = ? AND password_hash = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // 입력된 비밀번호를 해싱 후 비교

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("role_id"); // 역할 ID 반환
            } else {
                System.out.println("로그인 실패: 사용자 정보를 찾을 수 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 인증 실패
    }

    /**
     * 비밀번호 해싱 (SHA-256)
     * @param password 평문 비밀번호
     * @return 해싱된 비밀번호
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
