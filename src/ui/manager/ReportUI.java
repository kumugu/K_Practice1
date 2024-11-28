package ui.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportUI extends JPanel {
    private JComboBox<String> reportTypeComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ReportUI() {
        setLayout(new BorderLayout());

        // 상단 패널: 보고서 선택 및 입력 필드
        JPanel filterPanel = new JPanel(new GridLayout(2, 1));

        // 첫 번째 줄: 콤보 박스 및 기간 필드
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("보고서 종류:"));
        reportTypeComboBox = new JComboBox<>(new String[]{
                "판매 내역 보고서", "주문 내역 보고서", "급여 내역 보고서", "손익 계산 보고서"
        });
        inputPanel.add(reportTypeComboBox);

        inputPanel.add(new JLabel("시작일:"));
        startDateField = new JTextField(10);
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("종료일:"));
        endDateField = new JTextField(10);
        inputPanel.add(endDateField);

        filterPanel.add(inputPanel);

        // 두 번째 줄: 조회 버튼
        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("조회");
        searchButton.addActionListener(e -> fetchReportData());
        buttonPanel.add(searchButton);
        filterPanel.add(buttonPanel);

        add(filterPanel, BorderLayout.NORTH);

        // 중앙 패널: 결과 테이블
        tableModel = new DefaultTableModel(new Object[]{"항목", "내용"}, 0);
        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);
    }

    /**
     * 보고서 데이터를 가져와 테이블에 표시
     */
    private void fetchReportData() {
        // 콤보 박스에서 선택된 보고서 종류
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();

        // 테이블 초기화
        tableModel.setRowCount(0);

        // 예시 데이터 (실제 데이터베이스 연동 필요)
        switch (reportType) {
            case "판매 내역 보고서":
                tableModel.addRow(new Object[]{"판매 내역", "예시 데이터 1"});
                tableModel.addRow(new Object[]{"기간", startDate + " ~ " + endDate});
                break;
            case "주문 내역 보고서":
                tableModel.addRow(new Object[]{"주문 내역", "예시 데이터 2"});
                tableModel.addRow(new Object[]{"기간", startDate + " ~ " + endDate});
                break;
            case "급여 내역 보고서":
                tableModel.addRow(new Object[]{"급여 내역", "예시 데이터 3"});
                tableModel.addRow(new Object[]{"기간", startDate + " ~ " + endDate});
                break;
            case "손익 계산 보고서":
                tableModel.addRow(new Object[]{"손익 계산", "예시 데이터 4"});
                tableModel.addRow(new Object[]{"기간", startDate + " ~ " + endDate});
                break;
        }
    }
}
