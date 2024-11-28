package ui.inventory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryUI extends JPanel {

    public InventoryUI() {
        setLayout(new BorderLayout());

        // 상단: 재료 주문 기능
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중단: 검색 및 재고 조회
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단: 수정 및 삭제
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 상단 패널 - 재료 주문 기능
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JComboBox<String> ingredientComboBox = new JComboBox<>(new String[]{"소고기", "양상추", "치즈", "소스"});
        JTextField quantityField = new JTextField(10);
        JButton orderButton = new JButton("주문");

        panel.add(new JLabel("재료:"));
        panel.add(ingredientComboBox);
        panel.add(new JLabel("수량:"));
        panel.add(quantityField);
        panel.add(orderButton);

        // 이벤트 처리
        orderButton.addActionListener(e -> {
            String ingredient = (String) ingredientComboBox.getSelectedItem();
            String quantity = quantityField.getText();

            if (ingredient == null || quantity.isEmpty()) {
                JOptionPane.showMessageDialog(this, "재료와 수량을 입력하세요!");
                return;
            }

            try {
                double parsedQuantity = Double.parseDouble(quantity);
                JOptionPane.showMessageDialog(this,
                        "재료 주문 완료:\n재료: " + ingredient + "\n수량: " + parsedQuantity);
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "수량은 숫자로 입력하세요!");
            }
        });

        return panel;
    }

    /**
     * 중단 패널 - 검색 및 재고 조회
     */
    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 검색 영역
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        JButton viewAllButton = new JButton("전체 조회");

        searchPanel.add(new JLabel("재료명:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 재고 조회 테이블
        DefaultTableModel stockTableModel = new DefaultTableModel(
                new Object[]{"재료명", "카테고리", "현재 재고", "단위"}, 0
        );
        JTable stockTable = new JTable(stockTableModel);
        JScrollPane scrollPane = new JScrollPane(stockTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        // 임시 데이터 추가
        loadExampleData(stockTableModel);

        return panel;
    }

    /**
     * 하단 패널 - 수정 및 삭제 버튼
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        panel.add(editButton);
        panel.add(deleteButton);

        // 이벤트 처리
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "수정 버튼 클릭됨"));
        deleteButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "삭제 버튼 클릭됨"));

        return panel;
    }

    /**
     * 임시 데이터 로드
     */
    private void loadExampleData(DefaultTableModel stockTableModel) {
        stockTableModel.addRow(new Object[]{"소고기", "육류", 50.0, "kg"});
        stockTableModel.addRow(new Object[]{"양상추", "야채", 20.0, "개"});
        stockTableModel.addRow(new Object[]{"치즈", "유제품", 15.0, "장"});
        stockTableModel.addRow(new Object[]{"소스", "기타", 30.0, "병"});
    }
}
