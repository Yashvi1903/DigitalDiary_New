import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class DeleteEntry {
    public static void deleteEntry(Diary diary, User user) {
        Scanner scanner = new Scanner(System.in);
        UserFileManager.loadEntryIndex(diary, user);
        Map<String, String> titleToPath = UserFileManager.getTitleToPath();
        Map<String, Entry> pathToEntry = UserFileManager.getPathToEntry();


        if (titleToPath.isEmpty()) {
            System.out.println("\u001B[31mNo entries found in this diary.\u001B[0m");
            return;
        }
        System.out.println("\n\u001B[34m--- Entries in your Diary ---\u001B[0m");
        Map<Integer, String> indexToTitle = new HashMap<>();
        int index = 1;


        for (Map.Entry<String, String> entry : titleToPath.entrySet()) {
            String title = entry.getKey();
            String path = entry.getValue();
            Entry diaryEntry = pathToEntry.get(path);


            String dateTime = (diaryEntry != null && diaryEntry.getDate() != null) ? diaryEntry.getDate()
                    : "Unknown Date";
            System.out.println(index + ". " + dateTime + " - " + title);
            indexToTitle.put(index, title);
            index++;
        }
        System.out.print("\nEnter the number of the entry you want to delete (or 0 to cancel): ");
        int choice = -1;
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
        String filePath = titleToPath.get(selectedTitle);
        // File entryFile = new File(filePath);

        File entryFile = new File("Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + filePath);


        System.out.print("Are you sure you want to delete \"" + selectedTitle + "\"? (yes/no): ");
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Entry deletion canceled.");
            return;
        }


        if (entryFile.exists() && entryFile.delete()) {
            System.out.println("Entry deleted successfully.");


            // Clean up maps
            titleToPath.remove(selectedTitle);
            pathToEntry.remove(filePath);

            UserFileManager.saveEntryIndex(diary, user);
        } else {
            System.out.println("Failed to delete the entry.");
        }


    }
}


