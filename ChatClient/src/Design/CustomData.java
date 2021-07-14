package Design;

import java.awt.*;

public class CustomData {

    private int newMessages;
    private String name;

    public CustomData ( int newMessages, String name )
    {
        this.newMessages = newMessages;
        this.name = name;
    }


    public int getNewMessages ()
    {
        return newMessages;
    }
    public void setNewMessages (int newMessages)
    {
        this.newMessages=newMessages;
    }
    public String getName ()
    {
        return name;
    }
}
