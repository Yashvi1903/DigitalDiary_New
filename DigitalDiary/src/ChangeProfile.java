import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ChangeProfile {

    static void YourProfile(User u1) {
    File profileFile = new File(u1.getUserFolder(), "P_" + u1.getUserName() + ".txt");
    try (BufferedReader reader = new BufferedReader(new FileReader(profileFile))) {
        String line;
        System.out.println("\u001B[36m=== Your Profile ===\u001B[0m");
        while ((line = reader.readLine()) != null) {
            // Skip sensitive fields
            if (line.startsWith("UserID: ") ||
                line.startsWith("Primary Key: ") ||
                line.startsWith("Secondary Key: ")) {
                continue;
            }
            System.out.println(line);
        }
        // Show password status without revealing the value
        System.out.println("Primary Key: " + "\u001B[32m[SET]\u001B[0m");
        System.out.println("Secondary Key: " + "\u001B[32m[SET]\u001B[0m");
        System.out.println("\u001B[36m====================\u001B[0m");
    } catch (IOException e) {
        System.out.println("User not found!..");
    }
}
    public static void changeYourProfile(User user) {
        Scanner s1 = new Scanner(System.in);
        while (true) {
            YourProfile(user);
            System.out.println();
            System.out.println("What Do You want to change?..");
            System.out.println("1. Username: ");
            System.out.println("2. Password: ");
            System.out.println("3. Seconday PassWord: ");
            System.out.println("4. Full Name: ");
            System.out.println("5. The Name of Your Diary: ");
            System.out.println("6. Back To MyDiaries");
            System.out.println("7. Exit");
            int choice = 0;
            try {
                choice = Integer.parseInt(s1.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input. Please enter a number.");
                continue;
            }
            switch (choice) {
                case 1:
                    System.out.print("Enter new username: ");
                    String oldUsername = user.getUserName();
                    String newUsername = s1.nextLine().trim();
                    UserFileManager.updateUserIndex(oldUsername, newUsername, user.getUserID());
                    user.setUserName(newUsername);
                    System.out.println("Username updated successfully.");
                    break;

                case 2:
                    System.out.print("Enter new password: ");
                    String newPassword = s1.nextLine().trim();
                    if (!newPassword.isEmpty()) {
                        user.setPrimary_key(PasswordUtils.hashPassword(newPassword));
                        System.out.println("Password updated successfully.");

                    } else {
                        System.out.println("Password cannot be empty.");
                    }
                    break;

                case 3:
                    System.out.print("Enter new secondary password: ");
                    String newSecondaryPassword = s1.nextLine().trim();
                    if (!newSecondaryPassword.isEmpty()) {
                        user.setSecondary_key(PasswordUtils.hashPassword(newSecondaryPassword));
                        System.out.println("Secondary password updated successfully.");
                    } else {
                        System.out.println("Secondary Password can't be empty!");
                    }
                    break;

                case 4:
                    System.out.print("Enter full name: ");
                    String fullName = s1.nextLine().trim();
                    if (!fullName.isEmpty()) {
                        user.setName(fullName);
                        System.out.println("Full name updated successfully.");
                    } else {
                        System.out.println("please enter the valid name!..");
                    }
                    break;

                case 5:
                    System.out.print("Enter the name of your diary: ");
                    String diaryName = s1.nextLine().trim();
                    if (!diaryName.isEmpty()) {
                        user.setDiaryName(diaryName);
                        System.out.println("Diary name updated successfully.");
                    } else {
                        System.out.println("please enter the valid diary name!..");
                    }
                    break;

                case 6:
                    System.out.println("Returning to MyDiaries...");
                    Diary.Diarymenu(user);
                    return;

                case 7:
                    System.out.println("Exiting. Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Please choose a valid option.");
                    break;
            }
            UserFileManager.updateUser(user);
            System.out.println();
        }

    }
}
