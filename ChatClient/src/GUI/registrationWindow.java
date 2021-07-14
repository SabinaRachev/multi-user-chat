package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import client.ChatClient;

public class registrationWindow extends JFrame {
    private JTextField name;
    private JButton signButton;
    private JButton backButton;
    private JPanel mainPanel;
    private JPasswordField cPassword;
    private JPasswordField Password;
    private JLabel msgError;
    private ChatClient client;
    private JLabel title;


    public registrationWindow(ChatClient client) {
        super("registration");
        this.client = client;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        mainPanel.setBackground(new Color(216, 239, 237));
        title.setFont(new Font("arial", Font.PLAIN, 30));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginWindow loginWindow = new loginWindow();
                loginWindow.setSize(new Dimension(350, 350));
                loginWindow.setVisible(true);
                dispose();
            }
        });
        signButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = name.getText();
                //check user name
                if (validUserName(userName)) {
                    String password = String.valueOf(Password.getPassword());
                    //check the password
                    if (validPassword(password)) {
                        if (password.equals(String.valueOf(cPassword.getPassword()))) {
                            try {
                                if (client.registry(userName, password)) {//registration is successful
                                    loginWindow loginWindow = new loginWindow();
                                    loginWindow.setSize(new Dimension(350, 350));
                                    loginWindow.setVisible(true);
                                    dispose();
                                } else {
                                    msgError.setText("user name already exists");//registration failed
                                    msgError.setForeground(Color.red);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            msgError.setText("passwords dont match");
                            msgError.setForeground(Color.red);
                        }
                    }else {
                        msgError.setText("Invalid password");
                        msgError.setForeground(Color.red);
                    }
                }else {
                    msgError.setText("Invalid user name");
                    msgError.setForeground(Color.red);
                }
            }
        });
    }

    public boolean validUserName(String userName) {
        if (userName.equals("")||userName.length() > 15||!validInput(userName))
            return false;
        return true;
    }
    public boolean validPassword(String password) {
        if (password.equals("")||password.length() <4||!validInput(password))
            return false;
        return true;
    }
    public boolean validInput(String input){
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            if ((curr < 'a' || curr > 'z') && (curr < 'A' || curr > 'Z') && (curr < '1' || curr > '9'))
                return false;
        }
        return true;
    }
}
