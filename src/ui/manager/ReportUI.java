package ui.manager;

import service.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportUI extends JPanel {
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> dateRangeComboBox;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private ReportDAO reportDao;

    public ReportUI() {
        setLayout(new BorderLayout());
        reportDao = new ReportDAO();

        // 상단 패널: 보고서 선택 및 입력 필드
        JPanel filterPanel = new JPanel(new GridLayout(2, 1));

        // 첫 번째 줄: 콤보 박스 및 기간 필드
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("보고서 종류:"));
        reportTypeComboBox = new JComboBox<>(new String[]{
                "판매 내역 보고서", "주문 내역 보고서", "급여 내역 보고서"
        });
        inputPanel.add(reportTypeComboBox);

        inputPanel.add(new JLabel("검색 기간:"));
        dateRangeComboBox = new JComboBox<>(new String[]{"하루", "일주일", "한 달", "일 년", "전체"});
        inputPanel.add(dateRangeComboBox);

        // 조회 버튼을 같은 줄에 배치
        JButton searchButton = new JButton("조회");
        searchButton.addActionListener(e -> fetchReportData());
        inputPanel.add(searchButton);

        filterPanel.add(inputPanel);

        add(filterPanel, BorderLayout.NORTH);

        // 중앙 패널: 결과 테이블
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);
    }

    private void fetchReportData() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String dateCondition = getDateCondition((String) dateRangeComboBox.getSelectedItem(), reportType);

        clearTable();

        try {
            List<String[]> reportData;
            switch (reportType) {
                case "판매 내역 보고서":
                    setTableColumns("상품명", "판매 수량", "총 금액", "판매 날짜");
                    reportData = reportDao.getSalesReportWithCondition(dateCondition);
                    for (String[] row : reportData) {
                        tableModel.addRow(new Object[]{row[0], row[1], row[2] + "원", row[3]});
                    }
                    break;
                case "주문 내역 보고서":
                    setTableColumns("재료명", "주문 수량", "총 금액", "주문 날짜", "거래처");
                    reportData = reportDao.getOrdersReportWithCondition(dateCondition);
                    for (String[] row : reportData) {
                        tableModel.addRow(new Object[]{row[0], row[1], row[2] + "원", row[3], row[4]});
                    }
                    break;
                case "급여 내역 보고서":
                    setTableColumns("직원명", "급여 금액", "지급 날짜");
                    reportData = reportDao.getSalariesReportWithCondition(dateCondition);
                    for (String[] row : reportData) {
                        tableModel.addRow(new Object[]{row[0], row[1] + "원", row[2]});
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "알 수 없는 보고서 유형입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "데이터를 가져오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getDateCondition(String dateRange, String reportType) {
        String dateColumn = "";
        switch (reportType) {
            case "판매 내역 보고서":
                dateColumn = "sale_date";
                break;
            case "주문 내역 보고서":
                dateColumn = "order_date";
                break;
            case "급여 내역 보고서":
                dateColumn = "payment_date";
                break;
        }

        switch (dateRange) {
            case "하루":
                return "WHERE TRUNC(" + dateColumn + ") = TRUNC(SYSDATE)";
            case "일주일":
                return "WHERE TRUNC(" + dateColumn + ") >= TRUNC(SYSDATE - 7)";
            case "한 달":
                return "WHERE TRUNC(" + dateColumn + ") >= ADD_MONTHS(TRUNC(SYSDATE), -1)";
            case "일 년":
                return "WHERE TRUNC(" + dateColumn + ") >= ADD_MONTHS(TRUNC(SYSDATE), -12)";
            default:
                return "";  // 전체 조회
        }
    }

    private void setTableColumns(String... columns) {
        tableModel.setColumnIdentifiers(columns);
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }
}
