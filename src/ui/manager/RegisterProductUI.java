package ui.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterProductUI extends JPanel {

    private JComboBox<String> categoryComboBox;
    private JTextField nameField, priceField, ingredientQuantityField;
    private JComboBox<String> ingredientComboBox;
    private JTable productTable;
    private DefaultTableModel productTableModel, ingredientTableModel;

    public RegisterProductUI() {
        setLayout(new BorderLayout());

        // 상단: 상품 기본 정보 입력
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중단: 재료 추가 영역
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단: 상품 조회 및 수정/삭제
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // 임시 데이터 로드
        loadExampleData();
    }

    /**
     * 상단 패널 - 상품 기본 정보 입력
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        panel.add(new JLabel("카테고리:"));
        categoryComboBox = new JComboBox<>(new String[]{"버거", "사이드", "음료"}); // 예시 카테고리
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
        ingredientComboBox = new JComboBox<>(new String[]{"소고기", "양상추", "치즈", "소스"}); // 예시 재료
        inputPanel.add(ingredientComboBox);

        inputPanel.add(new JLabel("소요량:"));
        ingredientQuantityField = new JTextField(5);
        inputPanel.add(ingredientQuantityField);

        JButton addIngredientButton = new JButton("추가");
        addIngredientButton.addActionListener(e -> addIngredientToList());
        inputPanel.add(addIngredientButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // 하단: 재료 목록 테이블
        ingredientTableModel = new DefaultTableModel(new Object[]{"재료", "소요량"}, 0);
        JTable ingredientTable = new JTable(ingredientTableModel);
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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

        // 수정/삭제 버튼 패널 (가운데 배치)
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

    /**
     * 상품 등록
     */
    private void registerProduct() {
        String category = (String) categoryComboBox.getSelectedItem();
        String name = nameField.getText();
        String price = priceField.getText();

        // 입력 검증
        if (category == null || name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요!");
            return;
        }

        try {
            double parsedPrice = Double.parseDouble(price);

            // 상품 테이블에 추가
            productTableModel.addRow(new Object[]{productTableModel.getRowCount() + 1, category, name, parsedPrice});
            JOptionPane.showMessageDialog(this, "상품이 등록되었습니다!");

            // 입력 필드 초기화
            nameField.setText("");
            priceField.setText("");
            ingredientTableModel.setRowCount(0); // 재료 리스트 초기화
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "가격은 숫자로 입력하세요!");
        }
    }

    /**
     * 재료 추가
     */
    private void addIngredientToList() {
        String ingredient = (String) ingredientComboBox.getSelectedItem();
        String quantity = ingredientQuantityField.getText();

        if (ingredient == null || quantity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "재료와 소요량을 입력하세요!");
            return;
        }

        try {
            double parsedQuantity = Double.parseDouble(quantity);

            ingredientTableModel.addRow(new Object[]{ingredient, parsedQuantity});
            JOptionPane.showMessageDialog(this, "재료가 추가되었습니다!");

            ingredientQuantityField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "소요량은 숫자로 입력하세요!");
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
        String category = (String) productTableModel.getValueAt(selectedRow, 1);
        String name = (String) productTableModel.getValueAt(selectedRow, 2);
        double price = (double) productTableModel.getValueAt(selectedRow, 3);

        // 수정 창 표시
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"버거", "사이드", "음료"});
        categoryBox.setSelectedItem(category);
        JTextField nameField = new JTextField(name);
        JTextField priceField = new JTextField(String.valueOf(price));

        panel.add(new JLabel("카테고리:"));
        panel.add(categoryBox);
        panel.add(new JLabel("상품 이름:"));
        panel.add(nameField);
        panel.add(new JLabel("상품 가격:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "상품 수정", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double updatedPrice = Double.parseDouble(priceField.getText());

                productTableModel.setValueAt(categoryBox.getSelectedItem(), selectedRow, 1);
                productTableModel.setValueAt(nameField.getText(), selectedRow, 2);
                productTableModel.setValueAt(updatedPrice, selectedRow, 3);

                JOptionPane.showMessageDialog(this, "상품이 수정되었습니다!");
            } catch (NumberFormatException e) {
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

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 상품을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            productTableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "상품이 삭제되었습니다!");
        }
    }

    /**
     * 임시 데이터 로드
     */
    private void loadExampleData() {
        List<Object[]> exampleData = List.of(
                new Object[]{1, "버거", "불고기 버거", 5000},
                new Object[]{2, "음료", "콜라", 1500}
        );

        for (Object[] data : exampleData) {
            productTableModel.addRow(data);
        }
    }
}
