package ui.sales;

import model.Product;
import service.MenuDAO;
import service.ProductDAO;
import ui.EventManager;
import ui.EventTypes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductManagementUI extends JPanel {
    private ProductDAO productDAO;
    private MenuDAO menuDAO;
    private Map<String, JTable> categoryTables = new HashMap<>();
    private Map<String, DefaultTableModel> tableModels = new HashMap<>();
    private JTable focusedTable;

    public ProductManagementUI() {
        productDAO = new ProductDAO();
        menuDAO = new MenuDAO();

        setLayout(new BorderLayout());

//        // 이벤트 구독: "홈"에서 갱신될 때 트리거
//        EventManager.getInstance().subscribe(EventTypes.REFRESH_PRODUCTS, this::refreshAllTables);


        // UI 구성
        JPanel middlePanel = createMiddlePanel(); // 중단 패널 생성
        JPanel bottomPanel = createBottomPanel(); // 하단 패널 생성

        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 데이터 초기화
        loadInitialData();
    }

    private void loadInitialData() {
        refreshAllTables(); // 메시지 출력 없이 데이터 로드
    }
    /**
     * 중단 패널 - 카테고리별 등록 메뉴 조회
     */
    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 카테고리별 테이블을 수평으로 나열할 패널
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS)); // 가로로 테이블 배치

        // 카테고리 목록 가져오기
        List<String> categories = productDAO.getAllCategories();

        for (String category : categories) {
            // 테이블 모델 생성
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"순번", "상품명", "가격", "판매 메뉴 표시"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // "판매 메뉴 표시" 컬럼만 편집 가능 (체크박스)
                    return column == 3;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 3) {
                        return Boolean.class; // 체크박스 컬럼
                    }
                    return super.getColumnClass(columnIndex);
                }
            };

            // 테이블 생성 및 설정
            JTable categoryTable = new JTable(tableModel);

            // Map에 저장
            categoryTables.put(category, categoryTable);
            tableModels.put(category, tableModel);

            // 열 비율 설정 (순번: 10%, 상품명: 40%, 가격: 40%, 체크박스: 10%)
            setColumnWidths(categoryTable, 10, 40, 40, 10);

            // 포커스가 생길 때마다 focusedTable을 업데이트
            categoryTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    focusedTable = categoryTable;
                }
            });

            // 카테고리 패널 생성
            JPanel categoryPanel = new JPanel(new BorderLayout());
            categoryPanel.setBorder(BorderFactory.createTitledBorder(category));
            categoryPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);

            // 카테고리 패널 최대 크기 제한 (너비 조절)
            categoryPanel.setPreferredSize(new Dimension(250, categoryPanel.getPreferredSize().height));
            categoryPanel.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));

            // 테이블 패널에 추가
            tablePanel.add(categoryPanel);

            // 데이터 로드
            loadCategoryData(category, tableModel);
        }

        // 테이블 패널을 전체 스크롤 가능하도록 설정
        JScrollPane scrollPane = new JScrollPane(tablePanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 열 비율 설정
     */
    private void setColumnWidths(JTable table, int... percentages) {
        TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = table.getPreferredScrollableViewportSize().width;

        for (int i = 0; i < percentages.length; i++) {
            int preferredWidth = (int) (totalWidth * (percentages[i] / 100.0));
            columnModel.getColumn(i).setPreferredWidth(preferredWidth);
        }
    }

    /**
     * 특정 카테고리의 데이터를 테이블에 로드
     */
    private void loadCategoryData(String category, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // 기존 데이터 삭제
        List<Product> menuItems = productDAO.getProductsByCategory(category); // 카테고리별 상품 항목 조회

        int index = 1; // 순번
        for (Product menuItem : menuItems) {
            boolean isInMenu = menuDAO.isProductInMenu(menuItem.getProductId());
            tableModel.addRow(new Object[]{
                    index++, // 순번
                    menuItem.getName(), // 상품명
                    formatPrice(menuItem.getPrice()), // 가격 형식화
                    isInMenu // 판매 메뉴 표시 여부
            });
        }
    }

    /**
     * 가격 형식화
     */
    private String formatPrice(BigDecimal price) {
        return NumberFormat.getCurrencyInstance(Locale.KOREA).format(price); // 1,000원 형식으로 변환
    }

    /**
     * 하단 패널 - 이동 버튼 및 등록 버튼
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton moveUpButton = new JButton("위로");
        JButton moveDownButton = new JButton("아래로");
        JButton registerButton = new JButton("등록");

        // 갱신 버튼 추가
        JButton refreshButton = new JButton("갱신");
        refreshButton.addActionListener(e -> refreshAllTables()); // 갱신 버튼 이벤트

        // 버튼 추가
        panel.add(moveUpButton);
        panel.add(moveDownButton);
        panel.add(registerButton);
        panel.add(refreshButton); // 갱신 버튼 추가

        // "위로" 버튼 이벤트
        moveUpButton.addActionListener(e -> handleRowMovement(categoryTables, tableModels, -1));

        // "아래로" 버튼 이벤트
        moveDownButton.addActionListener(e -> handleRowMovement(categoryTables, tableModels, 1));

        // 등록 버튼 이벤트
        registerButton.addActionListener(e -> {
            for (Map.Entry<String, DefaultTableModel> entry : tableModels.entrySet()) {
                String category = entry.getKey();
                DefaultTableModel model = entry.getValue();
                JTable table = categoryTables.get(category);

                for (int row = 0; row < model.getRowCount(); row++) {
                    boolean isSelected = (boolean) model.getValueAt(row, 3); // 체크박스 값
                    int productId = productDAO.getProductIdByName((String) model.getValueAt(row, 1));

                    if (isSelected) {
                        // 메뉴 테이블에 등록
                        if (!menuDAO.isProductInMenu(productId)) {
                            int displayOrder = menuDAO.getNextDisplayOrder();
                            menuDAO.addMenuItem(productId, displayOrder);
                        }
                    } else {
                        // 메뉴 테이블에서 제거
                        if (menuDAO.isProductInMenu(productId)) {
                            menuDAO.deleteMenuItemByProductId(productId);
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "메뉴 설정이 업데이트되었습니다!");
            refreshAllTables();
        });

        return panel;
    }

    /**
     * 행 이동 핸들러
     */
    private void handleRowMovement(Map<String, JTable> categoryTables, Map<String, DefaultTableModel> tableModels, int direction) {
        JTable selectedTable = getSelectedTable();
        DefaultTableModel selectedModel = getSelectedTableModel();
        if (selectedTable != null && selectedModel != null) {
            moveRow(selectedTable, selectedModel, direction);
        }
    }

    /**
     * 행 이동
     */
    private void moveRow(JTable table, DefaultTableModel model, int direction) {
        if (table == null || model == null) {
            return; // 테이블이나 모델이 null이면 아무 작업도 하지 않음
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "이동할 항목을 선택하세요!", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int targetRow = selectedRow + direction;
        if (targetRow < 0 || targetRow >= model.getRowCount()) {
            return; // 테이블 범위를 벗어나면 아무 작업도 하지 않음
        }

        // 순번을 제외하고 상품명, 가격, 체크박스 상태만 이동
        Object currentProductName = model.getValueAt(selectedRow, 1);
        Object currentPrice = model.getValueAt(selectedRow, 2);
        Object currentCheckBox = model.getValueAt(selectedRow, 3);
        Object targetProductName = model.getValueAt(targetRow, 1);
        Object targetPrice = model.getValueAt(targetRow, 2);
        Object targetCheckBox = model.getValueAt(targetRow, 3);

        // 데이터 스왑 (순번 제외)
        model.setValueAt(targetProductName, selectedRow, 1);
        model.setValueAt(targetPrice, selectedRow, 2);
        model.setValueAt(targetCheckBox, selectedRow, 3);
        model.setValueAt(currentProductName, targetRow, 1);
        model.setValueAt(currentPrice, targetRow, 2);
        model.setValueAt(currentCheckBox, targetRow, 3);

        // 선택된 행 갱신
        table.setRowSelectionInterval(targetRow, targetRow);
    }

    /**
     * 현재 선택된 카테고리 테이블 가져오기
     */
    private JTable getSelectedTable() {
        for (Map.Entry<String, JTable> entry : categoryTables.entrySet()) {
            JTable table = entry.getValue();
            if (table.getSelectedRow() != -1) { // 선택된 행이 있을 경우 해당 테이블 반환
                return table;
            }
        }
        JOptionPane.showMessageDialog(this, "이동할 항목이 있는 테이블을 선택하세요!", "오류", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    /**
     * 현재 선택된 테이블 모델 가져오기
     */
    private DefaultTableModel getSelectedTableModel() {
        JTable selectedTable = getSelectedTable();
        if (selectedTable == null) {
            return null;
        }
        for (Map.Entry<String, JTable> entry : categoryTables.entrySet()) {
            if (entry.getValue() == selectedTable) {
                return tableModels.get(entry.getKey());
            }
        }
        JOptionPane.showMessageDialog(this, "테이블 모델을 찾을 수 없습니다!", "오류", JOptionPane.ERROR_MESSAGE);
        return null;
    }


    /**
     * 모든 테이블 데이터 갱신
     */
    private void refreshAllTables() {
        for (Map.Entry<String, DefaultTableModel> entry : tableModels.entrySet()) {
            String category = entry.getKey();
            DefaultTableModel tableModel = entry.getValue();
            loadCategoryData(category, tableModel); // 카테고리별 데이터 로드
        }
    }

}
