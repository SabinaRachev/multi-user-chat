package GUI;
import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class ChatWindow extends JFrame {
    private final String login;
    private final GUI.groupListPane groupListPane;
    private JPanel mainPanel;
    private JPanel titleUsersPanel;
    private JPanel titleGroupPanel;
    private JButton groupButton;
    private JButton logOffButton;
    private UserListPane userListPane;
    private ChatClient client;
    private ArrayList<String> groupNamesList;
    public ChatWindow(ChatClient client, String login, ArrayList<String> groupNamesList){
        super("Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.client=client;
        this.login=login;
        this.groupNamesList=groupNamesList;
        //main panel
        mainPanel=new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(139,187,186));
        mainPanel.setBounds(0, 0, 400, 50);
        mainPanel.setPreferredSize(new Dimension(400, 50));
        //group button
        groupButton=new JButton();
        groupButton.setSize(new Dimension(100,30));
        groupButton.setLocation(150,5);
        groupButton.setText("add group");
        //log off button
        logOffButton=new JButton();
        logOffButton.setSize(new Dimension(100,30));
        logOffButton.setLocation(270,5);
        logOffButton.setText("logoff");
       //login name title
        JLabel loginTitle=new JLabel(login);
       loginTitle.setFont(new Font("arial",Font.PLAIN,20));
        loginTitle.setSize(new Dimension(80,20));
        loginTitle.setLocation(7,10);
        mainPanel.add(loginTitle);
        mainPanel.add(groupButton);
        mainPanel.add(logOffButton);
        setLayout(null);
        this.getContentPane().add(mainPanel);
        //title panel
        titleUsersPanel=new JPanel();
        titleUsersPanel.setBackground(new Color(161,215,255));
        titleUsersPanel.setBounds(0, 50, 400, 30);
        titleUsersPanel.setPreferredSize(new Dimension(400,30));
        JLabel title=new JLabel();
        title.setFont(new Font("arial",Font.PLAIN,20));
        title.setText("Online users");
        title.setLocation(120,52);
        titleUsersPanel.add(title);
        this.getContentPane().add(titleUsersPanel);
        //user list panel
        userListPane=new UserListPane(this.client);
        userListPane.setBounds(0,80,400,300);
        userListPane.setPreferredSize(new Dimension(400,300));
        this.getContentPane().add(userListPane);
        //title  group panel
        titleGroupPanel=new JPanel();
        titleGroupPanel.setBackground(new Color(161,215,255));
        titleGroupPanel.setBounds(0, 380, 400, 30);
        titleGroupPanel.setPreferredSize(new Dimension(400,30));
        JLabel titleGroup=new JLabel();
        titleGroup.setFont(new Font("arial",Font.PLAIN,20));
        titleGroup.setText("Groups");
        titleGroupPanel.add(titleGroup);
        this.getContentPane().add(titleGroupPanel);
        //groups list panel
        groupListPane=new groupListPane(this.client,this.login,groupNamesList);
        groupListPane.setBounds(0,410,400,190);
        groupListPane.setPreferredSize(new Dimension(400,190));
        this.getContentPane().add(groupListPane);
        this.pack();
        groupWindow groupWindow=new groupWindow(client,userListPane.getUserListModel(),groupNamesList);
        logOffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.logoff();
                    dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        groupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             groupWindow.setSize(new Dimension(400,600));
                groupWindow.setVisible(true);

            }
        });
    }
}
