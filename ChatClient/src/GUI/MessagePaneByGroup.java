package GUI;

public class MessagePaneByGroup {
    private String groupName;
    private groupMessagePane messagePane;
    public MessagePaneByGroup(String groupName,groupMessagePane messagePane){
        this.groupName=groupName;
        this.messagePane=messagePane;
    }
    public String getGroupName(){
        return groupName;
    }
    public groupMessagePane getMessagePane(){
        return messagePane;
    }
}
