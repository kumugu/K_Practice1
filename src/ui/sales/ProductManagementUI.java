package ui.sales;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductManagementUI extends JPanel {

    public ProductManagementUI() {
        setLayout(new BorderLayout());

        // 상단: 메뉴 등록
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 중단: 검색 및 상품 조회
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        // 하단: 수정/삭제 버튼
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 상단 패널 - 메뉴 등록
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"버거", "사이드", "음료"});
        JComboBox<String> productNameComboBox = new JComboBox<>(new String[]{"불고기 버거", "치즈 버거", "콜라"});
        JButton registerButton = new JButton("등록");

        panel.add(new JLabel("카테고리:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel("상품명:"));
        panel.add(productNameComboBox);
        panel.add(registerButton);

        // 이벤트 처리
        registerButton.addActionListener(e -> {
            String category = (String) categoryComboBox.getSelectedItem();
            String productName = (String) productNameComboBox.getSelectedItem();
            JOptionPane.showMessageDialog(this,
                    "메뉴가 등록되었습니다:\n카테고리: " + category + "\n상품명: " + productName);
        });

        return panel;
    }

    /**
     * 중단 패널 - 검색 및 상품 조회
     */
    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 검색 영역
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        JButton viewAllButton = new JButton("전체 조회");

        searchPanel.add(new JLabel("상품명:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(viewAllButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 상품 조회 테이블
        DefaultTableModel productTableModel = new DefaultTableModel(
                new Object[]{"카테고리", "상품명", "가격", "재료 내용"}, 0
        );
        JTable productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        // 임시 데이터 추가
        loadExampleData(productTableModel);

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
    private void loadExampleData(DefaultTableModel productTableModel) {
        productTableModel.addRow(new Object[]{"버거", "불고기 버거", 5000, "소고기: 100g, 양상추: 30g"});
        productTableModel.addRow(new Object[]{"사이드", "감자튀김", 2000, "감자: 150g, 소금: 5g"});
        productTableModel.addRow(new Object[]{"음료", "콜라", 1500, "설탕: 10g, 탄산수: 200ml"});
    }
}
