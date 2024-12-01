package ui;

import javax.swing.*;
import java.awt.*;

/**
 * 로비 화면
 * - 애플리케이션의 메인 로비 역할을 합니다.
 * - 주요 메뉴 버튼을 배치하여 다른 화면으로 이동할 수 있도록 설계합니다.
 */
public class LobbyUI extends JPanel {
    private MainUI mainUI; // MainUI 필드 선언

    public LobbyUI(MainUI mainUI) {
        this.mainUI = mainUI;

        // 기본 레이아웃 설정
        setLayout(new BorderLayout());

        // 배경색 설정
        setBackground(new Color(153, 50, 205)); // 보라색 배경

        // 중앙 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20)); // 2행 2열, 간격 20px
        buttonPanel.setOpaque(false); // 배경 투명 설정

        // 버튼 생성
        JButton salesButton = createStyledButton("판매 관리");
        JButton productButton = createStyledButton("상품 관리");
        JButton inventoryButton = createStyledButton("재고 관리");
        JButton managerButton = createStyledButton("관리자 메뉴");

        // 버튼 추가
        buttonPanel.add(salesButton);
        buttonPanel.add(productButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(managerButton);

        // 이벤트 리스너 추가
        salesButton.addActionListener(e -> MainUI.showPanel(MainUI.SALES_PANEL)); // 판매 관리로 이동
        productButton.addActionListener(e -> MainUI.showPanel(MainUI.PRODUCTS_PANEL)); // 상품 관리로 이동
        inventoryButton.addActionListener(e -> MainUI.showPanel(MainUI.INVENTORY_PANEL)); // 재고 관리로 이동
        // 관리자 메뉴 버튼에 접근 제한 추가
        managerButton.addActionListener(e -> {
            Integer userRoleId = mainUI.getCurrentUserRoleId();
            System.out.println("현재 사용자 role_id: " + userRoleId);

            if (userRoleId != null && userRoleId == 3) { // role_id가 3이면 관리자
                MainUI.showPanel(MainUI.MANAGER_PANEL);
            } else {
                JOptionPane.showMessageDialog(null,
                        "관리자 메뉴는 매니저만 접근 가능합니다.",
                        "접근 제한",
                        JOptionPane.WARNING_MESSAGE);
            }
        });


        // 버튼 패널을 중앙에 추가
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * 스타일이 적용된 버튼 생성
     * @param text 버튼 텍스트
     * @return 스타일이 적용된 JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        button.setBackground(new Color(255, 165, 0)); // 오렌지색
        button.setForeground(Color.WHITE); // 텍스트 흰색
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }
}
