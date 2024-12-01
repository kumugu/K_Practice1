package ui.login;

import ui.MainUI;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterUI extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JTextField contactField;

    public RegisterUI(JFrame frame) {
        setLayout(new BorderLayout());

        // 배경색 설정
        setBackground(new Color(153, 50, 205));

        // 상단 빈 공간
        JPanel topEmptyPanel = new JPanel();
        topEmptyPanel.setBackground(new Color(49, 203, 134));
        topEmptyPanel.setPreferredSize(new Dimension(100, 100)); // 적절한 크기 설정
        add(topEmptyPanel, BorderLayout.NORTH);

        // 중앙 패널 (회원가입 폼)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.ORANGE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 아이디 입력
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("아이디:"), gbc);

        usernameField = new JTextField(15); // 필드 초기화
        gbc.gridx = 1;
        centerPanel.add(usernameField, gbc);

        // 아이디 중복 확인 버튼
        JButton checkUsernameButton = new JButton("아이디 중복 확인");
        checkUsernameButton.addActionListener(e -> {
            String username = usernameField.getText().trim();  // 입력값의 양쪽 공백 제거

            if (username.isEmpty()) {  // 아이디가 공백인 경우
                JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
            } else if (isUsernameExists(username)) {  // 중복된 아이디인 경우
                JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
            } else {  // 사용 가능한 아이디인 경우
                JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 5, 10, 0); // 여백 조정
        gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
        centerPanel.add(checkUsernameButton, gbc);

        // 비밀번호 입력
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(new JLabel("비밀번호:"), gbc);

        passwordField = new JPasswordField(15); // 필드 초기화
        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5); // 기본 여백으로 되돌림
        centerPanel.add(passwordField, gbc);

        // 비밀번호 확인 입력
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(new JLabel("비밀번호 확인:"), gbc);

        confirmPasswordField = new JPasswordField(15); // 필드 초기화
        gbc.gridx = 1;
        centerPanel.add(confirmPasswordField, gbc);

        // 이름 입력
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(new JLabel("이름:"), gbc);

        nameField = new JTextField(15); // 필드 초기화
        gbc.gridx = 1;
        centerPanel.add(nameField, gbc);

        // 연락처 입력
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(new JLabel("연락처:"), gbc);

        contactField = new JTextField(15); // 필드 초기화
        gbc.gridx = 1;
        centerPanel.add(contactField, gbc);

        // 뒤로가기 버튼
        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> MainUI.showPanel(MainUI.LOGIN_PANEL));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 5, 10, 10); // 여백 조정
        gbc.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
        centerPanel.add(backButton, gbc);

        // 회원가입 버튼
        JButton registerButton = new JButton("회원가입");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String name = nameField.getText();
            String contact = contactField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            } else if (isUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
            } else {
                // 비밀번호 해싱 후 DB에 저장
                String hashedPassword = hashPassword(password);

                // DB에 사용자 등록 로직을 추가해야 합니다.
                if (registerUser(username, hashedPassword, name, contact)) {
                    JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!");
                    MainUI.showPanel(MainUI.LOGIN_PANEL); // 로그인 화면으로 전환

                    // 텍스트 필드 초기화
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "회원가입에 실패했습니다.");
                }
            }
        });

        gbc.gridx = 1;
        gbc.insets = new Insets(10, 10, 10, 5); // 여백 조정
        gbc.anchor = GridBagConstraints.EAST; // 오른쪽 정렬
        centerPanel.add(registerButton, gbc);

        // 중앙 패널 추가
        add(centerPanel, BorderLayout.CENTER);

        // 하단 빈 공간
        JPanel bottomEmptyPanel = new JPanel();
        bottomEmptyPanel.setBackground(new Color(153, 50, 205));
        bottomEmptyPanel.setPreferredSize(new Dimension(100, 100)); // 적절한 크기 설정
        add(bottomEmptyPanel, BorderLayout.SOUTH);
    }

    // 텍스트 필드 초기화 메소드
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        nameField.setText("");
        contactField.setText("");
    }

    // 아이디 중복 여부 확인 메소드
    private boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM employees WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 비밀번호 해싱 메소드 (간단한 예시로 SHA-256 사용)
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 사용자 등록 메소드 (DB에 사용자 추가)
    private boolean registerUser(String username, String password, String name, String contact) {
        String query = "INSERT INTO employees (username, password_hash, name, contact) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setString(4, contact);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
