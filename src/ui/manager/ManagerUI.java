package ui.manager;

import javax.swing.*;

public class ManagerUI extends JPanel {

    public ManagerUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();

        // 탭 추가
        tabbedPane.addTab("상품 등록", new RegisterProductUI());
        tabbedPane.addTab("재료 등록", new RegisterIngredientUI());
        tabbedPane.addTab("직원 관리", new UserManagementUI());
        tabbedPane.addTab("보고서", new ReportUI());

        // 탭 패널 추가
        add(tabbedPane);
    }
}
