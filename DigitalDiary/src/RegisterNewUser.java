import java.io.File;
import java.util.Scanner;
import java.util.UUID;

public class RegisterNewUser {

    public static User Register() {
        Scanner s1 = new Scanner(System.in);
        String username;

        while (true) {
            System.out.print("Enter username: ");
            username = s1.nextLine();

            if (UserFileManager.getUserIndex().containsKey(username)) {
                System.out.println("\u001B[31mUsername already exists. Please choose a different username.\u001B[0m");
            } else {
                break;
            }
        }
        System.out.print("Enter password: ");
        String primary_key = s1.nextLine();
        System.out.print("Enter seconday PassWord: ");
        String Secondary_key = s1.nextLine();
        System.out.print("Enter Full name: ");
        String Name = s1.nextLine();

        int age = 0;
        while (true) {
            System.out.print("Enter age: ");
            try {
                age = Integer.parseInt(s1.nextLine());
                if (age > 0)
                    break;
                else
                    System.out.println("Age must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid age.");
            }
        }
        System.out.print("Enter gender: ");
        String gender = s1.nextLine();
        System.out.print("Enter the name of your diary: ");
        String diaryname = s1.nextLine();
        // String hkey= PasswordUtils.hashPassword(primary_key);
        String UserID = UUID.randomUUID().toString();
        // defines folder path based on unique ID
        File folder = new File("Users/" + UserID);
        if (!folder.exists()) {
            // creates the folder and all necessary parent directories
            folder.mkdirs();
        }

        User newUser =  new User(UserID, username, primary_key, Secondary_key, Name, age, gender, folder, diaryname, true);
        //UserFileManager.saveIndexToFile(username, UserID);
        UserFileManager.saveToFile(newUser);
        return newUser;
    }

}
