package service;

import db.DBConnection;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // 직원 등록
    public boolean registerEmployee(Employee employee) {
        String query = "INSERT INTO Employees (username, password_hash, name, contact, hire_date, role_id) " +
                "VALUES (?, ?, ?, ?, SYSDATE, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPasswordHash());
            stmt.setString(3, employee.getName());
            stmt.setString(4, employee.getContact());
            stmt.setInt(5, employee.getRoleId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 전체 직원 조회
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getDate("hire_date"),
                        rs.getInt("role_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    // 직원 이름으로 검색
    public List<Employee> searchEmployeesByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getDate("hire_date"),
                        rs.getInt("role_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    // 직원 정보 수정
    public boolean updateEmployee(int employeeId, String name, String contact, int roleId) {
        String query = "UPDATE Employees SET name = ?, contact = ?, role_id = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.setInt(3, roleId);
            stmt.setInt(4, employeeId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 직원 삭제
    public boolean deleteEmployee(int employeeId) {
        String query = "DELETE FROM Employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 급여 지급
    public boolean paySalary(int employeeId, double amount, String paymentType) {
        String query = "INSERT INTO Salaries (salary_id, employee_id, payment_date, payment_amount, payment_type) " +
                "VALUES (salary_seq.NEXTVAL, ?, SYSDATE, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setDouble(2, amount);
            stmt.setString(3, paymentType);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 역할 이름 가져오기
    public String getRoleNameById(int roleId) {
        String query = "SELECT role_name FROM Roles WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모든 역할 이름 가져오기
    public List<String> getAllRoleNames() {
        List<String> roleNames = new ArrayList<>();
        String query = "SELECT role_name FROM Roles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roleNames.add(rs.getString("role_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roleNames;
    }
}
