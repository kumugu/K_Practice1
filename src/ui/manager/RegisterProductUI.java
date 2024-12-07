package ui.manager;

import model.Ingredient;
import model.Product;
import model.ProductCategory;
import model.ProductIngredient;
import service.IngredientDAO;
import service.ProductCategoryDAO;
import service.ProductDAO;
import service.ProductIngredientDAO;
import service.MenuDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegisterProductUI extends JPanel {

    // UI 컴포넌트
    private JComboBox<String> categoryComboBox, ingredientComboBox; // 카테고리와 재료 선택 콤보박스
    private JTextField nameField, priceField, ingredientQuantityField; // 상품 이름, 가격, 재료 소요량 입력 필드
    private JTable productTable, ingredientTable; // 상품 및 재료 테이블
    private DefaultTableModel productTableModel, ingredientTableModel; // 테이블 모델

    // DAO 인스턴스
    private final ProductDAO productDAO = new ProductDAO();
    private final ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
    private final ProductIngredientDAO ingredientDAO = new ProductIngredientDAO();
    private final IngredientDAO ingredientDAO2 = new IngredientDAO(); // 재료 추가를 위한 DAO
    private MenuDAO menuDAO;
    private DefaultTableModel tableModel;


    // 생성자
    public RegisterProductUI() throws SQLException {
        setLayout(new BorderLayout());
        menuDAO = new MenuDAO(); // 메뉴 DAO 초기화

        // 테이블 초기화
        tableModel = new DefaultTableModel(new Object[]{"ID", "카테고리", "이름", "가격", "단위"}, 0);
        productTable = new JTable(tableModel);


        // 상단: 상품 기본 정보 입력
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중단: 재료 추가 영역
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단: 상품 조회 및 수정/삭제
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        loadIngredientsIntoComboBox(); // 콤보박스 초기화
        loadProductCategories(); // 카테고리 초기화
        refreshProductTable(); // 상품 테이블 초기화
    }
    /**
     * 상단 패널 - 상품 기본 정보 입력
     * 상품 등록을 위한 필드와 버튼 구성
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        panel.add(new JLabel("카테고리:"));
        categoryComboBox = new JComboBox<>();
        panel.add(categoryComboBox);

        panel.add(new JLabel("상품 이름:"));
        nameField = new JTextField(10);
        panel.add(nameField);

        panel.add(new JLabel("상품 가격:"));
        priceField = new JTextField(10);
        panel.add(priceField);

        JButton registerButton = new JButton("등록");
        registerButton.addActionListener(e -> registerProduct());
        panel.add(registerButton);

        return panel;
    }


    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상단: 재료 추가 입력
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new JLabel("재료:"));
        ingredientComboBox = new JComboBox<>();
        inputPanel.add(ingredientComboBox);

        inputPanel.add(new JLabel("소요량:"));
        ingredientQuantityField = new JTextField(5);
        inputPanel.add(ingredientQuantityField);

        JButton addIngredientButton = new JButton("추가");
        addIngredientButton.addActionListener(e -> addIngredientToList());
        inputPanel.add(addIngredientButton);

        JButton deleteIngredientButton = new JButton("삭제");
        deleteIngredientButton.addActionListener(e -> deleteSelectedIngredient());
        inputPanel.add(deleteIngredientButton); // 삭제 버튼 추가

        panel.add(inputPanel, BorderLayout.NORTH);

        // 하단: 재료 목록 테이블
        ingredientTableModel = new DefaultTableModel(new Object[]{"재료", "소요량"}, 0);
        ingredientTable = new JTable(ingredientTableModel);
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }



    /**
     * "추가" 버튼 클릭 시 테이블에 재료 추가
     */
    private void addIngredientToList() {
        String selectedIngredient = (String) ingredientComboBox.getSelectedItem();
        String quantityText = ingredientQuantityField.getText();

        if (selectedIngredient == null || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "재료와 소요량을 입력하세요!");
            return;
        }

        try {
            BigDecimal quantity = new BigDecimal(quantityText);
            ingredientTableModel.addRow(new Object[]{selectedIngredient, quantity}); // 테이블에 추가
            ingredientQuantityField.setText(""); // 입력 필드 초기화
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "소요량은 숫자로 입력하세요!");
        }
    }

    /**
     * 하단 패널 - 상품 조회, 수정, 삭제
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상품 테이블 유지
        productTableModel = new DefaultTableModel(new Object[]{"ID", "카테고리", "상품명", "가격"}, 0);
        productTable = new JTable(productTableModel);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 조회 버튼
        JButton viewButton = new JButton("조회");
        viewButton.addActionListener(e -> refreshUI()); // 전체 UI 갱신

        // 수정 버튼
        JButton editButton = new JButton("수정");
        editButton.addActionListener(e -> editSelectedProduct());

        // 삭제 버튼
        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> deleteSelectedProduct());

        // 버튼 추가
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * UI 전반적인 갱신
     */
    private void refreshUI() {
        loadIngredientsIntoComboBox(); // 재료 콤보박스 갱신
        refreshProductTable(); // 상품 테이블 갱신
    }

    /**
     * 재료 콤보박스에 모든 재료 로드
     */
    public void loadIngredientsIntoComboBox() {
        ingredientComboBox.removeAllItems(); // 기존 항목 제거
        List<Ingredient> ingredients = ingredientDAO2.getAllIngredients(); // 모든 재료 불러오기
        for (Ingredient ingredient : ingredients) {
            ingredientComboBox.addItem(ingredient.getName()); // 재료 이름만 추가
        }
    }

    /**
     * 상품 등록
     * 카테고리, 이름, 가격, 재료를 기반으로 상품 등록
     */
    private void registerProduct() {
        String category = (String) categoryComboBox.getSelectedItem();
        String name = nameField.getText();
        String priceText = priceField.getText();

        if (category == null || name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            int categoryId = categoryComboBox.getSelectedIndex() + 1;
            Product product = new Product(0, categoryId, category, name, price);

            // 상품 추가
            if (productDAO.addProduct(product)) {
                for (int i = 0; i < ingredientTableModel.getRowCount(); i++) {
                    String ingredientName = (String) ingredientTableModel.getValueAt(i, 0);
                    BigDecimal quantity = new BigDecimal(ingredientTableModel.getValueAt(i, 1).toString());
                    int ingredientId = ingredientComboBox.getSelectedIndex() + 1;

                    ProductIngredient productIngredient = new ProductIngredient(
                            product.getProductId(), ingredientId, ingredientName, quantity
                    );
                    ingredientDAO.addProductIngredient(productIngredient);
                }
                JOptionPane.showMessageDialog(this, "상품이 등록되었습니다!");
                refreshProductTable();
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "상품 등록에 실패했습니다!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로 입력하세요!");
        }
    }


    /**
     * 재료 삭제
     */
    private void deleteSelectedIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 재료를 선택하세요!");
            return;
        }
        ingredientTableModel.removeRow(selectedRow);
    }

    /**
     * 입력 필드 초기화
     */
    private void clearInputFields() {
        nameField.setText("");
        priceField.setText("");
        ingredientTableModel.setRowCount(0);
    }

    /**
     * 상품 수정
     */
    private void editSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 상품을 선택하세요!");
            return;
        }

        // 기존 데이터 가져오기
        int productId = (int) productTableModel.getValueAt(selectedRow, 0);
        String category = (String) productTableModel.getValueAt(selectedRow, 1);
        String name = (String) productTableModel.getValueAt(selectedRow, 2);
        BigDecimal price = (BigDecimal) productTableModel.getValueAt(selectedRow, 3);

        // 상품 카테고리 콤보박스 동적으로 로드
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JComboBox<String> categoryBox = new JComboBox<>();
        loadProductCategories(categoryBox); // 상품 카테고리 로드
        categoryBox.setSelectedItem(category); // 기존 상품의 카테고리 설정
        JTextField nameField = new JTextField(name);
        JTextField priceField = new JTextField(price.toString());

        panel.add(new JLabel("카테고리:"));
        panel.add(categoryBox);
        panel.add(new JLabel("상품 이름:"));
        panel.add(nameField);
        panel.add(new JLabel("상품 가격:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "상품 수정", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal updatedPrice = new BigDecimal(priceField.getText());

                // 상품 수정
                Product updatedProduct = new Product(productId, categoryBox.getSelectedIndex() + 1, categoryBox.getSelectedItem().toString(), nameField.getText(), updatedPrice);
                if (productDAO.updateProduct(updatedProduct)) {
                    JOptionPane.showMessageDialog(this, "상품이 수정되었습니다!");
                    refreshProductTable(); // 테이블 갱신
                } else {
                    JOptionPane.showMessageDialog(this, "수정에 실패했습니다.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "가격은 숫자로 입력하세요!");
            }
        }
    }

    /**
     * 상품 삭제
     */
    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 상품을 선택하세요!");
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 상품을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deactivateProduct(productId)) {
                JOptionPane.showMessageDialog(this, "상품이 삭제(비활성화)되었습니다!");
                refreshProductTable();
            } else {
                JOptionPane.showMessageDialog(this, "상품 삭제(비활성화)에 실패했습니다.");
            }
        }
    }


    /**
     * 상품 카테고리 로드
     */
    private void loadProductCategories() {
        List<ProductCategory> categories = categoryDAO.getAllCategories();
        for (ProductCategory category : categories) {
            categoryComboBox.addItem(category.getCategoryName());
        }
    }

    private void loadProductCategories(JComboBox<String> targetComboBox) {
        targetComboBox.removeAllItems(); // 기존 항목 초기화
        List<ProductCategory> categories = categoryDAO.getAllCategories();
        for (ProductCategory category : categories) {
            targetComboBox.addItem(category.getCategoryName());
        }
    }


   // 데이터베이스에서 재료 목록을 가져오는 메서드
    private List<String> getIngredientsFromDatabase() throws SQLException {
        List<String> ingredients = new ArrayList<>();
        IngredientDAO ingredientDAO = new IngredientDAO();

        // 실제 DB에서 재료 목록을 가져오기
        List<Ingredient> ingredientList = ingredientDAO.getAllIngredients(); // DB에서 재료 리스트 가져오기

        // 재료 이름만 콤보박스에 추가
        for (Ingredient ingredient : ingredientList) {
            ingredients.add(ingredient.getName());
        }

        return ingredients;
    }

    public void refreshTable() {
        ingredientTableModel.setRowCount(0); // 테이블 초기화

        // 이 메서드에서 ingredientDAO2.getAllIngredients() 호출 제거
        // 테이블은 사용자가 "추가" 버튼을 눌렀을 때만 업데이트
    }



    public void refreshProductTable() {
        productTableModel.setRowCount(0);
        List<Product> products = productDAO.getActiveProducts(); // 활성 상품만 조회
        for (Product product : products) {
            productTableModel.addRow(new Object[]{
                    product.getProductId(),
                    product.getCategoryName(),
                    product.getName(),
                    product.getPrice()
            });
        }
    }



}
