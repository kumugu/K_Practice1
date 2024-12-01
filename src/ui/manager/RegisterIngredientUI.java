package ui.manager;

import service.IngredientCategoryDAO;
import service.IngredientDAO;
import model.Ingredient;
import model.IngredientCategory;
import service.StockDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class RegisterIngredientUI extends JPanel {

    private JComboBox<String> categoryComboBox;
    private JTextField nameField, unitPriceField, unitField;
    private JTable ingredientTable;
    private DefaultTableModel tableModel;

    private final IngredientCategoryDAO categoryDAO = new IngredientCategoryDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private RegisterProductUI registerProductUI; // RegisterProductUI 인스턴스 추가

    public RegisterIngredientUI(RegisterProductUI registerProductUI) throws SQLException {
        this.registerProductUI = registerProductUI;
        setLayout(new BorderLayout());

        // UI 초기화
        setupInputPanel();
        setupTable();
        setupButtonPanel();

        // 데이터 로드
        loadCategoriesIntoComboBox();
        refreshTable();
    }

    // 상단 입력 패널 구성
    private void setupInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        inputPanel.add(new JLabel("카테고리:"));
        categoryComboBox = new JComboBox<>();
        inputPanel.add(categoryComboBox);

        inputPanel.add(new JLabel("재료 이름:"));
        nameField = new JTextField(10);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("단가:"));
        unitPriceField = new JTextField(10);
        inputPanel.add(unitPriceField);

        inputPanel.add(new JLabel("단위:"));
        unitField = new JTextField(10);
        inputPanel.add(unitField);

        JButton addButton = new JButton("등록");
        addButton.addActionListener(e -> {
            addIngredient();
        });
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);
    }

    // 중앙 테이블 구성
    private void setupTable() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "카테고리", "재료 이름", "단가", "단위"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ingredientTable = new JTable(tableModel);
        add(new JScrollPane(ingredientTable), BorderLayout.CENTER);
    }

    // 하단 버튼 패널 구성
    private void setupButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("수정");
        editButton.addActionListener(e -> editSelectedIngredient());
        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> deleteSelectedIngredient());
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 카테고리 콤보박스에 데이터 로드
    private void loadCategoriesIntoComboBox() {
        List<String> categoryNames = categoryDAO.getCategoryNames();
        categoryComboBox.removeAllItems(); // 기존 항목 초기화
        for (String categoryName : categoryNames) {
            categoryComboBox.addItem(categoryName);
        }
    }

    // 테이블 데이터 갱신
    private void refreshTable() {
        tableModel.setRowCount(0); // 기존 데이터 초기화
        List<Ingredient> ingredients = ingredientDAO.getAllIngredients();
        for (Ingredient ingredient : ingredients) {
            tableModel.addRow(new Object[]{
                    ingredient.getIngredientId(),
                    ingredient.getCategoryName(),
                    ingredient.getName(),
                    ingredient.getUnitPrice(),
                    ingredient.getUnit()
            });
        }
    }

    // 재료 등록
    private void addIngredient() {
        String category = (String) categoryComboBox.getSelectedItem();
        String name = nameField.getText();
        String unitPrice = unitPriceField.getText();
        String unit = unitField.getText();

        if (category == null || name.isEmpty() || unitPrice.isEmpty() || unit.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(unitPrice);

            // 카테고리 ID 매핑
            int categoryId = categoryDAO.getAllCategories()
                    .stream()
                    .filter(c -> c.getCategoryName().equals(category))
                    .map(IngredientCategory::getCategoryId)
                    .findFirst()
                    .orElse(-1);

            if (categoryId == -1) {
                JOptionPane.showMessageDialog(this, "선택한 카테고리가 존재하지 않습니다!");
                return;
            }

            // 재료 추가
            Ingredient ingredient = new Ingredient(0, name, price, unit, categoryId, category);
            int ingredientId = ingredientDAO.addIngredient(ingredient); // 수정된 addIngredient 메서드에서 ID 반환

            if (ingredientId != -1) {
                // 재료 등록 성공 후, 재고 추가
                StockDAO stockDAO = new StockDAO();
                stockDAO.addStockForNewIngredient(ingredientId);

                JOptionPane.showMessageDialog(this, "재료가 등록되었습니다!");

                // 테이블 및 콤보박스 갱신
                refreshTable();
                registerProductUI.loadIngredientsIntoComboBox();

                // 입력 필드 초기화
                nameField.setText("");
                unitPriceField.setText("");
                unitField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "재료 등록에 실패했습니다!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "단가는 숫자로 입력하세요!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 재료 수정
    private void editSelectedIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 재료를 선택하세요!");
            return;
        }

        int ingredientId = (int) tableModel.getValueAt(selectedRow, 0);
        String categoryName = (String) tableModel.getValueAt(selectedRow, 1);
        String name = (String) tableModel.getValueAt(selectedRow, 2);
        BigDecimal unitPrice = (BigDecimal) tableModel.getValueAt(selectedRow, 3);
        String unit = (String) tableModel.getValueAt(selectedRow, 4);

        int categoryId = categoryDAO.getAllCategories()
                .stream()
                .filter(c -> c.getCategoryName().equals(categoryName))
                .map(IngredientCategory::getCategoryId)
                .findFirst()
                .orElse(-1);

        if (categoryId == -1) {
            JOptionPane.showMessageDialog(this, "선택한 카테고리가 존재하지 않습니다!");
            return;
        }

        Ingredient updatedIngredient = new Ingredient(ingredientId, name, unitPrice, unit, categoryId, categoryName);

        if (ingredientDAO.updateIngredient(updatedIngredient)) {
            JOptionPane.showMessageDialog(this, "재료가 수정되었습니다!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "수정에 실패했습니다!");
        }
    }

    // 재료 삭제
    private void deleteSelectedIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 재료를 선택하세요!");
            return;
        }

        int ingredientId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 재료를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (ingredientDAO.deleteIngredient(ingredientId)) {
                JOptionPane.showMessageDialog(this, "재료가 삭제되었습니다!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "삭제에 실패했습니다.");
            }
        }
    }




    // 카테고리 콤보박스 생성
    private JComboBox<String> createCategoryComboBox() {
        JComboBox<String> categoryBox = new JComboBox<>();
        List<String> categories = categoryDAO.getCategoryNames();
        for (String category : categories) {
            categoryBox.addItem(category);
        }
        return categoryBox;
    }



}
