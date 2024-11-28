package ui.login;

import ui.MainUI;

import javax.swing.*;
import java.awt.*;

public class RegisterUI extends JPanel {

    public RegisterUI(JFrame frame) {
        setLayout(new BorderLayout());

        // 배경색 설정
        setBackground(new Color(153, 50, 205));

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
        JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER);
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

        // 비밀번호 확인
        gbc.gridx = 0;
        gbc.gridy++;
        cardPanel.add(new JLabel("비밀번호 확인:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(15);
        cardPanel.add(confirmPasswordField, gbc);

        // 이름 입력
        gbc.gridx = 0;
        gbc.gridy++;
        cardPanel.add(new JLabel("이름:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        cardPanel.add(nameField, gbc);

        // 연락처 입력
        gbc.gridx = 0;
        gbc.gridy++;
        cardPanel.add(new JLabel("연락처:"), gbc);

        gbc.gridx = 1;
        JTextField contactField = new JTextField(15);
        cardPanel.add(contactField, gbc);

        // 버튼
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton registerButton = new JButton("회원가입");
        JButton backButton = new JButton("뒤로가기");
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        cardPanel.add(buttonPanel, gbc);

        // 전체 중앙 정렬
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(245, 245, 245));
        container.add(cardPanel);
        add(container, BorderLayout.CENTER);

        // 이벤트 처리
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다!");
            } else if ("admin".equals(username)) { // 임시 중복 확인
                JOptionPane.showMessageDialog(this, "아이디가 중복되었습니다!");
            } else {
                JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!");
                MainUI.showPanel(MainUI.LOGIN_PANEL);
            }
        });

        backButton.addActionListener(e -> MainUI.showPanel(MainUI.LOGIN_PANEL));
    }
}
