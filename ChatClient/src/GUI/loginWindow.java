package GUI;
import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

public class loginWindow extends JFrame {
    private JTextField loginField;
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JLabel ErrorMessage;
    private ChatClient client;
    private JLabel title;
    private JTextField ip;
    private JTextField port;
    private String login;
    private ArrayList<String> groupNamesList=new ArrayList<>();
    public loginWindow (){
        super("Chat");
        ip.setText("localhost");
        port.setText("8818");
        this.client=new ChatClient("localhost",8818);
        client.connect();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.setBackground(new Color(216,239,237));
        title.setFont(new Font("arial",Font.PLAIN,30));

        this.setVisible(true);
        //login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            doLogin();
            }
        });
        //sign up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ip.getText().equals("localhost")||!port.equals("8818")){
                   client = new ChatClient(ip.getText(), new Integer(port.getText()));
                    client.connect();
                }
                registrationWindow registrationWindow=new registrationWindow(client);
                registrationWindow.setSize(new Dimension(350,350));
                registrationWindow.setVisible(true);
                registrationWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        setVisible(true);
                    }
                });
                 setVisible(false);
            }
        });
    }

    public static void main(String[] args) {
        loginWindow loginWindow=new loginWindow();
        loginWindow.setSize(new Dimension(350,350));
        loginWindow.setVisible(true);
    }
    public void doLogin() {
        this.login = loginField.getText();
        if (!ip.getText().equals("localhost")||!port.equals("8818")){
            this.client = new ChatClient(ip.getText(), new Integer(port.getText()));
        client.connect();
    }  String password=String.valueOf(passwordField.getPassword());
        ChatWindow chatWindow=new ChatWindow(client,login,groupNamesList);
        chatWindow.setSize(new Dimension(415,600));
        chatWindow.setVisible(false);
        chatWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    for (int i=0;i<groupNamesList.size();i++){
                        client.leave(groupNamesList.get(i));
                        client.msgGroup(groupNamesList.get(i),"left group");
                    }
                    client.logoff();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        try {
            if (!login.equals("")&&!password.equals("")&&client.login(login,password)){
                //bring up the chat window
                chatWindow.setVisible(true);
                setVisible(false);
            }else {
                ErrorMessage.setText("Invalid user name/password");
                ErrorMessage.setForeground(Color.red);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
