import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        try {
            UserFileManager.loadFromIndexFile();
            while (true) {
                System.out.println();
                System.out.println("\u001B[95m" + "-------WELCOME TO MY DIARY---------" + "\u001B[0m");
                System.out.println();
                System.out.println("1.Existing User");
                System.out.println("2.New User ");
                System.out.println("3.EXIT Program");
                System.out.print("Please enter your choice: ");

                Scanner scanner = AppContext.scanner();
                int choice = 0;
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    System.out.println("No input detected. Please enter a number.");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    continue;
                }

                try {
                    choice = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input. Please enter a number.");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1:
                        try {
                            clearScreen();
                            User user = loginService.login();
                            if (user != null) {
                                clearScreen();
                                Diary d1 = new Diary(user, null, null, false);
                                Diary.Diarymenu(user);
                            }
                        } catch (Exception e) {
                            System.out.println("Error logging in: " + e.getMessage());
                            System.out.println("Press Enter to continue...");
                            scanner.nextLine();
                        }
                        break;
                    case 2:
                        try {
                            clearScreen();
                            User registereUser = RegisterNewUser.Register();
                            if (registereUser != null) {
                                clearScreen();
                                System.out.println("Registration successful! Welcome, " + "\u001B[31m"
                                        + registereUser.getName() + "\u001b[0m");
                                Diary d1 = new Diary(registereUser, null, null, false);
                                Diary.Diarymenu(registereUser);
                            }
                        } catch (Exception e) {
                            System.out.println("Error registering: " + e.getMessage());
                            System.out.println("Press Enter to continue...");
                            scanner.nextLine();
                        }
                        break;
                    case 3:
                        System.out.println("Thank you for using diary! Program is terminating...");
                        System.exit(0);
                        return;
                    default:
                        System.out.println("Please choose a valid choice!...");
                        System.out.println("Press Enter to continue...");
                        scanner.nextLine();
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            System.out.println("Please restart the application.");
        }
    }

    // for clear screen
    public static void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }
}
