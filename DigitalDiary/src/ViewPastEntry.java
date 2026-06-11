
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;

public class ViewPastEntry {

    public static Entry showEntry(Diary diary, User user) {
        try {
            // Load entry index
            UserFileManager.loadEntryIndex(diary, user);
            if (UserFileManager.getTitleToPath().isEmpty()) {
                System.out.println("\u001B[31m No past entries found. \u001B[0m");
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();
                return null;
            }

            System.out.println("\u001B[34m╔══════════════════════════════════════╗");
            System.out.println("║       Your Past Diary Entries        ║");
            System.out.println("╚══════════════════════════════════════╝\u001B[0m");

            int index = 1;
            Map<Integer, String> indexToTitle = new java.util.HashMap<>();

            for (Map.Entry<String, String> entry : UserFileManager.getTitleToPath().entrySet()) {
                String filepath = entry.getValue();
                Entry diaryentry = UserFileManager.getPathToEntry().get(filepath);

                String dateTime = (diaryentry != null && diaryentry.getDate() != null) ? diaryentry.getDate()
                        : "UnKnown Date";
                String title = entry.getKey();

                System.out.println(index + ". " + dateTime + " - " + title);
                indexToTitle.put(index, title);
                index++;
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("\nEnter the number of the entry you want to read (or 0 to go back): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return null;
            }

            String selectedTitle = indexToTitle.get(choice);
            if (selectedTitle == null) {
                System.out.println("Invalid entry number.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return null;
            }

            String filePath = UserFileManager.getTitleToPath().get(selectedTitle);
            Entry entry = UserFileManager.getPathToEntry().get(filePath);

            if (entry == null) {
                System.out.println("Failed to retrieve entry content.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return null;
            }

            try {
                // Display the entry content
                System.out.println("\n\u001B[36m╔═══════════════════════════════════════════════╗");
                System.out.println("║              Entry Details                    ║");
                System.out.println("╚═══════════════════════════════════════════════╝\u001B[0m");

                System.out.println("\u001B[33mDate: \u001B[0m" + entry.getDate());
                System.out.println("\u001B[33mTitle: \u001B[0m" + entry.getTitle());
                System.out.println("\u001B[33mMood: \u001B[0m" + entry.getMood());
                // System.out.println("\u001B[33mTag: \u001B[0m" + entry.getTag());
                System.out.println("\n\u001B[33m------------- Content -------------\u001B[0m");
                System.out.println(entry.getContent());
            } catch (Exception e) {
                System.out.println("Error displaying entry: " + e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();

            return entry;

        } catch (Exception e) {
            System.out.println("Error viewing entries: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            new Scanner(System.in).nextLine();
            return null;
        }
    }

}
