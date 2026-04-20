import java.util.Scanner;

public class loginService {
    public static User login() {
        Scanner s1 = new Scanner(System.in);
        System.out.print("\u001B[32m" + "please enter the Username: " + "\u001B[0m");
        String enteredUsername = s1.nextLine();
        if(enteredUsername.trim().isEmpty())
        {
            System.out.println("\u001B[31mUsername cannot be empty.\u001B[0m");
            return null;
        }
        String userID = UserFileManager.getUserIndex().get(enteredUsername);
        if (userID == null) {
            System.out.println("\u001B[31mUser not found.\u001B[0m");
            return null;
        }
        int count = 0;
        User u = UserFileManager.getUserObjMap().get(enteredUsername);
        while (count < 3) {
            System.out.print("\u001B[32m" + "please enter the Password: " + "\u001B[0m");
            String enteredPassword = s1.nextLine();
            if (u != null && u.getPrimary_key().equals(enteredPassword)) {
                System.out.println("\u001B[34mLogin successful! Welcome back, " + u.getName() + "\u001B[0m");
                return u;
            } else {
                count++;
                System.out.println("\u001B[31mInvalid password.\u001B[0m");
                if(count<3)
                {
                System.out.println("Try again!..");
                }
            }
        }
        System.out.println();
        System.out.println("Too many failed attempts."+"\nDo you wnat to use the secondary password?..(Enter 'yes')");
        String input = s1.nextLine().trim();
        if (input.equalsIgnoreCase("yes")) {
            int subcount = 0;
            while (subcount < 3) {
                System.out.print("Enter the secondary password: ");
                String secondarykey = s1.nextLine().trim();
                if (u.getSecondary_key().equals(secondarykey)) {
                    System.out.println("\u001B[34mYou have logged in successfully using the secondary password!\u001B[0m");
                    System.out.print("Please enter NEW Primary PassWord: ");
                    String newPrimaryKey = "";
                    while (newPrimaryKey.trim().isEmpty()) {
                        System.out.print("Please enter a NEW Primary Password: ");
                        newPrimaryKey = s1.nextLine().trim();
                        if (newPrimaryKey.isEmpty()) {
                            System.out.println("Password cannot be empty.");
                        }
                    }
                    u.setPrimary_key(newPrimaryKey);
                    UserFileManager.updateUser(u);
                    System.out.println("Your password has been reset. You are now logged in.");
                    return u;
                } else {
                    subcount++;
                    System.out.println("Incorrect secondary password!..");
                    System.out.println("Try Again!..");
                }
            }
            System.out.println("Sorry,Your attempt limit has been exceeded!..");
        } else {
            System.out.println("You can't login without verifying..Goodbye..");
        }
        return null;
    }
}
