package ui.sales;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SalesUI extends JPanel {
    private Map<String, Integer> productPrices; // 상품 데이터
    private Map<String, Integer> cart; // 장바구니 데이터
    private JTable cartTable; // 장바구니 테이블
    private DefaultTableModel cartTableModel; // 테이블 모델
    private JLabel totalPriceLabel; // 총 금액 표시
    private JTextField quantityInput; // 숫자 입력 필드

    public SalesUI() {
        setLayout(new BorderLayout());

        // 상품 데이터 초기화
        productPrices = new HashMap<>();
        productPrices.put("불고기 버거", 5000);
        productPrices.put("치즈 버거", 5500);
        productPrices.put("감자튀김", 2000);
        productPrices.put("콜라", 1500);

        // 장바구니 초기화
        cart = new HashMap<>();

        // 좌측: 상품 패널
        JPanel productPanel = createProductPanel();

        // 우측: 장바구니 및 결제 패널
        JPanel cartPanel = createCartPanel();

        // JSplitPane으로 좌우 패널 분리
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productPanel, cartPanel);
        splitPane.setDividerLocation(600); // 좌측 60%, 우측 40% 비율 고정
        splitPane.setDividerSize(0); // 구분선 제거
        splitPane.setEnabled(false); // 구분선 이동 불가

        // 메인 레이아웃에 추가
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * 좌측 상품 패널 생성
     */
    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // 카테고리 탭 추가
        tabbedPane.addTab("버거", createProductTab("버거"));
        tabbedPane.addTab("사이드", createProductTab("사이드"));
        tabbedPane.addTab("음료", createProductTab("음료"));

        productPanel.add(tabbedPane, BorderLayout.CENTER);
        return productPanel;
    }

    /**
     * 상품 탭 생성 (고정 레이아웃)
     */
    private JPanel createProductTab(String category) {
        JPanel tabPanel = new JPanel(new GridLayout(4, 3, 10, 10)); // 4행 3열 고정
        int buttonsAdded = 0;

        // 상품 버튼 추가
        for (String product : productPrices.keySet()) {
            if (category.equals("버거") && product.contains("버거") ||
                    category.equals("사이드") && product.contains("감자튀김") ||
                    category.equals("음료") && product.contains("콜라")) {
                JButton productButton = new JButton(product + " (" + productPrices.get(product) + "원)");
                productButton.setPreferredSize(new Dimension(150, 50)); // 버튼 크기 고정
                productButton.addActionListener(e -> addToCart(product));
                tabPanel.add(productButton);
                buttonsAdded++;
            }
        }

        // 빈 버튼으로 남은 공간 채우기
        while (buttonsAdded < 12) {
            JButton emptyButton = new JButton();
            emptyButton.setEnabled(false); // 비활성화된 빈 버튼
            tabPanel.add(emptyButton);
            buttonsAdded++;
        }

        return tabPanel;
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

        JButton payButton = new JButton("Pay");
        payButton.setBackground(Color.GREEN);
        payButton.addActionListener(e -> processPayment());
        numberPadPanel.add(payButton);

        return numberPadPanel;
    }

    /**
     * 숫자 입력 처리
     */
    private void handleNumberInput(String input) {
        quantityInput.setText(input); // 새로 입력한 숫자로 덮어쓰기
    }

    /**
     * 장바구니 초기화 (Clear 버튼)
     */
    private void clearCart() {
        cart.clear();
        updateCartTable();
        JOptionPane.showMessageDialog(this, "장바구니가 초기화되었습니다!");
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
            } else {
                JOptionPane.showMessageDialog(this, "수량을 변경할 항목을 선택하세요!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요!");
        }
    }

    /**
     * 장바구니에 상품 추가
     */
    private void addToCart(String productName) {
        cart.put(productName, cart.getOrDefault(productName, 0) + 1);
        updateCartTable();
    }

    /**
     * 결제 처리
     */
    private void processPayment() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다!");
            return;
        }

        StringBuilder receipt = new StringBuilder();
        int total = 0;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            int price = productPrices.get(entry.getKey()) * entry.getValue();
            receipt.append(entry.getKey()).append(" * ").append(entry.getValue()).append(" = ").append(price).append("원\n");
            total += price;
        }

        receipt.append("\n총 결제 금액: ").append(total).append("원\n결제하시겠습니까?");

        int response = JOptionPane.showConfirmDialog(this, receipt.toString(), "결제 확인", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "결제가 완료되었습니다!");
            clearCart(); // 결제 후 장바구니 초기화
        }
    }

    /**
     * 장바구니 테이블 갱신
     */
    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        int total = 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            int price = productPrices.get(entry.getKey()) * entry.getValue();
            cartTableModel.addRow(new Object[]{entry.getKey(), entry.getValue(), price});
            total += price;
        }
        totalPriceLabel.setText("총 금액: " + total + "원");
    }
}
