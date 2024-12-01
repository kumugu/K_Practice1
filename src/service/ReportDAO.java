package service;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    // 판매 내역 보고서
    public List<String[]> getSalesReportWithCondition(String dateCondition) {
        String query = "SELECT p.name AS product_name, s.quantity, s.total_price, s.sale_date " +
                "FROM Sales s " +
                "JOIN Products p ON s.product_id = p.product_id " +
                dateCondition;

        return executeQuery(query);
    }

    // 주문 내역 보고서
    public List<String[]> getOrdersReportWithCondition(String dateCondition) {
        String query = "SELECT i.name AS ingredient_name, o.quantity, o.total_price, o.order_date, o.supplier " +
                "FROM Orders o " +
                "JOIN Ingredients i ON o.ingredient_id = i.ingredient_id " +
                dateCondition;

        return executeQuery(query);
    }

    // 급여 내역 보고서
    public List<String[]> getSalariesReportWithCondition(String dateCondition) {
        String query = "SELECT e.name AS employee_name, s.payment_amount, s.payment_date " +
                "FROM Salaries s " +
                "JOIN Employees e ON s.employee_id = e.employee_id " +
                dateCondition.replace("s.sale_date", "s.payment_date");

        return executeQuery(query);
    }

    // 손익 계산 보고서 (전체/조건 조회)
    public List<String[]> getProfitLossReportWithCondition(String dateCondition) {
        String query = "SELECT report_date, total_sales_revenue, total_product_cost, " +
                "total_ingredient_cost, total_salary_expenses, total_profit " +
                "FROM ProfitLossReport " + dateCondition;

        return executeQuery(query);
    }

    // 손익 계산 보고서 (월별 조회)
    public List<String[]> getProfitLossReportByMonth(String dateCondition) {
        String query = "SELECT report_date, total_sales_revenue, " +
                "(total_sales_revenue - total_product_cost) AS gross_profit, " +
                "total_product_cost, total_ingredient_cost, total_salary_expenses, " +
                "(total_product_cost + total_ingredient_cost + total_salary_expenses) AS total_costs, " +
                "(total_sales_revenue - (total_product_cost + total_ingredient_cost + total_salary_expenses)) AS net_profit " +
                "FROM ProfitLossReport WHERE TO_CHAR(report_date, 'YYYY-MM') = ?";

        List<String[]> reportData = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dateCondition);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reportData.add(new String[]{
                            rs.getString("report_date"),
                            rs.getString("total_sales_revenue"),
                            rs.getString("gross_profit"),
                            rs.getString("total_product_cost"),
                            rs.getString("total_ingredient_cost"),
                            rs.getString("total_salary_expenses"),
                            rs.getString("total_costs"),
                            rs.getString("net_profit")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportData;
    }

    // 날짜 조건 생성 메서드
    public String getDateCondition(boolean isSelected, String year, String month) {
        if (!isSelected) {
            return ""; // 전체 조회
        }

        StringBuilder dateCondition = new StringBuilder("WHERE 1=1");
        if (year != null) {
            dateCondition.append(" AND EXTRACT(YEAR FROM report_date) = ").append(year);
        }
        if (month != null) {
            int monthNumber = Integer.parseInt(month.replace("월", "").trim());
            dateCondition.append(" AND EXTRACT(MONTH FROM report_date) = ").append(monthNumber);
        }
        return dateCondition.toString();
    }

    // 공통 쿼리 실행 메서드
    private List<String[]> executeQuery(String query) {
        List<String[]> data = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 손익 계산서 데이터를 자동으로 삽입하는 메서드
    public void updateProfitLossReportAfterTransaction() {
        String salesQuery = "SELECT SUM(total_price) FROM Sales"; // 총 매출
        String costQuery = "SELECT SUM(cost_price) FROM Products"; // 총 제품 원가
        String ingredientCostQuery = "SELECT SUM(total_price) FROM Orders"; // 총 재료 비용
        String salaryQuery = "SELECT SUM(payment_amount) FROM Salaries"; // 총 급여 비용

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 판매 매출 계산
            ResultSet rsSales = stmt.executeQuery(salesQuery);
            rsSales.next();
            double totalSalesRevenue = rsSales.getDouble(1); // 매출 합계

            // 제품 원가 계산
            ResultSet rsCost = stmt.executeQuery(costQuery);
            rsCost.next();
            double totalProductCost = rsCost.getDouble(1); // 원가 합계

            // 재료 비용 계산
            ResultSet rsIngredientCost = stmt.executeQuery(ingredientCostQuery);
            rsIngredientCost.next();
            double totalIngredientCost = rsIngredientCost.getDouble(1); // 재료 비용 합계

            // 급여 비용 계산
            ResultSet rsSalary = stmt.executeQuery(salaryQuery);
            rsSalary.next();
            double totalSalaryExpenses = rsSalary.getDouble(1); // 급여 합계

            // 순이익 계산
            double totalProfit = totalSalesRevenue - (totalProductCost + totalIngredientCost + totalSalaryExpenses);

            // ProfitLossReport 테이블에 삽입
            String insertQuery = "INSERT INTO ProfitLossReport " +
                    "(report_date, total_sales_revenue, total_product_cost, total_ingredient_cost, total_salary_expenses, total_profit) " +
                    "VALUES (CURRENT_DATE, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setDouble(1, totalSalesRevenue);
                pstmt.setDouble(2, totalProductCost);
                pstmt.setDouble(3, totalIngredientCost);
                pstmt.setDouble(4, totalSalaryExpenses);
                pstmt.setDouble(5, totalProfit);
                pstmt.executeUpdate();  // 데이터 삽입
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
