import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Diary {

    private String diaryname;
    private String diaryID;
    protected static Map<String, String> DiaryIndex = new HashMap<>();
    protected static Map<String, Diary> DiaryObjMap = new HashMap<>();
    User u1;

    public static String getDiaryIndexPath(User u) {
        return "Users/" + u.getUserID() + "/diary_index.txt";
    }

    public String getDiaryname() {
        return diaryname;
    }

    public String getDiaryID() {
        return diaryID;
    }

    Diary(User user, String diaryname, String diaryID, boolean writeToIndex) {
        this.u1 = user;
        this.diaryname = diaryname;
        this.diaryID = diaryID;
        DiaryObjMap.put(diaryname, this);
        if (writeToIndex) {
            if (!DiaryIndex.containsKey(diaryname)) {
                DiaryIndex.put(diaryname, diaryID);
                UserFileManager.saveDiaryIndex(user, diaryname, diaryID);
            } else {
                System.out.println("Diary already exist in the index file");
            }
        }

    }

    public static void Diarymenu(User u1) {
        while (true) {
            UserFileManager.loadFromIndexFile();
            System.out.println();
            System.out.println("\u001B[95m-------welcome Back to " + u1.getDiaryName() + ", by " + u1.getName()
                    + "--------\u001B[0m");
            Scanner s1 = AppContext.scanner();
            System.out.println();
            System.out.println("1.change profile");
            System.out.println("2.My Diaries");
            System.out.println("3.Back");
            System.out.print("Enter your choice: ");
            String input = s1.nextLine().trim(); // Safely read as string

            int choice = 0;
            try {
                choice = Integer.parseInt(input); // Parse safely
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                System.out.println("Press Enter to continue...");
                s1.nextLine();
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        ChangeProfile.changeYourProfile(u1);
                        System.out.println("Your profile has been changed successfully!..");
                        continue;
                    case 2:
                        Diary selectedDiary = showMyDiariesMenu(u1);
                        if (selectedDiary != null) {
                            Entry.menu(selectedDiary, u1);
                        }
                        continue;
                    case 3:
                        // App.main(null);
                        return;
                    default:
                        System.out.println("Please enter a valid choice...");
                        System.out.println("Press Enter to continue...");
                        s1.nextLine();
                        continue;
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                s1.nextLine();
            }
        }
    }

    public static Diary showMyDiariesMenu(User u1) {
        Scanner s1 = AppContext.scanner();
        UserFileManager.loadDiaryIndexFromFile(u1.getUserID(), u1);
        if (UserFileManager.getDiaryIndex().isEmpty()) {
            System.out.println();
            System.out.println("\u001B[33m ******** No diaries found...Let's create one! ********\u001B[0m");
            // System.out.print("Which diary you want to make(personal Diary,Work
            // Diary,special Diary..etc): ");
            // String diaryname = s1.nextLine().trim();
            System.out.println("What type of diary do you want to create?");
            System.out.println("╔══════════════════════════════════╗");
            System.out.println("║  1. Personal Diary               ║");
            System.out.println("║  2. Work Diary                   ║");
            System.out.println("║  3. Travel Diary                 ║");
            System.out.println("║  4. Dream Diary                  ║");
            System.out.println("║  5. Custom Name                  ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            String diaryname;
            try {
                int typeChoice = Integer.parseInt(s1.nextLine().trim());
                switch (typeChoice) {
                    case 1:
                        diaryname = "Personal Diary";
                        break;
                    case 2:
                        diaryname = "Work Diary";
                        break;
                    case 3:
                        diaryname = "Travel Diary";
                        break;
                    case 4:
                        diaryname = "Dream Diary";
                        break;
                    case 5:
                        System.out.print("Enter your custom diary name: ");
                        diaryname = s1.nextLine().trim();
                        break;
                    default:
                        System.out.println("Invalid choice, setting as Personal Diary.");
                        diaryname = "Personal Diary";
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, setting as Personal Diary.");
                diaryname = "Personal Diary";
            }
            String diaryid = UUID.randomUUID().toString();
            File folder = new File("Users/" + u1.getUserID() + "/" + diaryid);
            if (!folder.exists()) {
                // creates the folder and all necessary parent directories
                folder.mkdirs();
            }
            Diary d1 = new Diary(u1, diaryname, diaryid, true);
            return d1;
        }
        while (true) {
            System.out.println();
            System.out.println("****** \u001b[1;31mYour Diaries \u001B[0m *********");

            // Create a map to store the index-to-diaryName mapping
            Map<Integer, String> indexToDiary = new HashMap<>();
            int index = 1;

            // Display diaries with numbers
            for (Map.Entry<String, String> entry : UserFileManager.getDiaryIndex().entrySet()) {
                String diaryName = entry.getKey();
                System.out.println(index + ". " + diaryName);
                indexToDiary.put(index, diaryName);
                index++;
            }

            System.out.println();
            System.out.println("Enter the number of the diary you want to open, or type 'new' to create one: ");
            String input = s1.nextLine().trim();

            if (input.equalsIgnoreCase("new")) {
                while (true) {
                    System.out.println("Enter new diary name: ");
                    String newDiaryName = s1.nextLine().trim();

                    boolean exist = false;
                    String MatchedName = "";
                    for (String existingName : UserFileManager.getDiaryIndex().keySet()) {
                        if (existingName.equalsIgnoreCase(newDiaryName)) {
                            exist = true;
                            MatchedName = existingName;
                            break;
                        }
                    }
                    if (exist) {
                        System.out.println("A diary with this name already exists. ");
                        System.out.println();
                        System.out.println("Do you want to open it? (yes/no): ");
                        String choice = s1.nextLine().trim();

                        if (choice.equalsIgnoreCase("yes")) {
                            String existingDiaryID = UserFileManager.getDiaryIndex().get(newDiaryName);
                            Diary existingDiary = UserFileManager.getDiaryObjMap()
                                    .getOrDefault(newDiaryName, new Diary(u1, newDiaryName, existingDiaryID, false));
                            return existingDiary;
                        } else {
                            System.out.println("Please enter a different diary name. ");
                            continue;
                        }
                    }
                    String newDiaryID = UUID.randomUUID().toString();
                    File folder = new File("Users/" + u1.getUserID() + "/" + newDiaryID);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    return new Diary(u1, newDiaryName, newDiaryID, true);
                }
            } else {
                // Try to parse input as a number
                try {
                    int choice = Integer.parseInt(input);
                    String selectedDiaryName = indexToDiary.get(choice);

                    if (selectedDiaryName != null) {
                        String diaryID = UserFileManager.getDiaryIndex().get(selectedDiaryName);
                        System.out.println("Diary opened: " + selectedDiaryName);
                        Diary existingDiary = UserFileManager.getDiaryObjMap().getOrDefault(selectedDiaryName,
                                new Diary(u1, selectedDiaryName, diaryID, false));
                        return existingDiary;
                    } else {
                        System.out.println("Invalid diary number. Try again.");
                    }
                } catch (NumberFormatException e) {
                    // If not a number, try to match by name (for backward compatibility)
                    boolean found = false;
                    for (Map.Entry<String, String> entry : UserFileManager.getDiaryIndex().entrySet()) {
                        String diaryName = entry.getKey();
                        String diaryID = entry.getValue();

                        if (diaryName.equalsIgnoreCase(input)) {
                            System.out.println("Diary opened: " + diaryName);
                            Diary existingDiary = UserFileManager.getDiaryObjMap().getOrDefault(diaryName,
                                    new Diary(u1, diaryName, diaryID, false));
                            return existingDiary;
                        }
                    }
                    System.out.println("Diary not found. Try again.");
                }
            }
        }
    }
}