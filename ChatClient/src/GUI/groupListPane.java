package GUI;
import client.ChatClient;

import Design.CustomData;
import Design.cellRender;
import Listeners.groupListener;
import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;


public class groupListPane extends JPanel implements groupListener {
    private final ChatClient client;
    private JList<CustomData> groupListUI;
    private DefaultListModel<CustomData> groupListModel;
    private String login;
    private ArrayList<MessagePaneByGroup> messagePaneList=new ArrayList<>();
    private ArrayList<String> groupNameList;

    public groupListPane(ChatClient client, String login, ArrayList<String> groupNameList) {
        this.client = client;
        this.client.addGroupListener(this);
        this.login=login;
        this.groupNameList=groupNameList;
        groupListModel = new DefaultListModel<>();
        groupListUI = new JList<>(groupListModel);
        groupListUI.setCellRenderer(new cellRender());
        setLayout(new BorderLayout());
        groupListUI.setBackground(new Color(216,239,237));

        JScrollPane mainPanel=new JScrollPane(groupListUI);
        add(mainPanel, BorderLayout.CENTER);
        groupListUI.addMouseListener(new MouseAdapter() {
            @Override
            //open chat with user
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String groupName = groupListUI.getSelectedValue().getName();
                    for (int i = 0; i < groupListModel.size(); i++) {
                        if (groupListModel.get(i).getName().equals(groupName)) {
                            groupListModel.get(i).setNewMessages(0);
                            break;
                        }
                    }
                        for (MessagePaneByGroup current : messagePaneList) {
                            if (current.getGroupName().equals(groupName)) {
                                groupMessagePane groupMessagePane = current.getMessagePane();
                                groupMessagePane.setSize(515, 518);
                                groupMessagePane.setVisible(true);
                                break;
                            }
                        }
                }
            }
        });
    }

    public DefaultListModel<CustomData> getGroupListModel(){
        return groupListModel;
    }

    @Override
    public void joinGroup(String login, String groupName) throws IOException {
        if (this.login.equalsIgnoreCase(login)) {
            groupNameList.add(groupName);
            groupListModel.addElement(new CustomData(0,groupName));
            messagePaneList.add(new MessagePaneByGroup(groupName,new groupMessagePane(client,groupName,login,groupNameList,groupListModel)));
            client.msgGroup(groupName,"joined group");
        }
    }

    @Override
    public void leaveGroup(String login, String groupName) {
        if (this.login.equalsIgnoreCase(login)) {
            for (int i=0;i<groupListModel.size();i++){
                if (groupListModel.get(i).getName().equals(groupName)){
                    groupListModel.remove(i);
                    break;
                }
            }
        }
    }
    public String getLogin(){
        return login;
    }

    public static class groupMemberList extends JFrame {
        private JPanel mainPanel=new JPanel();
        private JList<String> membersList;
        public groupMemberList(JList<String> membersList){
            super("Groups Members");
            this.membersList=membersList;
            JScrollPane jScrollPane=new JScrollPane(membersList);
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(jScrollPane,BorderLayout.CENTER);
            this.setContentPane(mainPanel);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.pack();
            this.setBackground(new Color(216,239,237));
        }
    }
}

