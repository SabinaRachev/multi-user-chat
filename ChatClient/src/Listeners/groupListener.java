package Listeners;

import java.io.IOException;

public interface groupListener {
    public void joinGroup(String login,String groupName) throws IOException;
    public void leaveGroup(String login,String groupName);
   public String getLogin();
}
