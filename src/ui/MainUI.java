package ui;

import ui.inventory.InventoryUI;
import ui.manager.ManagerUI;
import ui.sales.ProductManagementUI;
import ui.sales.SalesUI;
import ui.login.LoginUI;
import ui.login.RegisterUI;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    public static final String LOBBY_PANEL = "로비";
    public static final String SALES_PANEL = "판매";
    public static final String MANAGER_PANEL = "관리자 메뉴";
    public static final String PRODUCTS_PANEL = "상품 관리";
    public static final String INVENTORY_PANEL = "재고 관리";
    public static final String LOGIN_PANEL = "로그인";
    public static final String REGISTER_PANEL = "회원 가입";

    private static JPanel centerPanel; // 화면 전환을 관리할 중앙 패널

    public MainUI() {
        // 기본 설정
        setTitle("메인 화면");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메뉴바 생성
        createMenuBar();

        // 메인 콘텐츠 패널 설정
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // 중앙 패널(CardLayout) 생성 및 추가
        centerPanel = new JPanel(new CardLayout());
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // 패널 추가
        centerPanel.add(new LoginUI(this), LOGIN_PANEL);
        centerPanel.add(new RegisterUI(this), REGISTER_PANEL);
        centerPanel.add(new LobbyUI(), LOBBY_PANEL);
        centerPanel.add(new SalesUI(), SALES_PANEL);
        centerPanel.add(new ManagerUI(), MANAGER_PANEL);
        centerPanel.add(new ProductManagementUI(), PRODUCTS_PANEL);
        centerPanel.add(new InventoryUI(), INVENTORY_PANEL);

        // 초기 화면 설정
        showPanel(LOGIN_PANEL);

        setVisible(true); // 화면 표시
    }

    /**
     * 메뉴바 생성
     * - 홈, 파일, 업무, 도움말 메뉴를 추가합니다.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 홈 메뉴
        JMenu homeMenu = new JMenu("✨홈");
        homeMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showPanel("로비"); // "홈" 클릭 시 로비 화면으로 이동
            }
        });

        // 파일 메뉴
        JMenu fileMenu = new JMenu("파일");
        JMenuItem printItem = new JMenuItem("인쇄");
        JMenuItem logoutItem = new JMenuItem("로그아웃");
        JMenuItem exitItem = new JMenuItem("종료");
        fileMenu.add(printItem);
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 업무 메뉴
        JMenu workMenu = new JMenu("업무");
        JMenuItem salesItem = new JMenuItem("판매 관리");
        JMenuItem productItem = new JMenuItem("상품 관리");
        JMenuItem inventoryItem = new JMenuItem("재고 관리");
        JMenuItem managerItem = new JMenuItem("관리자 메뉴");
        workMenu.add(salesItem);
        workMenu.add(productItem);
        workMenu.add(inventoryItem);
        workMenu.add(managerItem);

        // 도움말 메뉴
        JMenu helpMenu = new JMenu("도움말");
        JMenuItem infoItem = new JMenuItem("정보");
        helpMenu.add(infoItem);

        // 메뉴바에 메뉴 추가
        menuBar.add(homeMenu);
        menuBar.add(fileMenu);
        menuBar.add(workMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar); // 메뉴바 설정
    }



    /**
     * 패널 전환 메서드
     * - CardLayout을 사용해 화면을 전환합니다.
     * @param panelName 표시할 패널 이름
     */
    public static void showPanel(String panelName) {
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, panelName);
    }

    /**
     * 메인 메서드
     * - 애플리케이션 실행.
     */
    public static void main(String[] args) {
        new MainUI();
    }
}
