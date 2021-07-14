package GUI;
import Design.CustomData;
import Listeners.groupMessageListener;
import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class groupMessagePane extends JFrame implements groupMessageListener {
    private ChatClient client;
    private JPanel mainPanel = new JPanel();
    private String groupName;
    private String login;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();
    private JButton leaveButton = new JButton("leave group");
    JMenuBar mb = new JMenuBar();
    private JMenu option = new JMenu("Options");
    private JMenuItem groupMembers = new JMenuItem("group members");
    private JMenuItem leaveGroup = new JMenuItem("leave group");
    private ArrayList<String> groupNamesList;
    private DefaultListModel<String> membersList = new DefaultListModel<>();
    private JList<String> groupUserList = new JList<>(membersList);
    private DefaultListModel<CustomData> groupListModel;

    public groupMessagePane(ChatClient client, String groupName, String login, ArrayList<String> groupNamesList, DefaultListModel<CustomData> groupListModel) {
        super("Message: " + groupName);
        this.client = client;
        this.groupName = groupName;
        this.login = login;
        this.groupNamesList = groupNamesList;
        this.groupListModel = groupListModel;
        client.addGroupMessageListener(this);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        messageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.toString());
                Color background;
                if ((value.toString().substring(6,9)).equals("You")){
                    background=new Color(191,235,220);
                }else background=new Color(178,225,239);
                setBackground(background);
                return c;
            }
            @Override
            public Dimension getPreferredSize ()
            {
                final Dimension ps = super.getPreferredSize ();
                ps.height = 30;
                return ps;
            }
        });
        mainPanel.setLayout(null);
        messageList.setBackground(new Color(216, 239, 237));
        JScrollPane jScrollPane = new JScrollPane(messageList);
        jScrollPane.setBounds(0, 0, 500, 450);
        jScrollPane.setSize(new Dimension(500, 450));
        mainPanel.add(jScrollPane);
        inputField.setBounds(0, 450, 400, 30);
        inputField.setSize(new Dimension(400, 30));
        mainPanel.add(inputField);
        mb.setLayout(new BorderLayout());
        option.add(groupMembers);
        option.add(leaveGroup);
        option.setSize(new Dimension(100, 30));
        mb.add(option, BorderLayout.CENTER);
        mb.setSize(new Dimension(100, 30));
        mb.setLocation(400, 450);
        mainPanel.add(mb);
        this.setContentPane(mainPanel);
        this.pack();
        this.setBackground(new Color(216, 239, 237));
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msgGroup(groupName, text);
                    listModel.addElement(LocalTime.now().toString().substring(0, 5) + " You: " + text);
                    inputField.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        leaveGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.leave(groupName);
                    groupNamesList.remove(groupName);
                    //send other users in group you left
                    client.msgGroup(groupName, "left group");
                    dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        groupMembers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                groupListPane.groupMemberList groupMemberList = new groupListPane.groupMemberList(groupUserList);
                groupMemberList.setSize(300, 400);
                groupMemberList.setVisible(true);
            }
        });
    }


    @Override
    public void onMessage(String groupName, String fromLogin, String msgBody) {
        if (groupName.equalsIgnoreCase(this.groupName) && !fromLogin.equalsIgnoreCase(login)) {
            String line;
            if (msgBody.equals("left group")) {
                line = LocalTime.now().toString().substring(0, 5) + " " + fromLogin + " left group";
                membersList.removeElement(fromLogin);
                listModel.addElement(line);
            } else if (msgBody.equals("joined group")) {
                if (!membersList.contains(fromLogin)) {
                    line = LocalTime.now().toString().substring(0, 5) + " " + fromLogin + " joined group";
                    membersList.addElement(fromLogin);
                    listModel.addElement(line);
                }
            } else {
                line = LocalTime.now().toString().substring(0, 5) + " " + fromLogin + ":" + msgBody;
                listModel.addElement(line);
                if (!this.isVisible()) {
                    for (int i = 0; i < groupListModel.size(); i++) {
                        CustomData current = groupListModel.get(i);
                        if (current.getName().equals(groupName)) {
                            current.setNewMessages(current.getNewMessages() + 1);
                        }
                    }
                }
            }
        }
    }
}

