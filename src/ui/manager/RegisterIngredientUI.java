package ui.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterIngredientUI extends JPanel {

    private JComboBox<String> categoryComboBox;
    private JTextField nameField, unitPriceField, unitField;
    private JTable ingredientTable;
    private DefaultTableModel tableModel;

    public RegisterIngredientUI() {
        setLayout(new BorderLayout());

        // 상단 입력 패널: FlowLayout 사용
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        inputPanel.add(new JLabel("카테고리:"));
        categoryComboBox = new JComboBox<>(new String[]{"육류", "야채", "소스", "기타"}); // 예시 데이터
        inputPanel.add(categoryComboBox);

        inputPanel.add(new JLabel("재료 이름:"));
        nameField = new JTextField(10); // 필드 크기 고정
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("단가:"));
        unitPriceField = new JTextField(10);
        inputPanel.add(unitPriceField);

        inputPanel.add(new JLabel("단위:"));
        unitField = new JTextField(10);
        inputPanel.add(unitField);

        JButton addButton = new JButton("등록");
        addButton.addActionListener(e -> addIngredient());
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        // 중앙 테이블
        tableModel = new DefaultTableModel(new Object[]{"ID", "카테고리", "재료 이름", "단가", "단위"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 편집 불가
            }
        };
        ingredientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("수정");
        editButton.addActionListener(e -> editSelectedIngredient());
        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> deleteSelectedIngredient());
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드 (예시 데이터)
        loadExampleData();
    }

    /**
     * 예시 데이터 로드
     */
    private void loadExampleData() {
        List<Object[]> exampleData = List.of(
                new Object[]{1, "육류", "소고기", 2000, "g"},
                new Object[]{2, "야채", "양상추", 500, "개"}
        );
        for (Object[] data : exampleData) {
            tableModel.addRow(data);
        }
    }

    /**
     * 재료 등록
     */
    private void addIngredient() {
        String category = (String) categoryComboBox.getSelectedItem();
        String name = nameField.getText();
        String unitPrice = unitPriceField.getText();
        String unit = unitField.getText();

        // 입력 검증
        if (category == null || name.isEmpty() || unitPrice.isEmpty() || unit.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요!");
            return;
        }

        try {
            double price = Double.parseDouble(unitPrice);

            // 테이블에 추가
            tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, category, name, price, unit});
            JOptionPane.showMessageDialog(this, "재료가 등록되었습니다!");

            // 입력 필드 초기화
            nameField.setText("");
            unitPriceField.setText("");
            unitField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "단가는 숫자로 입력하세요!");
        }
    }

    /**
     * 재료 수정
     */
    private void editSelectedIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 재료를 선택하세요!");
            return;
        }

        // 기존 데이터 가져오기
        String category = (String) tableModel.getValueAt(selectedRow, 1);
        String name = (String) tableModel.getValueAt(selectedRow, 2);
        double unitPrice = (double) tableModel.getValueAt(selectedRow, 3);
        String unit = (String) tableModel.getValueAt(selectedRow, 4);

        // 수정 창 표시
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"육류", "야채", "소스", "기타"});
        categoryBox.setSelectedItem(category);
        JTextField nameField = new JTextField(name);
        JTextField priceField = new JTextField(String.valueOf(unitPrice));
        JTextField unitField = new JTextField(unit);

        panel.add(new JLabel("카테고리:"));
        panel.add(categoryBox);
        panel.add(new JLabel("재료 이름:"));
        panel.add(nameField);
        panel.add(new JLabel("단가:"));
        panel.add(priceField);
        panel.add(new JLabel("단위:"));
        panel.add(unitField);

        int result = JOptionPane.showConfirmDialog(this, panel, "재료 수정", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double price = Double.parseDouble(priceField.getText());

                // 수정 반영
                tableModel.setValueAt(categoryBox.getSelectedItem(), selectedRow, 1);
                tableModel.setValueAt(nameField.getText(), selectedRow, 2);
                tableModel.setValueAt(price, selectedRow, 3);
                tableModel.setValueAt(unitField.getText(), selectedRow, 4);
                JOptionPane.showMessageDialog(this, "재료가 수정되었습니다!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "단가는 숫자로 입력하세요!");
            }
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

        // 삭제 확인
        int confirm = JOptionPane.showConfirmDialog(this, "선택한 재료를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "재료가 삭제되었습니다!");
        }
    }
}
