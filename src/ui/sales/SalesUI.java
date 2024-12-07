package ui.sales;

import db.DBConnection;
import service.MenuDAO;
import model.Product;
import ui.EventManager;
import ui.EventTypes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SalesUI extends JPanel {

    private MenuDAO menuDAO;
    private JPanel productPanel;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JLabel totalPriceLabel;
    private JTextField quantityInput;
    private Map<String, Integer> cart = new HashMap<>();
    private Map<String, Integer> productPrices = new HashMap<>();

    public SalesUI() {
        setLayout(new BorderLayout());
        menuDAO = new MenuDAO();

        // 좌측: 상품 패널
        productPanel = createProductPanel();

        // 우측: 장바구니 및 결제 패널
        JPanel cartPanel = createCartPanel();

        // JSplitPane으로 좌우 패널 분리
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productPanel, cartPanel);
        splitPane.setDividerLocation(600); // 좌측 60%, 우측 40% 비율 고정
        splitPane.setDividerSize(0); // 구분선 제거
        splitPane.setEnabled(false); // 구분선 이동 불가

        // 메인 레이아웃에 추가
        add(splitPane, BorderLayout.CENTER);

        EventManager.getInstance().subscribe(EventTypes.PRODUCT_UPDATED, this::refreshProductPanel); // 적절한 이벤트 타입 추가
    }

    // 상품 패널 갱신 메서드
    private void refreshProductPanel() {
        productPanel.removeAll(); // 기존 패널 데이터 제거
        productPanel.add(createProductPanel()); // 새 데이터로 패널 갱신
        productPanel.revalidate();
        productPanel.repaint();
    }

    /**
     * 상품 패널 생성
     */
    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // Menu 테이블에서 데이터 가져오기
        List<Product> menuItems = menuDAO.getVisibleMenuItems();

        // 카테고리별로 그룹화
        Map<String, List<Product>> groupedByCategory = menuItems.stream()
                .collect(Collectors.groupingBy(Product::getCategoryName));

        // 카테고리별 탭 생성 (역순으로 정렬)
        List<Map.Entry<String, List<Product>>> categoryEntries = new ArrayList<>(groupedByCategory.entrySet());
        Collections.reverse(categoryEntries); // 역순 정렬

        for (Map.Entry<String, List<Product>> entry : categoryEntries) {
            String category = entry.getKey();
            List<Product> products = entry.getValue();
            JPanel categoryPanel = createCategoryPanel(products);
            tabbedPane.addTab(category, categoryPanel);
        }

        productPanel.add(tabbedPane, BorderLayout.CENTER);
        return productPanel;
    }


    /**
     * 카테고리 패널 생성
     */
    private JPanel createCategoryPanel(List<Product> products) {
        JPanel panel = new JPanel(new GridLayout(4, 3, 10, 10)); // 4행 3열 고정
        int buttonsAdded = 0;

        for (Product product : products) {
            JButton productButton = new JButton(product.getName() + " (" + product.getPrice() + "원)");
            productPrices.put(product.getName(), product.getPrice().intValue()); // 상품 가격 저장
            productButton.setPreferredSize(new Dimension(150, 50)); // 버튼 크기 고정

            // 여기서 액션 리스너 추가
            productButton.addActionListener(e -> {
                quantityInput.setText(""); // 수량 입력 필드 비우기
                addToCart(product.getName()); // 장바구니에 상품 추가
            });

            panel.add(productButton);
            buttonsAdded++;
        }

        // 빈 버튼으로 남은 공간 채우기
        while (buttonsAdded < 12) {
            JButton emptyButton = new JButton();
            emptyButton.setEnabled(false); // 비활성화된 빈 버튼
            panel.add(emptyButton);
            buttonsAdded++;
        }

        return panel;
    }

    /**
     * 우측 장바구니 및 결제 패널 생성
     */
    private JPanel createCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout());

        // 장바구니 테이블
        cartTableModel = new DefaultTableModel(new Object[]{"상품명", "수량", "금액"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);

        // 총 금액 라벨
        totalPriceLabel = new JLabel("총 금액: 0원", SwingConstants.RIGHT);
        totalPriceLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));

        // 숫자 패드 및 결제 버튼
        JPanel numberPadPanel = createNumberPad();

        // 레이아웃 배치
        cartPanel.add(cartScrollPane, BorderLayout.NORTH); // 상단: 장바구니 테이블
        cartPanel.add(totalPriceLabel, BorderLayout.CENTER); // 중앙: 총 금액
        cartPanel.add(numberPadPanel, BorderLayout.SOUTH); // 하단: 숫자 패드 및 결제 버튼
        return cartPanel;
    }

    /**
     * 숫자 패드 및 결제 버튼 생성
     */
    private JPanel createNumberPad() {
        JPanel numberPadPanel = new JPanel(new GridLayout(5, 3, 5, 5)); // 5행 3열 레이아웃
        quantityInput = new JTextField();

        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.addActionListener(e -> handleNumberInput(e.getActionCommand()));
            numberPadPanel.add(button);
        }

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearCart());
        numberPadPanel.add(clearButton);

        JButton zeroButton = new JButton("0");
        zeroButton.addActionListener(e -> handleNumberInput("0"));
        numberPadPanel.add(zeroButton);

        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> updateCartQuantity());
        numberPadPanel.add(enterButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshProductPanel());
        numberPadPanel.add(refreshButton);

        // 공백 추가
        JLabel spacer = new JLabel();
        numberPadPanel.add(spacer);

        JButton payButton = new JButton("Pay");
        payButton.setBackground(Color.GREEN);
        payButton.addActionListener(e -> processPayment());
        numberPadPanel.add(payButton);

        return numberPadPanel;
    }


    /**
     * 장바구니에 상품 추가
     */
    private void addToCart(String productName) {
        cart.put(productName, cart.getOrDefault(productName, 0) + 1);
        updateCartTable();
    }

    /**
     * 장바구니 수량 업데이트
     */
    private void updateCartQuantity() {
        try {
            int quantity = Integer.parseInt(quantityInput.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "0 이하의 값은 입력할 수 없습니다!");
                return;
            }
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                String productName = (String) cartTableModel.getValueAt(selectedRow, 0);
                cart.put(productName, quantity);
                updateCartTable();
                quantityInput.setText(""); // 수량 입력 필드를 비움
            } else {
                JOptionPane.showMessageDialog(this, "수량을 변경할 항목을 선택하세요!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요!");
        }
    }

    /**
     * 결제 처리
     */
    private void processPayment() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다!");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Sales 테이블에 데이터 삽입
            String insertSalesQuery = "INSERT INTO Sales (sale_id, product_id, sale_date, quantity, total_price) VALUES (sale_seq.NEXTVAL, ?, SYSDATE, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSalesQuery)) {
                for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                    int productId = menuDAO.getProductIdByName(entry.getKey());
                    int quantity = entry.getValue();
                    int totalPrice = productPrices.get(entry.getKey()) * quantity;

                    pstmt.setInt(1, productId);
                    pstmt.setInt(2, quantity);
                    pstmt.setInt(3, totalPrice);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "결제가 완료되었습니다!");
            clearCart(); // 결제 후 장바구니 초기화

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "결제 처리 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }



    private void clearCart() {
        // 장바구니 초기화
        cart.clear();
        cartTableModel.setRowCount(0); // 장바구니 테이블 초기화

        // 총 금액 초기화
        totalPriceLabel.setText("총 금액: 0원");

        // 수량 입력 필드 초기화
        quantityInput.setText(""); // 수량 입력 필드를 비움
    }

      private void handleNumberInput(String number) {
        // 수량 입력 필드에 값 설정 (기존 값 덮어쓰기)
        quantityInput.setText(number);
    }

    // 수량 입력 후 결제 버튼 클릭 시 처리하는 메서드에서
    private void updateCartWithQuantity(String productName, int quantity) {
        if (cart.containsKey(productName)) {
            // 기존 수량을 덮어쓰기
            cart.put(productName, quantity);
        } else {
            // 새 상품 추가
            cart.put(productName, quantity);
        }

        // 테이블 갱신
        updateCartTable();
    }

    // 장바구니 테이블 갱신 메서드
    private void updateCartTable() {
        cartTableModel.setRowCount(0); // 기존 테이블 데이터 초기화

        int totalPrice = 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            int productPrice = productPrices.get(productName);
            int price = productPrice * quantity;
            cartTableModel.addRow(new Object[]{productName, quantity, price});
            totalPrice += price;
        }

        // 총 금액 갱신
        totalPriceLabel.setText("총 금액: " + totalPrice + "원");
    }
}
