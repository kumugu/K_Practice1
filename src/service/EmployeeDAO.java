package service;

import db.DBConnection;
import model.Employee;
import ui.EventManager;
import ui.EventTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    /**
     * 직원 등록
     */
    public boolean registerEmployee(Employee employee) {
        String query = "INSERT INTO Employees (username, password_hash, name, contact, hire_date, role_id, is_deleted) " +
                "VALUES (?, ?, ?, ?, SYSDATE, ?, 'N')";
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

    /**
     * 모든 활성화된 직원 조회
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees WHERE is_deleted = 'N'";

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

    /**
     * 비활성화된 직원 조회
     */
    public List<Employee> getInactiveEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees WHERE is_deleted = 'Y'";

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

    /**
     * 직원 비활성화 처리
     */
    public boolean deactivateEmployee(int employeeId) {
        String query = "UPDATE Employees SET is_deleted = 'Y' WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 직원 활성화 처리 (복구)
     */
    public boolean activateEmployee(int employeeId) {
        String query = "UPDATE Employees SET is_deleted = 'N' WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 급여 지급
     */
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
            System.err.println("급여 지급 중 오류 발생: " + e.getMessage());
        }
        return false;
    }


    /**
     * 직원 정보 수정
     */
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE Employees SET name = ?, contact = ?, role_id = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getContact());
            stmt.setInt(3, employee.getRoleId());
            stmt.setInt(4, employee.getEmployeeId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                EventManager.getInstance().notifyListeners(EventTypes.EMPLOYEE_UPDATED);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 직원 이름으로 검색
     */
    public List<Employee> searchEmployeesByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees WHERE name LIKE ? AND is_deleted = 'N'";

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

    /**
     * 모든 역할 이름 조회
     */
//    public List<String> getAllRoleNames() {
//        List<String> roles = new ArrayList<>();
//        String query = "SELECT role_id || ' - ' || role_name AS role_display FROM Roles";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                roles.add(rs.getString("role_name"));
//            }
//        } catch (SQLException e) {
//            System.err.println("역할 이름 조회 중 오류 발생: " + e.getMessage());
//        }
//        return roles;
//    }

    public List<String> getAllRoleNames() {
        List<String> roles = new ArrayList<>();
        String query = "SELECT role_name FROM Roles";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
        } catch (SQLException e) {
            System.err.println("역할 이름 조회 중 오류 발생: " + e.getMessage());
        }
        return roles;
    }

    // 유효성 검증
    public boolean isRoleIdValid(int roleId) {
        String query = "SELECT COUNT(*) FROM Roles WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("역할 ID 유효성 검사 중 오류 발생: " + e.getMessage());
        }
        return false;
    }





    /**
     * 역할 ID로 역할 이름 조회
     */
    public String getRoleNameById(int roleId) {
        String query = "SELECT role_name FROM Roles WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("역할 이름 조회 중 오류 발생: " + e.getMessage());
        }
        return null; // 역할 ID가 없을 경우 null 반환
    }

    /**
     * 직원 삭제
     */
    public boolean deleteEmployee(int employeeId) {
        String query = "UPDATE Employees SET is_deleted = 'Y' WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("직원 삭제 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 비활성화된 직원 데이터를 가져오기
     */
    public List<Object[]> getInactiveEmployeesAsObjects() throws SQLException {
        String query = "SELECT employee_id, username FROM Employees WHERE is_deleted = 'Y'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<Object[]> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("employee_id"),
                        rs.getString("username"),
                        false // 체크박스 기본값
                });
            }
            return results;
        }
    }

    /**
     * 직원 복구 처리
     */
    public boolean reactivateEmployee(int employeeId) {
        String query = "UPDATE Employees SET is_deleted = 'N' WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("직원 복구 중 오류 발생: " + e.getMessage());
        }
        return false;
    }


}
