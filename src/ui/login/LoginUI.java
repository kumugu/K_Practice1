package ui.login;

import ui.LobbyUI;
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

public class LoginUI extends JPanel {

    private MainUI mainUI; // MainUI 참조

    public LoginUI(MainUI mainUI) {
        this.mainUI = mainUI;
        setLayout(new BorderLayout());

        // 배경색 설정
        setBackground(new Color(25, 55, 153));

        // 카드 패널
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        cardPanel.setBackground(Color.ORANGE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 타이틀
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("로그인", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Google Sans", Font.BOLD, 24));
        cardPanel.add(titleLabel, gbc);

        // 아이디 입력
        gbc.gridwidth = 1;
        gbc.gridy++;
        cardPanel.add(new JLabel("아이디:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        cardPanel.add(usernameField, gbc);

        // 비밀번호 입력
        gbc.gridx = 0;
        gbc.gridy++;
        cardPanel.add(new JLabel("비밀번호:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        cardPanel.add(passwordField, gbc);

        // 버튼
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.ORANGE); // 배경색 설정
        JButton loginButton = new JButton("로그인");
        JButton registerButton = new JButton("회원가입");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        cardPanel.add(buttonPanel, gbc);

        // 전체 중앙 정렬
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(153, 50, 205));
        container.add(cardPanel);
        add(container, BorderLayout.CENTER);

        // 로그인 버튼 이벤트 처리
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // 아이디나 비밀번호가 비어있는지 확인
            if ("".equals(username) || "".equals(password)) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요!");
            } else if (isValidLogin(username, password)) { // 로그인 정보가 유효한지 확인
                JOptionPane.showMessageDialog(this, "로그인 성공!");
                mainUI.loginSuccess(); // 로그인 성공 시 호출
                MainUI.showPanel(MainUI.LOBBY_PANEL);  // 로그인 성공 후 LobbyUI로 전환
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 잘못되었습니다.");
            }
        });

        // 회원가입 버튼 이벤트 처리
        registerButton.addActionListener(e -> {
            MainUI.showPanel(MainUI.REGISTER_PANEL);  // 회원가입 화면으로 전환
        });
    }

    // 로그인 검증 메소드
    private boolean isValidLogin(String username, String password) {
        String query = "SELECT password_hash FROM Employees WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password_hash");
                    // 사용자가 입력한 비밀번호를 해시하여 저장된 해시 값과 비교
                    String inputPasswordHash = hashPassword(password);
                    return inputPasswordHash.equals(storedPasswordHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
}
