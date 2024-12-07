package ui.manager;

import ui.EventManager;
import ui.EventTypes;
import service.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeesManagementUI extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private EmployeeDAO employeeDAO;
    private JComboBox<String> roleComboBox; // 역할 콤보박스

    public EmployeesManagementUI() {
        setLayout(new BorderLayout());
        employeeDAO = new EmployeeDAO();

        // 상단 검색 패널
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("직원 이름:"));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");
        JButton showAllButton = new JButton("전체 조회");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        add(searchPanel, BorderLayout.NORTH);

        // 중앙 테이블
        tableModel = new DefaultTableModel(new Object[]{"ID", "이름", "연락처", "직급", "입사 일자"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 편집 불가
            }
        };
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");
        JButton paySalaryButton = new JButton("급여 지급");
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(paySalaryButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 이벤트 연결
        searchButton.addActionListener(e -> searchUser(searchField.getText()));
        showAllButton.addActionListener(e -> showAllUsers());
        editButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        paySalaryButton.addActionListener(e -> paySalary());

        EventManager.getInstance().subscribe(EventTypes.EMPLOYEE_UPDATED, this::refreshTable);

        // 초기 데이터 로드
        showAllUsers();
    }

    public void refreshTable() {
        // 테이블 데이터 갱신 로직
    }

    /**
     * 역할 콤보박스 초기화
     */
    private void initializeRoleComboBox() {
        roleComboBox = new JComboBox<>();
        List<String> roles = employeeDAO.getAllRoleNames();
        for (String role : roles) {
            roleComboBox.addItem(role);
        }
    }

    /**
     * 직원 데이터 전체 조회
     */
    private void showAllUsers() {
        // 테이블 초기화
        tableModel.setRowCount(0);

        // DB에서 직원 데이터 가져오기
        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Employee employee : employees) {
            String roleName = employeeDAO.getRoleNameById(employee.getRoleId());
            tableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getName(),
                    employee.getContact(),
                    roleName, // 역할 이름을 사용
                    employee.getHireDate().toString()
            });
        }

    }

    /**
     * 직원 검색
     */
    private void searchUser(String name) {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "직원 이름을 입력하세요!");
            return;
        }

        // 테이블 초기화
        tableModel.setRowCount(0);

        // DB에서 직원 데이터 검색
        List<Employee> employees = employeeDAO.searchEmployeesByName(name);

        for (Employee employee : employees) {
            String roleName = employeeDAO.getRoleNameById(employee.getRoleId());
            tableModel.addRow(new Object[]{
                    employee.getEmployeeId(),
                    employee.getName(),
                    employee.getContact(),
                    roleName, // 역할 이름을 사용
                    employee.getHireDate().toString()
            });
        }
    }

    /**
     * 직원 수정
     */
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 직원을 선택하세요!");
            return;
        }

        // 선택된 직원 정보 가져오기
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String contact = (String) tableModel.getValueAt(selectedRow, 2);
        String role = (String) tableModel.getValueAt(selectedRow, 3);

        // 수정 창 표시
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField(name);
        JTextField contactField = new JTextField(contact);

        // 역할 콤보박스 초기화 및 설정
        initializeRoleComboBox();
        roleComboBox.setSelectedItem(role);

        panel.add(new JLabel("이름:"));
        panel.add(nameField);
        panel.add(new JLabel("연락처:"));
        panel.add(contactField);
        panel.add(new JLabel("직급:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "직원 정보 수정", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // 수정된 정보 저장
            String updatedName = nameField.getText();
            String updatedContact = contactField.getText();
            int updatedRole = roleComboBox.getSelectedIndex() + 1; // 직급 ID로 변환

            // 실제 DB 업데이트
            Employee updatedEmployee = new Employee(userId, null, null, updatedName, updatedContact, null, updatedRole);
            if (employeeDAO.updateEmployee(updatedEmployee)) {
                JOptionPane.showMessageDialog(this, "직원 정보가 수정되었습니다!");


            // 테이블 업데이트
                tableModel.setValueAt(updatedName, selectedRow, 1);
                tableModel.setValueAt(updatedContact, selectedRow, 2);
                tableModel.setValueAt(roleComboBox.getSelectedItem().toString(), selectedRow, 3);
            } else {
                JOptionPane.showMessageDialog(this, "직원 정보 수정에 실패했습니다.");
            }
        }
    }

    /**
     * 직원 삭제
     */
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 직원을 선택하세요!");
            return;
        }

        // 삭제 확인
        int confirm = JOptionPane.showConfirmDialog(this, "선택한 직원을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);

            // 실제 DB 삭제
            if (employeeDAO.deleteEmployee(userId)) {
                JOptionPane.showMessageDialog(this, "직원이 삭제되었습니다!");

                // 테이블에서 삭제
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "직원 삭제에 실패했습니다.");
            }

        }
    }

    /**
     * 급여 지급
     */
    private void paySalary() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "급여를 지급할 직원을 선택하세요!");
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        String amountStr = JOptionPane.showInputDialog(this, "지급할 급여 금액을 입력하세요:");
        if (amountStr == null || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "급여 금액을 입력하세요!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            String paymentType = "월급"; // 예시로 월급으로 설정
            if (employeeDAO.paySalary(employeeId, amount, paymentType)) {
                JOptionPane.showMessageDialog(this, "급여가 지급되었습니다!");
            } else {
                JOptionPane.showMessageDialog(this, "급여 지급에 실패했습니다.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "유효한 금액을 입력하세요!");
        }
    }
}
