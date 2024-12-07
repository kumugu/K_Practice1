package ui.manager;

import javax.swing.*;
import java.awt.*;

public class ManagerUI extends JPanel {

    private RegisterIngredientUI registerIngredientUI;
    private RegisterProductUI registerProductUI;
    private EmployeesManagementUI employeesManagementUI;
    private ReportUI reportUI;
    private ProfitLossUI profitLossUI;
    private RecoveryUI recoveryUI;

    public ManagerUI() {
        setLayout(new BorderLayout());

        // TabbedPane 생성
        JTabbedPane tabbedPane = new JTabbedPane();

        try {
            // UI 초기화
            registerProductUI = new RegisterProductUI();
            registerIngredientUI = new RegisterIngredientUI(registerProductUI);

            registerProductUI = new RegisterProductUI();       // 독립적으로 생성
            employeesManagementUI = new EmployeesManagementUI();
            reportUI = new ReportUI();
            profitLossUI = new ProfitLossUI();
            recoveryUI = new RecoveryUI("복구 관리");

            // Tab 추가
            tabbedPane.addTab("재료 등록", registerIngredientUI);
            tabbedPane.addTab("상품 등록", registerProductUI);
            tabbedPane.addTab("직원 관리", employeesManagementUI);
            tabbedPane.addTab("보고서", reportUI);
            tabbedPane.addTab("손익 계산서", profitLossUI);
            tabbedPane.addTab("복구 관리", recoveryUI.getPanel());

            // **탭 변경 시 자동 갱신 제거**
            // 수동 갱신으로 변경

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 연결에 문제가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}
