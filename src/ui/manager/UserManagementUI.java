package ui.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserManagementUI extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementUI() {
        setLayout(new BorderLayout());

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
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 이벤트 연결
        searchButton.addActionListener(e -> searchUser(searchField.getText()));
        showAllButton.addActionListener(e -> showAllUsers());
        editButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        // 초기 데이터 로드
        showAllUsers();
    }

    /**
     * 직원 데이터 전체 조회
     */
    private void showAllUsers() {
        // 테이블 초기화
        tableModel.setRowCount(0);

        // 예시 데이터 (실제 DB 연결 필요)
        List<Object[]> users = List.of(
                new Object[]{1, "홍길동", "010-1234-5678", "팀원", "2023-01-01"},
                new Object[]{2, "김철수", "010-2345-6789", "팀장", "2022-05-15"}
        );

        for (Object[] user : users) {
            tableModel.addRow(user);
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

        // 예시 검색 결과 (실제 DB 연결 필요)
        List<Object> searchResults = List.of(
                new Object[]{1, "홍길동", "010-1234-5678", "팀원", "2023-01-01"}
        );


        for (Object user : searchResults) {
            tableModel.addRow((Object[]) user);
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
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"팀원", "팀장", "매니저"});
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
            String updatedRole = (String) roleComboBox.getSelectedItem();

            // 실제 DB 업데이트 필요
            JOptionPane.showMessageDialog(this, "직원 정보가 수정되었습니다!");

            // 테이블 업데이트
            tableModel.setValueAt(updatedName, selectedRow, 1);
            tableModel.setValueAt(updatedContact, selectedRow, 2);
            tableModel.setValueAt(updatedRole, selectedRow, 3);
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
            // 실제 DB 삭제 필요
            JOptionPane.showMessageDialog(this, "직원이 삭제되었습니다!");

            // 테이블에서 삭제
            tableModel.removeRow(selectedRow);
        }
    }
}
