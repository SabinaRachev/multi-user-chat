import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class UsersFile {

    private String   fileName;
    private ArrayList<String> users;

    public UsersFile(String fileName){
        this.fileName = fileName;
        users=grabUsers();
    }

    public ArrayList<String> grabUsers(){
        try {
            users = (ArrayList<String>) Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }catch(Exception grabFileScoreException){
            return null;
        }
        return users;
    }
    public void saveUsers(){
        try {
            Files.write(Paths.get(fileName),users, Charset.defaultCharset());
        }catch(Exception saveScoreInFileException){
        }
    }
    //return index by user name
    public int getUserNameIndex(String userName) {

        for (int i = 0; i < users.size(); i++)
            if(users.get(i).contains(userName))
                return i;
        return -1;
    }
    //return index by password
    public int getUserNameIndexByPassword(String password) {
        for (int i = 0; i < users.size(); i++)
            if(getPasswordOfUserByIndex(i).equals(password)) return i;
        return -1;
    }
    //return password by index
    public String getPasswordOfUserByIndex(int userIndex){
        return  users.get(userIndex).split(":")[1];
    }
    //return name by index
    public String getUserNameByIndex(int userIndex){
        return  users.get(userIndex).split(":")[0];
    }
    //update the user list
    public void editUserList(String userName,String password){
        users=grabUsers();
        users.add(userName+":"+password);
        saveUsers();
    }
    public ArrayList<String> getUsers(){
        return users;
    }
}

