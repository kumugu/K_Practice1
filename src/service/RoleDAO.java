package dao;

import db.DBConnection;
import model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    // 직급 조회
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM Roles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
}
