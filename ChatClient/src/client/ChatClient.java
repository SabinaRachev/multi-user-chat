package client;

import Listeners.MessageListener;
import Listeners.userStatusListener;
import Listeners.groupListener;
import Listeners.groupMessageListener;

import java.io.*;
import java.net.Socket;


import java.util.ArrayList;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private ArrayList<userStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<groupListener> groupListeners=new ArrayList<>();
    private ArrayList<groupMessageListener> groupMessageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }


 //handle msg
    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }
    //handle group msg
    public void msgGroup(String groupName, String msgBody) throws IOException {
        String cmd = "msg " +"#"+ groupName + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }
//login client
    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);
        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }
    //add other users to group
    public  void addToGroup (String login,String groupName) throws IOException{
        String cmd = "add "+ login + " "+"#"+groupName+ "\n";
        serverOut.write(cmd.getBytes());

    }
    //leave group
    public void leave(String groupName) throws IOException {
        String cmd="leave "+"#"+groupName+"\n";
        serverOut.write(cmd.getBytes());
    }
    //join group
    public  void join (String groupName) throws IOException{
        String cmd = "join "+"#"+groupName+ "\n";
        serverOut.write(cmd.getBytes());
    }
    public  boolean registry (String login, String password) throws IOException{
        String cmd = "registry " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);
        if ("registration successful".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                System.out.println("the line----"+line);
                String[] tokens = line.split(" ");
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msgGroup".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",4);
                        handleGroupMessage(tokensMsg);
                    }
                    else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = line.split(" ",3);
                        handleMessage(tokensMsg);
                    }
                    else if ("left".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",3);
                        handleLeft(tokensMsg);
                    }
                    else if ("joined".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",3);
                        handleJoin(tokensMsg);
                    }
                    else if ("Member".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",3);
                        handleMember(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMember(String[] tokensMsg) {
        String groupName=tokensMsg[2].substring(1);
        String login=tokensMsg[1];
        String msgBody="joined group";
        for (groupMessageListener listener:groupMessageListeners){
            listener.onMessage(groupName,login,msgBody);
        }
    }

    private void handleLeft(String[] tokensMsg) {
        String login=tokensMsg[1];
        String groupName=tokensMsg[2].substring(1);
        for(groupListener listener : groupListeners) {
            listener.leaveGroup(login,groupName);
        }
    }

    private void handleGroupMessage(String[] tokensMsg) {
        String groupName=tokensMsg[1].substring(1);
        String login=tokensMsg[2];
        String msgBody=tokensMsg[3];
        for (groupMessageListener listener:groupMessageListeners){
            listener.onMessage(groupName,login,msgBody);
        }
    }

    //handle sent messages to other users
    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];
        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }
    private void handleJoin(String [] tokens) throws IOException {
        String login=tokens[1];
        String groupName=tokens[2].substring(1);
        for(groupListener listener : groupListeners) {
            listener.joinGroup(login,groupName);
            System.out.println(listener.getLogin());
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(userStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(userStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(userStatusListener listener) {
        userStatusListeners.add(listener);
    }
    public void addGroupListener(groupListener listener) {
        groupListeners.add(listener);
    }
    public void addGroupMessageListener(groupMessageListener listener) {
        groupMessageListeners.add(listener);
    }
    public void removeGroupMessageListener(groupMessageListener listener) {
        groupMessageListeners.remove(listener);
    }
    public void removeGroupListener(groupListener listener) {
        groupListeners.remove(listener);
    }


    public void removeUserStatusListener(userStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

}

