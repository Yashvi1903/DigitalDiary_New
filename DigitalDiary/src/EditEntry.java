import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// aa class diary ni entry edit karva mate use thay che
public class EditEntry {
    // aa color codes console ma color show karva mate use thay che
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";

    // aa method diary ni entry edit karva mate use thay che
    public static void edit(Diary diary, User user) {
        Scanner scanner = new Scanner(System.in);

        // saari entries load karo
        UserFileManager.loadEntryIndex(diary, user);

        // saari available entries show karo
        System.out.println(ANSI_CYAN + "\nAvailable Entries:" + ANSI_RESET);
        int count = 1;
        for (String title : UserFileManager.getTitleToPath().keySet()) {
            System.out.println(count + ". " + title);
            count++;
        }

        // jo koi entry nathi to return karo
        if (count == 1) {
            System.out.println("No entries found to edit.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // user ne entry number pucho
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

        // jo invalid choice hoy to return karo
        if (choice < 1 || choice >= count) {
            System.out.println("Invalid choice.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // selected entry ni details get karo
        String selectedTitle = UserFileManager.getTitleToPath().keySet().toArray(new String[0])[choice - 1];
        String filePath = UserFileManager.getTitleToPath().get(selectedTitle);
        Entry selectedEntry = UserFileManager.getPathToEntry().get(filePath);

        // jo entry nathi to return karo
        if (selectedEntry == null) {
            System.out.println("Entry not found.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // current entry ni details show karo
        System.out.println(ANSI_YELLOW + "\nCurrent Entry:" + ANSI_RESET);
        System.out.println("Date: " + selectedEntry.getDate());
        System.out.println("Title: " + selectedEntry.getTitle());
        System.out.println("Mood: " + selectedEntry.getMood());
        // System.out.println("Tag: " + selectedEntry.getTag());
        System.out.println("Content:");
        System.out.println(selectedEntry.getContent());

        try {
            // temporary file banavo
            File entryFile = new File("Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + filePath);
            File tempFile = new File("temp_entry.txt");

            // content temporary file ma copy karo
            try (BufferedReader reader = new BufferedReader(new FileReader(entryFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                // warning message add karo
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

            // warning message show karo
            System.out.println("\n" + ANSI_RED
                    + "⚠️  IMPORTANT: After editing, you MUST save the file (Ctrl+S) before closing the tab to update your diary!"
                    + ANSI_RESET);
            System.out.println(ANSI_CYAN + "Opening file for editing..." + ANSI_RESET);

            // notepad ma file open karo
            ProcessBuilder pb = new ProcessBuilder("notepad", tempFile.getAbsolutePath());
            Process p = pb.start();
            p.waitFor();

            // edited content read karo
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

            // original file update karo (overwrite mode)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(entryFile, false))) {
                writer.write("Date: " + selectedEntry.getDate() + "\n");
                writer.write("Title: " + selectedEntry.getTitle() + "\n");
                writer.write("Mood <3: " + selectedEntry.getMood() + "\n");
                // writer.write("Tag: " + selectedEntry.getTag() + "\n");
                writer.write("--------------------\n");
                writer.write(newContent);
            }

            // temporary file delete karo
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
            UserFileManager.getPathToEntry().put(filePath, updatedEntry);

            // success message show karo
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
