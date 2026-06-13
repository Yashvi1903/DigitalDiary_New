import java.io.File;
import java.util.Scanner;
import java.util.UUID;

public class RegisterNewUser {

    public static User Register() {
        Scanner s1 = AppContext.scanner();
        String username;

        while (true) {
            System.out.print("Enter username: ");
            username = s1.nextLine();

            if (username.trim().equals("-1")) {
                System.out.println("Returning to main menu...");
                return null;
            }
            if (UserFileManager.getUserIndex().containsKey(username)) {
                System.out.println(
                        "\u001B[31mUsername already exists. Please enter a different username or enter -1 to exit.\u001B[0m");
            } else {
                break;
            }
        }

        System.out.print("Enter password (or -1 to exit): ");
        String pwInput = s1.nextLine();
        if (pwInput.trim().equals("-1")) {
            System.out.println("Returning to main menu...");
            return null;
        }
        String primary_key = PasswordUtils.hashPassword(pwInput);

        System.out.print("Enter secondary password (or -1 to exit): ");
        String secPwInput = s1.nextLine();
        if (secPwInput.trim().equals("-1")) {
            System.out.println("Returning to main menu...");
            return null;
        }
        String Secondary_key = PasswordUtils.hashPassword(secPwInput);

        System.out.print("Enter full name (or -1 to exit): ");
        String Name = s1.nextLine();
        if (Name.trim().equals("-1")) {
            System.out.println("Returning to main menu...");
            return null;
        }

        int age = 0;
        while (true) {
            System.out.print("Enter age (or -1 to exit): ");
            String ageInput = s1.nextLine().trim();
            if (ageInput.equals("-1")) {
                System.out.println("Returning to main menu...");
                return null;
            }
            try {
                age = Integer.parseInt(ageInput);
                if (age > 0)
                    break;
                else
                    System.out.println("Age must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid age.");
            }
        }

        System.out.print("Enter gender (or -1 to exit): ");
        String gender = s1.nextLine();
        if (gender.trim().equals("-1")) {
            System.out.println("Returning to main menu...");
            return null;
        }

        System.out.print("Enter the name of your diary (or -1 to exit): ");
        String diaryname = s1.nextLine().trim();
        while (diaryname.isEmpty()) {
            System.out.println("Diary name cannot be empty.");
            System.out.print("Enter the name of your diary: ");
            diaryname = s1.nextLine().trim();
        }
        if (diaryname.trim().equals("-1")) {
            System.out.println("Returning to main menu...");
            return null;
        }

        String UserID = UUID.randomUUID().toString();
        File folder = new File("Users/" + UserID);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        User newUser = new User(UserID, username, primary_key, Secondary_key, Name, age, gender, folder, diaryname,
                true);
        UserFileManager.saveToFile(newUser);
        return newUser;
    }

}