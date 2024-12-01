package ui;

import ui.inventory.InventoryUI;
import ui.manager.ManagerUI;
import ui.sales.ProductManagementUI;
import ui.sales.SalesUI;
import ui.login.LoginUI;
import ui.login.RegisterUI;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

public class MainUI extends JFrame {

    public static final String LOBBY_PANEL = "로비";
    public static final String SALES_PANEL = "판매";
    public static final String MANAGER_PANEL = "관리자 메뉴";
    public static final String PRODUCTS_PANEL = "상품 관리";
    public static final String INVENTORY_PANEL = "재고 관리";
    public static final String LOGIN_PANEL = "로그인";
    public static final String REGISTER_PANEL = "회원 가입";

    private static JPanel centerPanel; // 화면 전환을 관리할 중앙 패널
    private boolean isLoggedIn = false; // 로그인 상태를 저장하는 변수

    public MainUI() throws SQLException {
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
        JMenu homeMenu = new JMenu("🏠홈");
        homeMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isUserLoggedIn()) {
                    EventManager.getInstance().notifyListeners(); // 전체 갱신 이벤트 트리거
                    showPanel(LOBBY_PANEL); // "홈" 클릭 시 로비 화면으로 이동
                } else {
                    JOptionPane.showMessageDialog(null, "먼저 로그인해주세요.");
                    showPanel(LOGIN_PANEL); // 로그인 화면으로 이동
                }
            }
        });

        // 파일 메뉴
        JMenu fileMenu = new JMenu("파일");
        JMenuItem printItem = new JMenuItem("인쇄");
        printItem.addActionListener(e -> printCurrentPanel()); // 인쇄 기능

        JMenuItem logoutItem = new JMenuItem("로그아웃");
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "로그아웃하시겠습니까?", "로그아웃 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                isLoggedIn = false; // 로그인 상태 해제
                JOptionPane.showMessageDialog(null, "로그아웃되었습니다.");
                showPanel(LOGIN_PANEL); // 로그인 화면으로 이동
            }
        });

        JMenuItem exitItem = new JMenuItem("종료");
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "종료하시겠습니까?", "종료 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // 애플리케이션 종료
            }
        });

        fileMenu.add(printItem);
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 업무 메뉴
        JMenu workMenu = new JMenu("업무");
        JMenuItem salesItem = new JMenuItem("판매 관리");
        salesItem.addActionListener(e -> {
            if (isUserLoggedIn()) {
                EventManager.getInstance().notifyListeners(); // 갱신 이벤트 발생
                showPanel(SALES_PANEL);
            } else {
                JOptionPane.showMessageDialog(null, "먼저 로그인해주세요.");
                showPanel(LOGIN_PANEL);
            }
        });
        workMenu.add(salesItem);

        JMenuItem productItem = new JMenuItem("상품 관리");
        productItem.addActionListener(e -> {
            if (isUserLoggedIn()) {
                EventManager.getInstance().notifyListeners(); // 갱신 이벤트 발생
                showPanel(PRODUCTS_PANEL);
            } else {
                JOptionPane.showMessageDialog(null, "먼저 로그인해주세요.");
                showPanel(LOGIN_PANEL);
            }
        });
        workMenu.add(productItem);

        JMenuItem inventoryItem = new JMenuItem("재고 관리");
        inventoryItem.addActionListener(e -> {
            if (isUserLoggedIn()) {
                EventManager.getInstance().notifyListeners(); // 갱신 이벤트 발생
                showPanel(INVENTORY_PANEL);
            } else {
                JOptionPane.showMessageDialog(null, "먼저 로그인해주세요.");
                showPanel(LOGIN_PANEL);
            }
        });
        workMenu.add(inventoryItem);

        JMenuItem managerItem = new JMenuItem("관리자 메뉴");
        managerItem.addActionListener(e -> {
            if (isUserLoggedIn()) {
                EventManager.getInstance().notifyListeners(); // 갱신 이벤트 발생
                showPanel(MANAGER_PANEL);
            } else {
                JOptionPane.showMessageDialog(null, "먼저 로그인해주세요.");
                showPanel(LOGIN_PANEL);
            }
        });
        workMenu.add(managerItem);

        // 도움말 메뉴
        JMenu helpMenu = new JMenu("도움말");
        JMenuItem infoItem = new JMenuItem("정보");
        infoItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "<html><h3>애플리케이션 정보</h3>" +
                            "<p>이 애플리케이션은 제품 관리, 판매 관리, 재고 관리, 직원 관리를 위해 설계되었습니다.</p>" +
                            "<p>개발자: [Your Name]</p>" +
                            "<p>버전: 1.0.0</p>" +
                            "<p>특징: 사용자 친화적인 UI, 실시간 데이터베이스 연동, 다양한 관리 기능 제공</p>" +
                            "<br><p>기타 정보:</p>" +
                            "<ul>" +
                            "<li>현재 시간을 확인하려면 상단 메뉴바를 확인하세요!</li>" +
                            "<li>로그인 후 다양한 관리 기능을 사용할 수 있습니다.</li>" +
                            "</ul></html>",
                    "애플리케이션 정보", JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(infoItem);

        // 메뉴바에 메뉴 추가
        menuBar.add(homeMenu);
        menuBar.add(fileMenu);
        menuBar.add(workMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar); // 메뉴바 설정
    }



    /**
     * 현재 패널을 인쇄하는 메서드
     */
    private void printCurrentPanel() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print Current Panel");

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                centerPanel.printAll(g2d);
                return PAGE_EXISTS;
            }
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 사용자 로그인 여부 확인 메서드
     * @return true면 로그인됨, false면 로그인되지 않음
     */
    private boolean isUserLoggedIn() {
        return isLoggedIn;
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
        try {
            new MainUI();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 성공 시 호출되는 메서드
     * - 로그인 상태를 true로 설정
     */
    public void loginSuccess() {
        isLoggedIn = true;
    }
}
