package ui.manager;

import service.EmployeeDAO;
import service.IngredientDAO;
import service.ProductDAO;
import ui.EventManager;
import ui.EventTypes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RecoveryUI extends JPanel {
    private JTable ingredientTable, productTable, employeeTable;
    private DefaultTableModel ingredientModel, productModel, employeeModel;

    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public RecoveryUI(String title) {
        setLayout(new BorderLayout());

        // 중단 영역 (카테고리별 목록 표시)
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단 영역 (버튼 패널)
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        EventManager.getInstance().subscribe(EventTypes.INGREDIENT_UPDATED, this::refreshIngredients);

        // 초기 데이터 로드
        refreshData();


    }

    private void refreshIngredients() {
        // 재료 데이터를 갱신하는 로직 작성
        ingredientModel.setRowCount(0); // 기존 데이터 초기화
        try {
            List<Object[]> ingredients = ingredientDAO.getInactiveIngredientsAsObjects();
            for (Object[] row : ingredients) {
                ingredientModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "재료 데이터를 갱신하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * 중단 영역 생성 - 카테고리별 테이블
     */
    private JPanel createMiddlePanel() {
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS)); // 가로로 나열

        // 재료 테이블
        ingredientModel = createTableModel();
        ingredientTable = new JTable(ingredientModel);
        addCategoryTable(middlePanel, "재료", ingredientTable);

        // 상품 테이블
        productModel = createTableModel();
        productTable = new JTable(productModel);
        addCategoryTable(middlePanel, "상품", productTable);

        // 직원 테이블
        employeeModel = createTableModel();
        employeeTable = new JTable(employeeModel);
        addCategoryTable(middlePanel, "직원", employeeTable);

        return middlePanel;
    }

    /**
     * 테이블 모델 생성
     */
    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new Object[]{"ID", "이름", "복구 선택"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 2 ? Boolean.class : String.class; // 마지막 열은 체크박스
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // 체크박스만 수정 가능
            }
        };
    }

    /**
     * 카테고리별 테이블 추가
     */
    private void addCategoryTable(JPanel parentPanel, String title, JTable table) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(title)); // 테이블 제목 설정
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 크기 조정
        tablePanel.setPreferredSize(new Dimension(300, 400)); // 너비 고정, 높이는 자동
        parentPanel.add(tablePanel);
    }

    /**
     * 하단 영역 생성 - 버튼 패널
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton viewAllButton = new JButton("전체조회");
        JButton recoverButton = new JButton("복구");

        // 전체조회 버튼 이벤트
        viewAllButton.addActionListener(e -> refreshData());

        // 복구 버튼 이벤트
        recoverButton.addActionListener(e -> recoverSelectedItems());

        bottomPanel.add(viewAllButton);
        bottomPanel.add(recoverButton);

        return bottomPanel;
    }

    /**
     * 데이터 갱신
     */
    public void refreshData() {
        try {
            // 재료 데이터 로드
            ingredientModel.setRowCount(0); // 기존 데이터 초기화
            List<Object[]> ingredients = ingredientDAO.getInactiveIngredientsAsObjects();
            for (Object[] row : ingredients) {
                ingredientModel.addRow(row);
            }

            // 상품 데이터 로드
            productModel.setRowCount(0); // 기존 데이터 초기화
            List<Object[]> products = productDAO.getInactiveProductsAsObjects();
            for (Object[] row : products) {
                productModel.addRow(row);
            }

            // 직원 데이터 로드
            employeeModel.setRowCount(0); // 기존 데이터 초기화
            List<Object[]> employees = employeeDAO.getInactiveEmployeesAsObjects();
            for (Object[] row : employees) {
                employeeModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "데이터를 로드하는 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 복구 선택된 항목 처리
     */
    private void recoverSelectedItems() {
        try {
            // 재료 복구
            for (int i = 0; i < ingredientModel.getRowCount(); i++) {
                boolean isSelected = (boolean) ingredientModel.getValueAt(i, 2); // 체크박스 값
                if (isSelected) {
                    int ingredientId = Integer.parseInt((String) ingredientModel.getValueAt(i, 0)); // ID 값
                    ingredientDAO.reactivateIngredient(ingredientId);
                }
            }

            // 상품 복구
            for (int i = 0; i < productModel.getRowCount(); i++) {
                boolean isSelected = (boolean) productModel.getValueAt(i, 2); // 체크박스 값
                if (isSelected) {
                    int productId = Integer.parseInt((String) productModel.getValueAt(i, 0)); // ID 값
                    productDAO.reactivateProduct(productId);
                }
            }

            // 직원 복구
            for (int i = 0; i < employeeModel.getRowCount(); i++) {
                boolean isSelected = (boolean) employeeModel.getValueAt(i, 2); // 체크박스 값
                if (isSelected) {
                    int employeeId = Integer.parseInt((String) employeeModel.getValueAt(i, 0)); // ID 값
                    employeeDAO.reactivateEmployee(employeeId);
                }
            }

            // 복구 후 데이터 갱신
            JOptionPane.showMessageDialog(this, "복구가 완료되었습니다!");
            refreshData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "복구 처리 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }


    public JPanel getPanel() {
        return this;
    }
}
