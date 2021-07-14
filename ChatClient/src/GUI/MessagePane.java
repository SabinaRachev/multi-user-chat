package GUI;

import Design.CustomData;
import Listeners.MessageListener;
import client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Formatter;

public class MessagePane extends JFrame implements MessageListener {
    private JPanel mainPanel=new JPanel();
    private ChatClient client;
    private final String login;
    private DefaultListModel<String > listModel = new DefaultListModel<>();
    private JList<String > messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();
    private DefaultListModel<CustomData> userListModel;

    public MessagePane(ChatClient client, String login, DefaultListModel<CustomData> userListModel) {
        super("message: "+login);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.client = client;
        this.login = login;
        client.addMessageListener(this);
        this.userListModel=userListModel;
        mainPanel.setLayout(new BorderLayout());
        messageList.setBackground(new Color(216,239,237));
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
        JScrollPane jScrollPane=new JScrollPane(messageList);
        mainPanel.add(jScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputField, BorderLayout.SOUTH);
        this.setContentPane(mainPanel);
        this.pack();
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msg(login, text);
                    listModel.addElement(LocalTime.now().toString().substring(0,5)+" You: "+text);
                    inputField.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if (fromLogin.equalsIgnoreCase(login)) {
            Formatter fmt = new Formatter();
            String line = LocalTime.now().toString().substring(0,5) +" "+fromLogin + ":"+msgBody;
            listModel.addElement(line);
           if (!this.isVisible()){
                for (int i=0;i<userListModel.size();i++){
                    CustomData current=userListModel.get(i);
                    if (current.getName().equals(fromLogin)){
                       current.setNewMessages(current.getNewMessages()+1);
                    }
                }
           }
        }
    }
}
