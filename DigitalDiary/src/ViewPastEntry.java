import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ViewPastEntry {

    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";

    public static Entry showEntry(Diary diary, User user, EntryRepository repo) {
        try {
            if (repo.getTitleToPath().isEmpty()) {
                System.out.println(ANSI_RED + " No past entries found. " + ANSI_RESET);
                System.out.println("\nPress Enter to continue...");
                AppContext.scanner().nextLine();
                return null;
            }

            Scanner scanner = AppContext.scanner();

            // ── Ask: browse list OR search by prefix ─────────────────────
            System.out.println(ANSI_CYAN + "\n╔══════════════════════════════════════╗");
            System.out.println("║       Your Past Diary Entries        ║");
            System.out.println("╚══════════════════════════════════════╝" + ANSI_RESET);
            System.out.println("1. Browse all entries");
            System.out.println("2. Search by title prefix  \u001B[32m← Trie O(L)\u001B[0m");
            System.out.print("Enter choice: ");

            String modeInput = scanner.nextLine().trim();

            if (modeInput.equals("2")) {
                return searchAndView(repo, scanner);
            }

            // ── Default: browse all ───────────────────────────────────────
            return browseAndView(repo, scanner);

        } catch (Exception e) {
            System.out.println("Error viewing entries: " + e.getMessage());
            System.out.println("\nPress Enter to continue...");
            AppContext.scanner().nextLine();
            return null;
        }
    }

    // ── Browse mode: numbered list, pick by number ────────────────────────
    private static Entry browseAndView(EntryRepository repo, Scanner scanner) {
        int index = 1;
        Map<Integer, String> indexToTitle = new java.util.HashMap<>();

        for (Map.Entry<String, String> entry : repo.getTitleToPath().entrySet()) {
            String filepath    = entry.getValue();
            Entry diaryEntry   = repo.getPathToEntry().get(filepath);
            String dateTime    = (diaryEntry != null && diaryEntry.getDate() != null)
                                    ? diaryEntry.getDate() : "Unknown Date";
            System.out.println(index + ". " + dateTime + " - " + entry.getKey());
            indexToTitle.put(index, entry.getKey());
            index++;
        }

        System.out.print("\nEnter the number of the entry you want to read (0 to go back): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return null;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
            return null;
        }

        String selectedTitle = indexToTitle.get(choice);
        if (selectedTitle == null) {
            System.out.println("Invalid entry number.");
            scanner.nextLine();
            return null;
        }

        return displayEntry(repo, selectedTitle, scanner);
    }

    // ── Search mode: Trie prefix search, then pick from suggestions ───────
    private static Entry searchAndView(EntryRepository repo, Scanner scanner) {
        System.out.print(ANSI_YELLOW + "Type a title prefix to search: " + ANSI_RESET);
        String prefix = scanner.nextLine().trim();

        if (prefix.isEmpty()) {
            System.out.println("Prefix cannot be empty.");
            return null;
        }

        // Trie O(L) lookup — this is the key DSA call
        List<String> matches = repo.searchByPrefix(prefix);

        if (matches.isEmpty()) {
            System.out.println(ANSI_RED + "No entries found matching \"" + prefix + "\"." + ANSI_RESET);
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return null;
        }

        System.out.println(ANSI_GREEN + "\nMatching entries:" + ANSI_RESET);
        for (int i = 0; i < matches.size(); i++) {
            String title    = matches.get(i);
            Entry e         = repo.findByTitle(title).orElse(null);
            String date     = (e != null) ? e.getDate() : "Unknown Date";
            System.out.println((i + 1) + ". [" + date + "]  " + title);
        }

        System.out.print("\nEnter the number to open (0 to go back): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return null;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }

        if (choice < 1 || choice > matches.size()) {
            System.out.println("Invalid choice.");
            return null;
        }

        return displayEntry(repo, matches.get(choice - 1), scanner);
    }

    // ── Shared: render one entry to screen ────────────────────────────────
    private static Entry displayEntry(EntryRepository repo, String title, Scanner scanner) {
        Entry entry = repo.findByTitle(title).orElse(null);

        if (entry == null) {
            System.out.println("Failed to retrieve entry content.");
            scanner.nextLine();
            return null;
        }

        System.out.println("\n\u001B[36m╔═══════════════════════════════════════════════╗");
        System.out.println("║              Entry Details                    ║");
        System.out.println("╚═══════════════════════════════════════════════╝\u001B[0m");

        System.out.println(ANSI_YELLOW + "Date:  " + ANSI_RESET + entry.getDate());
        System.out.println(ANSI_YELLOW + "Title: " + ANSI_RESET + entry.getTitle());
        System.out.println(ANSI_YELLOW + "Mood:  " + ANSI_RESET + entry.getMood());
        System.out.println("\n" + ANSI_YELLOW + "------------- Content -------------" + ANSI_RESET);
        System.out.println(entry.getContent());

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        return entry;
    }
}