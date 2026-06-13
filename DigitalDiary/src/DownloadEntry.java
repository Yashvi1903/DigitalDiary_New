
// DownloadEntry.java
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DownloadEntry {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void downloadMenu(Diary diary, User user,EntryRepository repo) {
        Scanner scanner = AppContext.scanner();

        System.out.println(ANSI_CYAN + "\n╔════════════════════════════════════╗");
        System.out.println("║         Download / Export           ║");
        System.out.println("╚════════════════════════════════════╝" + ANSI_RESET);
        System.out.println("1. Export a single entry");
        System.out.println("2. Export ALL entries from this diary");
        System.out.println("3. Back");
        System.out.print("Enter choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        switch (choice) {
            case 1:
                exportSingleEntry(diary, user,repo);
                break;
            case 2:
                exportAllEntries(diary, user,repo);
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void exportSingleEntry(Diary diary, User user,EntryRepository repo) {
        Scanner scanner = AppContext.scanner();
        // UserFileManager.loadEntryIndex(diary, user);

        Map<String, String> titleToPath = repo.getTitleToPath();
        Map<String, Entry> pathToEntry = repo.getPathToEntry();

        if (titleToPath.isEmpty()) {
            System.out.println(ANSI_RED + "No entries found." + ANSI_RESET);
            return;
        }

        // List entries
        System.out.println(ANSI_YELLOW + "\n--- Your Entries ---" + ANSI_RESET);
        Map<Integer, String> indexToTitle = new HashMap<>();
        int idx = 1;
        for (Map.Entry<String, String> e : titleToPath.entrySet()) {
            Entry de = pathToEntry.get(e.getValue());
            String date = (de != null) ? de.getDate() : "Unknown";
            System.out.println(idx + ". [" + date + "] " + e.getKey());
            indexToTitle.put(idx, e.getKey());
            idx++;
        }

        System.out.print("Enter number of entry to export (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (choice == 0)
            return;

        String title = indexToTitle.get(choice);
        if (title == null) {
            System.out.println("Invalid choice.");
            return;
        }

       Entry entry = repo.findByTitle(title).orElse(null);
        if (entry == null) {
            System.out.println("Entry not found.");
            return;
        }

        // Build export file
        String exportDir = "Exports/" + user.getUserName() + "/" + diary.getDiaryname();
        String fileName = sanitizeFileName(entry.getDate()) + "_" + sanitizeFileName(title) + ".txt";
        writeEntryToFile(entry, exportDir, fileName);
    }

    private static void exportAllEntries(Diary diary, User user,EntryRepository repo) {
        

        Map<String, String> titleToPath = repo.getTitleToPath();
        Map<String, Entry> pathToEntry = repo.getPathToEntry();

        if (titleToPath.isEmpty()) {
            System.out.println(ANSI_RED + "No entries found." + ANSI_RESET);
            return;
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String exportFile = "Exports/" + user.getUserName() + "/"
                + sanitizeFileName(diary.getDiaryname()) + "_" + timestamp + ".txt";

        File outFile = new File(exportFile);
        outFile.getParentFile().mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            bw.write("========================================\n");
            bw.write("  Diary: " + diary.getDiaryname() + "\n");
            bw.write("  Owner: " + user.getName() + "\n");
            bw.write("  Exported: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            bw.write("========================================\n\n");

            for (Map.Entry<String, String> e : titleToPath.entrySet()) {
                Entry entry = pathToEntry.get(e.getValue());
                if (entry == null)
                    continue;
                bw.write("----------------------------------------\n");
                bw.write("Date  : " + entry.getDate() + "\n");
                bw.write("Title : " + entry.getTitle() + "\n");
                bw.write("Mood  : " + entry.getMood() + "\n");
                bw.write("----------------------------------------\n");
                bw.write(entry.getContent());
                bw.write("\n\n");
            }

            System.out.println(ANSI_GREEN + "\n✔ All entries exported to: "
                    + outFile.getAbsolutePath() + ANSI_RESET);
        } catch (IOException ex) {
            System.out.println(ANSI_RED + "Export failed: " + ex.getMessage() + ANSI_RESET);
        }
    }

    private static void writeEntryToFile(Entry entry, String dir, String fileName) {
        File folder = new File(dir);
        folder.mkdirs();
        File out = new File(folder, fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
            bw.write("========================================\n");
            bw.write("Date  : " + entry.getDate() + "\n");
            bw.write("Title : " + entry.getTitle() + "\n");
            bw.write("Mood  : " + entry.getMood() + "\n");
            bw.write("Tag   : " + entry.getTag() + "\n");
            bw.write("========================================\n\n");
            bw.write(entry.getContent());
            System.out.println(ANSI_GREEN + "\n✔ Entry exported to: "
                    + out.getAbsolutePath() + ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Export failed: " + e.getMessage() + ANSI_RESET);
        }
    }

    // ── Helper: strip characters unsafe for filenames ────────────────────────
    private static String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|\\s]", "_");
    }
}