package ui.manager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ManagerUI extends JPanel {

    public ManagerUI() {
        setLayout(new BorderLayout());

        // TabbedPane 생성
        JTabbedPane tabbedPane = new JTabbedPane();

        try {
            // 상품 등록 탭 생성
            RegisterProductUI registerProductUI = new RegisterProductUI();

            // 재료 등록 탭 생성 (RegisterProductUI를 매개변수로 전달)
            RegisterIngredientUI registerIngredientUI = new RegisterIngredientUI(registerProductUI);
            EmployeesManagementUI employeesManagementUI = new EmployeesManagementUI();
            ReportUI reportUI = new ReportUI();
            ProfitLossUI profitLossUI = new ProfitLossUI();

            // Tab 추가
            tabbedPane.addTab("재료 등록", registerIngredientUI);
            tabbedPane.addTab("상품 등록", registerProductUI);
            tabbedPane.addTab("직원 관리", employeesManagementUI);
            tabbedPane.addTab("보고서", reportUI);
            tabbedPane.addTab("손익 계산서", profitLossUI);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 연결에 문제가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}
