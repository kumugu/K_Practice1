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

    private JComboBox<String> categoryComboBox, ingredientComboBox;
    private JTextField nameField, priceField, ingredientQuantityField;
    private JTable productTable, ingredientTable;
    private DefaultTableModel productTableModel, ingredientTableModel;

    private final ProductDAO productDAO = new ProductDAO();
    private final ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
    private final ProductIngredientDAO ingredientDAO = new ProductIngredientDAO();
    private final IngredientDAO ingredientDAO2 = new IngredientDAO(); // 재료 추가를 위한 DAO
    private MenuDAO menuDAO;

    public RegisterProductUI() throws SQLException {
        setLayout(new BorderLayout());
        menuDAO = new MenuDAO(); // 필드로 menuDAO 초기화

        // 상단: 상품 기본 정보 입력
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중단: 재료 추가 영역
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단: 상품 조회 및 수정/삭제
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // 데이터 로드
        refreshProductTable();
        // 재료 콤보박스 로드
        loadIngredientsIntoComboBox();
        // 상품 카테고리 콤보박스 로드
        loadProductCategories();
    }

    /**
     * 상단 패널 - 상품 기본 정보 입력
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

    /**
     * 중단 패널 - 재료 추가 영역
     */
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

        panel.add(inputPanel, BorderLayout.NORTH);

        // 하단: 재료 목록 테이블
        ingredientTableModel = new DefaultTableModel(new Object[]{"재료", "소요량", "삭제"}, 0);
        ingredientTable = new JTable(ingredientTableModel);
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 삭제 버튼 추가
        JButton deleteIngredientButton = new JButton("재료 삭제");
        deleteIngredientButton.addActionListener(e -> deleteSelectedIngredient());
        inputPanel.add(deleteIngredientButton);

        return panel;
    }

    /**
     * 하단 패널 - 상품 조회 및 수정/삭제
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상품 테이블
        productTableModel = new DefaultTableModel(new Object[]{"ID", "카테고리", "상품명", "가격"}, 0);
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 수정/삭제 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton editButton = new JButton("수정");
        editButton.addActionListener(e -> editSelectedProduct());
        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

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
            int categoryId = categoryComboBox.getSelectedIndex() + 1; // 카테고리 ID 매핑
            Product product = new Product(0, categoryId, category, name, price);

            if (productDAO.addProduct(product)) { // Products 테이블에 추가
                for (int i = 0; i < ingredientTableModel.getRowCount(); i++) {
                    String ingredientName = (String) ingredientTableModel.getValueAt(i, 0);
                    BigDecimal quantity = new BigDecimal(ingredientTableModel.getValueAt(i, 1).toString());
                    int ingredientId = ingredientComboBox.getSelectedIndex() + 1; // 재료 ID 매핑

                    ProductIngredient productIngredient = new ProductIngredient(
                            product.getProductId(), ingredientId, ingredientName, quantity
                    );
                    ingredientDAO.addProductIngredient(productIngredient); // ProductIngredients 테이블에 추가
                }
                JOptionPane.showMessageDialog(this, "상품이 등록되었습니다!");
                refreshProductTable(); // 테이블 갱신
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "상품 등록에 실패했습니다!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로 입력하세요!");
        }
    }


    /**
     * 재료 추가
     */
    // 재료 추가
    private void addIngredientToList() {
        String ingredient = (String) ingredientComboBox.getSelectedItem();
        String quantityText = ingredientQuantityField.getText();

        if (ingredient == null || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "재료와 소요량을 입력하세요!");
            return;
        }

        try {
            BigDecimal quantity = new BigDecimal(quantityText);
            ingredientTableModel.addRow(new Object[]{ingredient, quantity});

            // 재료 추가 후 콤보박스 갱신
            loadIngredientsIntoComboBox(); // 재료 추가 후 콤보박스를 갱신

            ingredientQuantityField.setText(""); // 입력 필드 초기화
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "소요량은 숫자로 입력하세요!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 재료를 콤보박스에 로드
    public void loadIngredientsIntoComboBox() throws SQLException {
        ingredientComboBox.removeAllItems(); // 기존 항목 제거
        List<String> ingredients = getIngredientsFromDatabase(); // DB에서 재료 가져오기
        for (String ingredient : ingredients) {
            ingredientComboBox.addItem(ingredient); // 새로운 항목 추가
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
        ingredientTableModel.setRowCount(0); // 재료 목록 초기화
    }

    /**
     * 상품 테이블 데이터 갱신
     */
    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product product : products) {
            productTableModel.addRow(new Object[] {
                    product.getProductId(),
                    product.getCategoryName(),
                    product.getName(),
                    product.getPrice()
            });
        }
    }

    /**
     * 상품 카테고리 로드
     */
    // 기존 메서드 그대로 유지
    private void loadProductCategories() {
        List<ProductCategory> categories = categoryDAO.getAllCategories();
        for (ProductCategory category : categories) {
            categoryComboBox.addItem(category.getCategoryName());
        }
    }

    // 추가할 오버로드된 메서드
    private void loadProductCategories(JComboBox<String> targetComboBox) {
        targetComboBox.removeAllItems(); // 기존 항목 초기화
        List<ProductCategory> categories = categoryDAO.getAllCategories();
        for (ProductCategory category : categories) {
            targetComboBox.addItem(category.getCategoryName());
        }
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
            // 메뉴에서 관련된 항목 삭제 (수동 삭제)
            if (!menuDAO.deleteMenuByProductId(productId)) {
                JOptionPane.showMessageDialog(this, "메뉴 항목 삭제에 실패했습니다.");
                return;
            }

            // 상품-재료 매핑 삭제
            ingredientDAO.deleteProductIngredients(productId);

            // 상품 삭제
            if (productDAO.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(this, "상품이 삭제되었습니다!");
                refreshProductTable(); // 테이블 갱신
            } else {
                JOptionPane.showMessageDialog(this, "상품 삭제에 실패했습니다.");
            }
        }
    }


}
