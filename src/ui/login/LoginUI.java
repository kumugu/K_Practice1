package ui.login;

import ui.MainUI;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JPanel {

    public LoginUI(JFrame frame) {
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

        // 이벤트 처리
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if ("".equals(username) && "".equals(password)) {
                JOptionPane.showMessageDialog(this, "로그인 성공!");
                MainUI.showPanel(MainUI.LOBBY_PANEL);
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 잘못되었습니다!");
            }
        });

        registerButton.addActionListener(e -> MainUI.showPanel(MainUI.REGISTER_PANEL));
    }
}
