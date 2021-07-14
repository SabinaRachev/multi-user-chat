package GUI;

import Design.CustomData;
import Design.cellRender;
import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class groupWindow extends JFrame {
    private ChatClient client;
    private  JPanel mainPanel;
    private JList<CustomData> userListUI;
    private DefaultListModel<CustomData> userList;
    private  JTextField groupName;
    private JLabel title;
    private JButton OkButton;
    private JButton backButton;

    private ArrayList<String> groupNamesList;

    public groupWindow(ChatClient client, DefaultListModel<CustomData> userListModel, ArrayList<String> groupNamesList ) {
        super("New group");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.client = client;
        this.groupNamesList=groupNamesList;
        //set online users list
        userList=userListModel;
        mainPanel=new JPanel();
        userListUI = new JList<>(userList);
       userListUI.setCellRenderer(new cellRender());
        mainPanel.setLayout(null);
        userListUI.setBackground(new Color(216,239,237));
        userListUI.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                }
                else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });
        JScrollPane scrollPane=new JScrollPane(userListUI);
        scrollPane.setBounds(0,50,400,400);
        scrollPane.setSize(400,400);
        mainPanel.add(scrollPane);
        mainPanel.setBackground(new Color(216,239,237));
        //Ok button
        OkButton=new JButton("Ok");
        OkButton.setSize(new Dimension(150,30));
        OkButton.setLocation(110,460);
        mainPanel.add(OkButton);
        //back button
        backButton=new JButton("Back");
        backButton.setSize(new Dimension(150,30));
        backButton.setLocation(110,500);
        mainPanel.add(backButton);
        //set title and text field
        title=new JLabel("group name:");
        groupName=new JTextField();
        groupName.setSize(new Dimension(250,30));
        groupName.setLocation(97,10);
        title.setSize(new Dimension(100,30));
        title.setLocation(10,10);
        mainPanel.add(groupName);
        mainPanel.add(title);
        this.getContentPane().add(mainPanel);
        this.pack();
        //ok button
        OkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = groupName.getText();
                if (groupNameIsValid(name)) {
                    if (!groupNamesList.contains(name)) {
                        try {
                            client.join(name);
                            //add selected users to group
                            for (CustomData selectedValue : userListUI.getSelectedValuesList()) {
                                client.addToGroup(selectedValue.getName(), name);
                            }
                            groupName.setText("");
                            setVisible(false);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }else JOptionPane.showMessageDialog(new JFrame(), "group name already exists", "Error", JOptionPane.ERROR_MESSAGE);
                }else  JOptionPane.showMessageDialog(new JFrame(), "group name is not valid", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                groupName.setText("");
                setVisible(false);

            }
        });
    }
    public boolean groupNameIsValid(String groupName){
        if (groupName.equals(""))
            return false;
        for (int i = 0; i < groupName.length(); i++) {
            char curr = groupName.charAt(i);
            if ((curr < 'a' || curr > 'z') && (curr < 'A' || curr > 'Z') && (curr < '1' || curr > '9'))
                return false;
        }
        return true;
    }
}

