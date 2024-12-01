package db;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("DB 연결 성공!");
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("Driver: " + conn.getMetaData().getDriverName());
        } catch (SQLException e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
        }
    }
}
