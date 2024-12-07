// InventoryUI.java
package ui.inventory;

import model.Ingredient;
import model.Stock;
import service.IngredientDAO;
import service.OrderDAO;
import service.StockDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryUI extends JPanel {

    private DefaultTableModel stockTableModel;
    private JComboBox<String> ingredientComboBox;
    private JTextField supplierField, quantityField, searchField;
    private JTable stockTable;

    private final IngredientDAO ingredientDAO = new IngredientDAO();

    public InventoryUI() {
        setLayout(new BorderLayout());

        // 상단: 주문 기능 패널
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중단: 재고 조회 패널
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단: 전체 조회 버튼 패널
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // 데이터 초기 로드
        refreshUI(); // UI 초기화
    }

    // 상단 패널: 주문 기능
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 재료 콤보박스
        ingredientComboBox = new JComboBox<>();
        refreshIngredientComboBox(); // 초기 로드
        panel.add(new JLabel("재료:"));
        panel.add(ingredientComboBox);

        // 거래처 입력
        supplierField = new JTextField(15);
        panel.add(new JLabel("거래처:"));
        panel.add(supplierField);

        // 주문 수량 입력
        quantityField = new JTextField(10);
        panel.add(new JLabel("주문 수량:"));
        panel.add(quantityField);

        // 주문 버튼
        JButton orderButton = new JButton("주문");
        orderButton.addActionListener(e -> handleOrder());
        panel.add(orderButton);

        return panel;
    }

    // 중단 패널: 재고 조회
    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");

        searchPanel.add(new JLabel("재료명:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(e -> searchStockData());

        panel.add(searchPanel, BorderLayout.NORTH);

        stockTableModel = new DefaultTableModel(new Object[]{"ID", "재료명", "현재 재고", "단위", "최근 주문 일시"}, 0);
        stockTable = new JTable(stockTableModel);
        panel.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        return panel;
    }

    // 하단 패널: 전체 조회 버튼
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 조회 버튼 추가
        JButton viewButton = new JButton("조회");
        viewButton.addActionListener(e -> refreshUI()); // 클릭 시 UI 갱신
        panel.add(viewButton);

        return panel;
    }

    // UI 갱신 메서드
    public void refreshUI() {
        refreshIngredientComboBox(); // 재료 콤보박스 갱신
        loadStockData(); // 재고 테이블 갱신
    }

    // 재료 콤보박스 갱신
    private void refreshIngredientComboBox() {
        ingredientComboBox.removeAllItems();
        List<Ingredient> ingredients = ingredientDAO.getAllIngredients(); // DAO를 통해 재료 목록 가져오기
        for (Ingredient ingredient : ingredients) {
            ingredientComboBox.addItem(ingredient.getName());
        }
    }

    // 재고 데이터 로드
    private void loadStockData() {
        StockDAO stockDAO = new StockDAO();
        List<Stock> stockList = stockDAO.getAllStocks(); // 데이터 로드

        stockTableModel.setRowCount(0); // 기존 데이터 초기화

        for (Stock stock : stockList) {
            stockTableModel.addRow(new Object[]{
                    stock.getIngredientId(), // PK
                    stock.getIngredientName(),
                    stock.getCurrentStock(),
                    stock.getUnit(),
                    stock.getLastOrderDate()
            });
        }
    }

    // 주문 처리
    private void handleOrder() {
        String ingredientName = (String) ingredientComboBox.getSelectedItem();
        String supplier = supplierField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (ingredientName == null || supplier.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요!");
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityText);

            // IngredientDAO를 사용하여 단가 가져오기
            double unitPrice = ingredientDAO.getUnitPriceByName(ingredientName);

            // 재료 ID 가져오기
            StockDAO stockDAO = new StockDAO();
            Stock stock = stockDAO.getStockByName(ingredientName);

            if (stock == null) {
                JOptionPane.showMessageDialog(this, "재료를 찾을 수 없습니다.");
                return;
            }

            int ingredientId = stock.getIngredientId();

            // 주문 추가
            OrderDAO orderDAO = new OrderDAO();
            orderDAO.addOrder(ingredientId, supplier, quantity, unitPrice);

            JOptionPane.showMessageDialog(this, "주문이 성공적으로 처리되었습니다!");

            // UI 업데이트
            refreshUI();
            quantityField.setText("");
            supplierField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "수량은 숫자로 입력하세요!");
        }
    }

    // 재료명 검색
    private void searchStockData() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadStockData(); // 검색어가 없을 경우 전체 조회
            return;
        }

        StockDAO stockDAO = new StockDAO();
        List<Stock> stockList = stockDAO.searchStockByName(searchText);

        stockTableModel.setRowCount(0); // 테이블 초기화

        for (Stock stock : stockList) {
            stockTableModel.addRow(new Object[]{
                    stock.getIngredientId(),
                    stock.getIngredientName(),
                    stock.getCurrentStock(),
                    stock.getUnit(),
                    stock.getLastOrderDate()
            });
        }
    }
}
