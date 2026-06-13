import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DeleteEntry {

    public static void deleteEntry(Diary diary, User user, EntryRepository repo) {
        Scanner scanner = AppContext.scanner();

        
        System.out.println("\u001B[34m\n--- Delete Options ---\u001B[0m");
        System.out.println("1. Delete a single entry");
        System.out.println("2. Delete entire diary  (\u001B[31m" + diary.getDiaryname() + "\u001B[0m)");
        System.out.println("3. Cancel");
        System.out.print("Enter choice: ");

        int option;
        try {
            option = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        switch (option) {
            case 1:
                deleteSingleEntry(diary, user, repo, scanner);
                break;
            case 2:
                deleteWholeDiary(diary, user, scanner);
                break;
            case 3:
                System.out.println("Cancelled.");
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

   
    private static void deleteSingleEntry(Diary diary, User user, EntryRepository repo, Scanner scanner) {
        Map<String, String> titleToPath = repo.getTitleToPath();
        Map<String, Entry>  pathToEntry = repo.getPathToEntry();

        if (titleToPath.isEmpty()) {
            System.out.println("\u001B[31mNo entries found in this diary.\u001B[0m");
            return;
        }

        System.out.println("\n\u001B[34m--- Entries in your Diary ---\u001B[0m");
        Map<Integer, String> indexToTitle = new HashMap<>();
        int index = 1;

        for (Map.Entry<String, String> entry : titleToPath.entrySet()) {
            String title     = entry.getKey();
            Entry diaryEntry = pathToEntry.get(entry.getValue());
            String dateTime  = (diaryEntry != null && diaryEntry.getDate() != null)
                                ? diaryEntry.getDate() : "Unknown Date";
            System.out.println(index + ". " + dateTime + " - " + title);
            indexToTitle.put(index, title);
            index++;
        }

        System.out.print("\nEnter the number of the entry to delete (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        if (choice == 0) {
            System.out.println("Deletion canceled.");
            return;
        }

        if (!indexToTitle.containsKey(choice)) {
            System.out.println("Invalid choice. No such entry.");
            return;
        }

        String selectedTitle = indexToTitle.get(choice);
        System.out.print("Are you sure you want to delete \"" + selectedTitle + "\"? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Entry deletion canceled.");
            return;
        }

        if (repo.delete(selectedTitle, diary, user)) {
            System.out.println("Entry deleted successfully.");
        } else {
            System.out.println("Failed to delete the entry.");
        }
    }

    private static void deleteWholeDiary(Diary diary, User user, Scanner scanner) {
        System.out.println("\u001B[31m\n⚠ This will permanently delete the diary \""
                + diary.getDiaryname() + "\" and ALL its entries!\u001B[0m");
        System.out.print("Type the diary name to confirm: ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equals(diary.getDiaryname())) {
            System.out.println("Name didn't match. Deletion cancelled.");
            return;
        }

        
        File diaryFolder = new File("Users/" + user.getUserID() + "/" + diary.getDiaryID());
        deleteFolder(diaryFolder);

        
        removeDiaryFromIndex(diary, user);

        UserFileManager.getDiaryIndex().remove(diary.getDiaryname());
        UserFileManager.getDiaryObjMap().remove(diary.getDiaryname());

        System.out.println("\u001B[32mDiary \"" + diary.getDiaryname() + "\" deleted successfully.\u001B[0m");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();

        
        Diary.showMyDiariesMenu(user);
    }

    
    private static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) deleteFolder(f);
                    else f.delete();
                }
            }
            folder.delete();
        }
    }

   
    private static void removeDiaryFromIndex(Diary diary, User user) {
        File indexFile = new File("Users/" + user.getUserID() + "/diary_index.txt");
        File tempFile  = new File("Users/" + user.getUserID() + "/temp_diary_index.txt");

        if (!indexFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(diary.getDiaryname() + "=")) continue; // skip deleted diary
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating diary index: " + e.getMessage());
            return;
        }

        if (indexFile.delete()) tempFile.renameTo(indexFile);
    }
}