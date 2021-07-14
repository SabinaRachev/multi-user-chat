package GUI;
import client.ChatClient;

import Design.CustomData;
import Design.cellRender;
import Listeners.userStatusListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UserListPane extends JPanel implements userStatusListener {
    private final ChatClient client;
    private JList<CustomData> userListUI;
    private DefaultListModel<CustomData> userListModel;
    private ArrayList<MessagePaneByLogin> messagePaneList=new ArrayList<>();


    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        userListUI.setBackground(new Color(216,239,237));
        userListUI.setCellRenderer(new cellRender());
        JScrollPane mainPanel=new JScrollPane(userListUI);
        add(mainPanel, BorderLayout.CENTER);
        userListUI.setVisibleRowCount(7);
        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            //open chat with user
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    CustomData selected=userListUI.getSelectedValue();
                    String login = selected.getName();
                    for (int i=0;i<userListModel.size();i++){
                        if (userListModel.get(i).getName().equals(login)){
                            userListModel.get(i).setNewMessages(0);
                            break;
                        }
                    }for (MessagePaneByLogin current : messagePaneList) {
                        if (current.getLogin().equals(login)) {
                           MessagePane messagePane=current.getMessagePane();
                            messagePane.setSize(500, 500);
                            messagePane.setVisible(true);
                            break;
                        }
                    }

                }
            }
        });
    }


    @Override
    //show online users
    public void online(String login) {
        userListModel.addElement(new CustomData(0,login));
        MessagePane messagePane= new MessagePane(client, login,userListModel);
        messagePaneList.add(new MessagePaneByLogin(login,messagePane));
    }

    @Override
    public void offline(String login) {
        for (int i=0;i<userListModel.size();i++){
            if (userListModel.get(i).getName().equals(login)){
                userListModel.remove(i);
                break;
            }
        }
    }

    public DefaultListModel<CustomData> getUserListModel(){

        return userListModel;
    }
}
