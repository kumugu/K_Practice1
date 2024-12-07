package ui.manager;

import model.Report;
import service.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProfitLossUI extends JPanel {
    private final ReportDAO reportDAO;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private ReportDAO reportDao;



    public ProfitLossUI() {
        this.reportDAO = new ReportDAO(); // ReportDAO 초기화
        setupUI();
        
        setLayout(new BorderLayout());
        reportDao = new ReportDAO();

        // 상단 패널 구성
        JPanel topPanel = new JPanel();
        yearComboBox = new JComboBox<>(new String[]{"2023", "2024", "2025"});  // 연도 ComboBox
        monthComboBox = new JComboBox<>(new String[]{"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"});  // 월 ComboBox

        JButton fetchButton = new JButton("조회");
        fetchButton.addActionListener(e -> fetchProfitLossData(true)); // 선택 조회 버튼
        JButton fetchAllButton = new JButton("전체 조회");
        fetchAllButton.addActionListener(e -> fetchProfitLossData(false)); // 전체 조회 버튼

        // 상단 패널에 컴포넌트 추가
        topPanel.add(new JLabel("연도:"));
        topPanel.add(yearComboBox);
        topPanel.add(new JLabel("월:"));
        topPanel.add(monthComboBox);
        topPanel.add(fetchButton);
        topPanel.add(fetchAllButton);

        add(topPanel, BorderLayout.NORTH);  // 화면 상단에 배치

        // 테이블 구성
        tableModel = new DefaultTableModel();  // 기본 테이블 모델
        reportTable = new JTable(tableModel);  // 테이블
        add(new JScrollPane(reportTable), BorderLayout.CENTER);  // 테이블을 스크롤 패널에 넣어서 중앙에 배치
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Date", "Details"}, 0);
        JTable table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        add(refreshButton, BorderLayout.SOUTH);
    }

    // 손익 계산서 데이터를 가져오는 메서드
    private void fetchProfitLossData(boolean isSelected) {
        String year = (String) yearComboBox.getSelectedItem();  // 선택된 연도
        String month = (String) monthComboBox.getSelectedItem();  // 선택된 월

        String dateCondition = reportDao.getDateCondition(isSelected, year, month);  // 조건에 맞는 날짜 조건
        clearTable();  // 테이블 초기화

        try {
            // 손익계산서 갱신: 선택된 날짜 조건에 맞게 갱신
            reportDao.updateProfitLossReportAfterTransaction();

            // 데이터 조회
            List<String[]> reportData = reportDao.getProfitLossReportWithCondition(dateCondition);  // 데이터 조회

            // 테이블 컬럼 설정
            setTableColumns("보고서 날짜", "총 매출", "총 제품 원가", "총 재료 비용", "총 급여 비용", "순이익");

            // 조회된 데이터 테이블에 추가
            for (String[] row : reportData) {
                tableModel.addRow(new Object[]{
                        row[0],  // 보고서 날짜
                        row[1] + "원",  // 총 매출
                        row[2] + "원",  // 총 제품 원가
                        row[3] + "원",  // 총 재료 비용
                        row[4] + "원",  // 총 급여 비용
                        row[5] + "원"   // 순이익
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "데이터를 가져오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);  // 에러 처리
        }
    }

    // 테이블 초기화
    private void clearTable() {
        tableModel.setRowCount(0);  // 테이블의 모든 행을 지움
    }

    // 테이블 컬럼 설정
    private void setTableColumns(String... columns) {
        tableModel.setColumnIdentifiers(columns);  // 테이블 컬럼 이름 설정
    }

    public void refreshData() {
        tableModel.setRowCount(0); // 테이블 초기화
        List<Report> reports = reportDAO.getAllReports(); // DAO에서 데이터 가져오기
        for (Report report : reports) {
            tableModel.addRow(new Object[]{
                    report.getId(),
                    report.getTitle(),
                    report.getDate(),
                    report.getDetails()
            });
        }
    }

}
