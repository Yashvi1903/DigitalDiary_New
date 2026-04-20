import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

public class User {
    private String UserID;
    private String UserName;
    private String primary_key;
    private String Secondary_key;
    private String Name;
    private int age;
    private String gender;
    private Diary currDiary;
    private LocalDateTime lastTime;
    private String diaryName;
    private File userFolder;
    protected static Map<String, String> UserIndex = new HashMap<>();
    protected static Map<String, User> UserObjMap = new HashMap<>();
    //protected static final String Index_File = "Users/user_index.txt";

    public User(String UserID, String UserName, String primary_key, String Secondary_key, String Name, int age,
            String gender, File userFolder, String diaryName, boolean writeToIndex) {
        this.UserID = UserID;
        this.UserName = UserName;
        this.primary_key = primary_key;
        this.Secondary_key = Secondary_key;
        this.Name = Name;
        this.age = age;
        this.gender = gender;
        this.lastTime = LocalDateTime.now();
        this.userFolder = userFolder;
        this.diaryName = diaryName;
        UserObjMap.put(UserName, this);
        if (writeToIndex) {
            if (!UserIndex.containsKey(UserName)) {
                UserIndex.put(UserName, UserID);
                UserFileManager.saveIndexToFile(UserName, UserID);
            } else {
                System.out.println("Username already exists in index map.not writing to file again.");
            }

        }
    }

    public void setUserFolder(File userFolder) {
        this.userFolder = userFolder;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPrimary_key() {
        return primary_key;
    }

    public String getSecondary_key() {
        return Secondary_key;
    }

    public String getName() {
        return Name;
    }

    public Diary getCurrDiary() {
        return currDiary;
    }

    public String getDiaryName() {
        return diaryName;
    }

    public File getUserFolder() {
        return userFolder;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setPrimary_key(String primary_key) {
        this.primary_key = primary_key;
    }

    public void setSecondary_key(String secondary_key) {
        Secondary_key = secondary_key;
    }

    public void setName(String name) {
        Name = name;
    }
    
    public void setDiaryName(String diaryName) {
        this.diaryName = diaryName;
    }
    

}
