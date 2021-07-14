import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();
    private UsersFile usersFile;

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        usersFile=new UsersFile("UsersFile.txt");
    }

    public void run() {
        try {
            HandleClinetSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void HandleClinetSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
      while ( (line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                        handlerLogOff();
                        break;
                    } else if ("login".equalsIgnoreCase(cmd)) {
                        handlerlogin(outputStream, tokens);
                    } else if ("leave".equalsIgnoreCase(cmd)) {
                        handlerLeave(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] msgTokens = line.split(" ", 3);
                        handlerMessage(msgTokens);
                    } else if ("join".equalsIgnoreCase(cmd)) {
                        handlerJoin(tokens);
                    }else if ("registry".equalsIgnoreCase(cmd))
                        handlerRegistry(outputStream,tokens);
                    else if ("add".equalsIgnoreCase(cmd)){
                        handlerAdd(outputStream,tokens);
                    }
                    else {
                        String msg = "unknown :" + line + "\n";
                        outputStream.write(msg.getBytes());
                    }
                }
            }
        inputStream.close();
        outputStream.close();
        clientSocket.close();
}
//handle adding users to group
    private void handlerAdd(OutputStream out,String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String topic = tokens[2];
            ArrayList<ServerWorker> workList = server.getWorkerslist();
            if (isMemberOfTopic(topic)) {
                for (ServerWorker worker : workList) {
                    if (login.equals(worker.getLogin())) {
                        String msg = "added " + login + " " + topic + "\n";
                        out.write(msg.getBytes());
                        System.out.println(login + " was added to " + topic);
                        String[] tokens2 = {"join", topic};
                        worker.handlerJoin(tokens2);
                    }
                }
            } else {
                outputStream.write("please join group first".getBytes());
                System.out.println("error 1");
            }
        } else {
            outputStream.write("unknown".getBytes());
            System.out.println("error 2");
        }
    }
//handle registration
    private void handlerRegistry(OutputStream out, String[] tokens) throws IOException {
        if (tokens.length==3) {
            String login = tokens[1];
            String password = tokens[2];
            //user name doesnt exist
            if (usersFile.getUserNameIndex(login) == -1) {
                usersFile.editUserList(login, password);
                String msg="registration successful\n";
                out.write(msg.getBytes());
            }
            else {
                String msg="user name already exists\n";
                out.write(msg.getBytes());
            }
        }
        else outputStream.write("unknown".getBytes());
    }

    //handle leaving a group
    private void handlerLeave(String[] tokens) throws IOException {
        if (tokens.length>1){
            String topic=tokens[1];
            topicSet.remove(topic);
            String onlineMsg="left "+login+" "+topic+"\n";
            outputStream.write(onlineMsg.getBytes());
            ArrayList<ServerWorker> workList = server.getWorkerslist();
            //notify other users in group
            for (ServerWorker worker:workList){
                if (!login.equals(worker.getLogin())&&worker.isMemberOfTopic(topic))
                    worker.send(onlineMsg);
            }
        }
    }

    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
}
//handler joining a group
    private void handlerJoin(String[] tokens) throws IOException {
        if (tokens.length==2){
            String topic=tokens[1];
           topicSet.add(topic);
            String msg="joined "+login+" "+topic+"\n";
           outputStream.write(msg.getBytes());
            String onlineMsg="joined "+login+" "+topic+"\n";
            System.out.println(onlineMsg);
            ArrayList<ServerWorker> workList = server.getWorkerslist();
            //notify other users in group
            for (ServerWorker worker:workList){
                if (!login.equals(worker.getLogin())){
                    worker.send(onlineMsg);
                    if (worker.isMemberOfTopic(topic)){
                        outputStream.write(("Member "+worker.getLogin()+" "+topic+"\n").getBytes());
                    }
                }
            }
        }else outputStream.write("unknown".getBytes());
    }

    // format 1 :  msg login body
    //format 2 msg group: "msg" "#topic" body
    private void handlerMessage(String[] tokens) throws IOException {
        if (tokens.length>= 3) {
            String sendTo = tokens[1];
            String body = tokens[2];
            boolean isTopic = sendTo.charAt(0) == '#';
            ArrayList<ServerWorker> workList = server.getWorkerslist();
            for (ServerWorker worker : workList) {
                //msg to a group
                if (isTopic) {
                    if (worker.isMemberOfTopic(sendTo)) {
                        String outMsg = "msgGroup " + sendTo + " " + login + " " + body + "\n";
                        worker.send(outMsg);
                    }
                } //msg to a single user
                else if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }else {
            outputStream.write("unknown".getBytes());
        }
    }
//handle logoff of a user
    private void handlerLogOff() throws IOException {
        server.removeWorker(this);
        System.out.println(login+" "+"is logged off");
    ArrayList<ServerWorker>workList= server.getWorkerslist();
    if (workList.size()>0) {
        String onlineMsg = "offline " + login + "\n";
        //notify other online users
        for (ServerWorker worker : workList) {
            if (!login.equals(worker.getLogin()))
                worker.send(onlineMsg);
        }
    }
        clientSocket.close();
    }
//handle login of a user
    private void handlerlogin(OutputStream out, String[] tokens) throws IOException {
        if (tokens.length==3){
            String login=tokens[1];
            String password=tokens[2];
            int loginIndex=usersFile.getUserNameIndex(login);
            if (loginIndex!=-1&&usersFile.getPasswordOfUserByIndex(loginIndex).equals(password)){
                String msg="Ok login\n";
                out.write(msg.getBytes());
                this.login=login;
                System.out.println("online--"+login);
               ArrayList<ServerWorker>workList= server.getWorkerslist();
               //sent current user all other online users
                for (ServerWorker worker:workList) {
                        if (worker.getLogin()!=null){
                            if (!login.equals(worker.getLogin())){
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }//notify  other online users that current user is online
               String onlineMsg="online "+login+"\n";
               for (ServerWorker worker:workList){
                   if (!login.equals(worker.getLogin()))
                  worker.send(onlineMsg);
               }
            }else {
                String msg="error login\n";
                out.write(msg.getBytes());
              System.err.println("login failed for "+login);
            }
        }else {
            out.write("unknown".getBytes());
        }
    }
    public String getLogin(){
       return login;
    }
    public void send(String msg) throws IOException {
        if (login!=null)
     outputStream.write(msg.getBytes());
    }
    }
