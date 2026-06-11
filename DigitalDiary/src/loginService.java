import java.util.Scanner;

public class loginService {
    public static User login() {
        Scanner s1 = new Scanner(System.in);

        // ── Username check ──
        System.out.print("\u001B[32mPlease enter the Username: \u001B[0m");
        String enteredUsername = s1.nextLine();
        if (enteredUsername.trim().isEmpty()) {
            System.out.println("\u001B[31mUsername cannot be empty.\u001B[0m");
            return null;
        }

        String userID = UserFileManager.getUserIndex().get(enteredUsername);
        if (userID == null) {
            System.out.println("\u001B[31mUser not found.\u001B[0m");
            return null;
        }

        User u = UserFileManager.getUserObjMap().get(enteredUsername);

        // ── Primary key — 3 attempts ──
        int count = 0;
        while (count < 3) {
            System.out.print("\u001B[32mPlease enter the Password: \u001B[0m");
            String enteredPassword = s1.nextLine();
            if (u != null && PasswordUtils.checkPassword(enteredPassword, u.getPrimary_key())) {
                System.out.println("\u001B[34mLogin successful! Welcome back, " + u.getName() + "\u001B[0m");
                return u;
            } else {
                count++;
                int remaining = 3 - count;
                System.out.println("\u001B[31mInvalid password.\u001B[0m");
                if (remaining > 0) {
                    System.out.println("You have " + remaining + " attempt(s) left. Try again!");
                }
            }
        }

        // ── After 3 failures — show options ──
        System.out.println();
        System.out.println("\u001B[31mToo many failed attempts.\u001B[0m");
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║  1. Login with Secondary Key     ║");
        System.out.println("║  2. Exit                         ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.print("Enter your choice: ");
        String input = s1.nextLine().trim();

        if (input.equals("2")) {
            System.out.println("Goodbye!");
            return null;
        } else if (!input.equals("1")) {
            System.out.println("Invalid choice. Goodbye!");
            return null;
        }

        // ── Secondary key — 3 attempts ──
        int subcount = 0;
        while (subcount < 3) {
            System.out.print("Enter the secondary password: ");
            String secondarykey = s1.nextLine().trim();
            if (PasswordUtils.checkPassword(secondarykey, u.getSecondary_key())) {
                System.out.println("\u001B[34mLogged in using secondary key!\u001B[0m");

                // ── Force reset primary password ──
                String newPrimaryKey = "";
                while (newPrimaryKey.trim().isEmpty()) {
                    System.out.print("Please enter a NEW Primary Password: ");
                    newPrimaryKey = s1.nextLine().trim();
                    if (newPrimaryKey.isEmpty()) {
                        System.out.println("Password cannot be empty.");
                    }
                }
                u.setPrimary_key(PasswordUtils.hashPassword(newPrimaryKey));
                UserFileManager.updateUser(u);
                System.out.println("\u001B[32mPassword reset successfully! You are now logged in.\u001B[0m");
                return u;

            } else {
                subcount++;
                int remaining = 3 - subcount;
                System.out.println("\u001B[31mIncorrect secondary password!\u001B[0m");
                if (remaining > 0) {
                    System.out.println("You have " + remaining + " attempt(s) left. Try again!");
                }
            }
        }

        System.out.println("\u001B[31mAttempt limit exceeded. Goodbye!\u001B[0m");
        return null;
    }
}