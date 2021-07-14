package GUI;

import GUI.MessagePane;

public class MessagePaneByLogin {
    private String login;
    private MessagePane messagePane;
    public MessagePaneByLogin(String login,MessagePane messagePane){
        this.login=login;
        this.messagePane=messagePane;
    }
    public String getLogin(){
        return login;
    }
    public MessagePane getMessagePane(){
        return messagePane;
    }
}
