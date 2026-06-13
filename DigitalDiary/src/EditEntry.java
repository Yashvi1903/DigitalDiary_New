import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EditEntry {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void edit(Diary diary, User user, EntryRepository repo) {
        Scanner scanner = AppContext.scanner();

        // UserFileManager.loadEntryIndex(diary, user);

        System.out.println(ANSI_CYAN + "\nAvailable Entries:" + ANSI_RESET);
        int count = 1;
        for (String title : repo.getTitleToPath().keySet()) {
            System.out.println(count + ". " + title);
            count++;
        }

        if (count == 1) {
            System.out.println("No entries found to edit.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.print("\nEnter the number of entry you want to edit: ");
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (choice < 1 || choice >= count) {
            System.out.println("Invalid choice.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        String selectedTitle = repo.getTitleToPath().keySet().toArray(new String[0])[choice - 1];
        String filePath = repo.getTitleToPath().get(selectedTitle);
        Entry selectedEntry = repo.getPathToEntry().get(filePath);

        if (selectedEntry == null) {
            System.out.println("Entry not found.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println(ANSI_YELLOW + "\nCurrent Entry:" + ANSI_RESET);
        System.out.println("Date: " + selectedEntry.getDate());
        System.out.println("Title: " + selectedEntry.getTitle());
        System.out.println("Mood: " + selectedEntry.getMood());
        // System.out.println("Tag: " + selectedEntry.getTag());
        System.out.println("Content:");
        System.out.println(selectedEntry.getContent());

        try {

            File entryFile = new File("Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + filePath);
            File tempFile = new File("temp_entry.txt");

            try (BufferedReader reader = new BufferedReader(new FileReader(entryFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                writer.write(
                        "⚠️  IMPORTANT: After editing, you MUST save the file (Ctrl+S) before closing the tab to update your diary!\n\n");
                writer.write("--------------------\n");

                String line;
                boolean contentStarted = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("-----")) {
                        contentStarted = true;
                        writer.write(line + "\n");
                        continue;
                    }
                    if (contentStarted) {
                        writer.write(line + "\n");
                    } else {
                        writer.write(line + "\n");
                    }
                }
            }

            System.out.println("\n" + ANSI_RED
                    + "⚠️  IMPORTANT: After editing, you MUST save the file (Ctrl+S) before closing the tab to update your diary!"
                    + ANSI_RESET);
            System.out.println(ANSI_CYAN + "Opening file for editing..." + ANSI_RESET);

            ProcessBuilder pb = new ProcessBuilder("notepad", tempFile.getAbsolutePath());
            Process p = pb.start();
            p.waitFor();

            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String line;
                boolean contentStarted = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("-----")) {
                        contentStarted = true;
                        continue;
                    }
                    if (contentStarted) {
                        contentBuilder.append(line).append("\n");
                    }
                }
            }

            String newContent = contentBuilder.toString();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(entryFile, false))) {
                writer.write("Date: " + selectedEntry.getDate() + "\n");
                writer.write("Title: " + selectedEntry.getTitle() + "\n");
                writer.write("Mood <3: " + selectedEntry.getMood() + "\n");
                // writer.write("Tag: " + selectedEntry.getTag() + "\n");
                writer.write("--------------------\n");
                writer.write(newContent);
            }

            tempFile.delete();

            // memory ma entry update karo
            // Entry updatedEntry = new Entry(
            // selectedEntry.getDate(),
            // selectedEntry.getTitle(),
            // newContent,
            // selectedEntry.getMood(),
            // selectedEntry.getTag(),
            // diary,
            // user,
            // filePath,
            // false
            // );

            Entry updatedEntry = new Entry.Builder(
                    selectedEntry.getDate(), selectedEntry.getTitle(), diary, user)
                    .content(newContent)
                    .mood(selectedEntry.getMood())
                    .filepath(filePath)
                    .build();

            // memory maps update karo
            repo.getPathToEntry().put(filePath, updatedEntry);

            System.out.println("\u001b[1;34mEntry updated successfully!\u001B[0m");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Error editing content: " + e.getMessage());
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
    }
}
