import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Entry {
    private String date;
    private String title;
    private String content;
    private String Mood;
    private String tag;
    private Diary diary;
    private User user;
    private String filepath;
    private static Map<String, String> titleToPath = new LinkedHashMap<>();
    private static Map<String, Entry> pathToEntry  = new LinkedHashMap<>();

    // ── Private constructor — only Builder can call this ──────────────────
    private Entry(Builder b) {
        this.diary    = b.diary;
        this.user     = b.user;
        this.date     = b.date;
        this.title    = b.title;
        this.content  = b.content;
        this.Mood     = b.mood;
        this.tag      = b.tag;
        this.filepath = b.filepath;
        if (b.writeToIndex) {
            UserFileManager.saveEntryIndex(b.diary, this);
        }
    }

    // ── Builder class ─────────────────────────────────────────────────────
    public static class Builder {
        // Required
        private final String date;
        private final String title;
        private final Diary  diary;
        private final User   user;

        // Optional — safe defaults
        private String  content      = "";
        private String  mood         = "";
        private String  tag          = "";
        private String  filepath     = "";
        private boolean writeToIndex = false;

        public Builder(String date, String title, Diary diary, User user) {
            this.date  = date;
            this.title = title;
            this.diary = diary;
            this.user  = user;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder mood(String mood) {
            this.mood = mood;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder filepath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public Builder writeToIndex(boolean w) {
            this.writeToIndex = w;
            return this;
        }

        public Entry build() {
            if (date  == null || date.isEmpty())  throw new IllegalStateException("Date is required");
            if (title == null || title.isEmpty()) throw new IllegalStateException("Title is required");
            if (diary == null)                    throw new IllegalStateException("Diary is required");
            if (user  == null)                    throw new IllegalStateException("User is required");
            return new Entry(this);
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String getFilePath() { return filepath; }
    public String getDate()     { return date;     }
    public Diary  getDiary()    { return diary;    }
    public User   getUser()     { return user;     }
    public String getTitle()    { return title;    }
    public String getContent()  { return content;  }
    public String getMood()     { return Mood;     }
    public String getTag()      { return tag;      }

    // ── Menu ──────────────────────────────────────────────────────────────
    public static void menu(Diary diary, User user) {
        while (true) {
            try {
                UserFileManager.loadEntryIndex(diary, user);
                App.clearScreen();
                System.out.println("\u001B[95m------- welcome Back to " + diary.getDiaryname() + ", by "
                        + diary.u1.getName() + " -------- \u001B[0m");
                System.out.println();
                System.out.println("1. Add Entry");
                System.out.println("2. View Entry");
                System.out.println("3. Back to my diaries");
                System.out.println("4. Delete Entry");
                System.out.println("5. Edit Entry");
                System.out.println("6. Download Entry");
                System.out.println("7. Back to Main Menu");
                System.out.print("Enter your choice: ");

                Scanner s1   = new Scanner(System.in);
                String input = s1.nextLine().trim();
                int choice   = 0;

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    System.out.println("Press Enter to continue...");
                    s1.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1:
                        App.clearScreen();
                        AddNewEntry.AddEntry(diary, user);
                        break;
                    case 2:
                        App.clearScreen();
                        ViewPastEntry.showEntry(diary, user);
                        break;
                    case 3:
                        App.clearScreen();
                        Diary.Diarymenu(user);
                        System.out.println("Going back to diary menu...");
                        return;
                    case 4:
                        App.clearScreen();
                        DeleteEntry.deleteEntry(diary, user);
                        break;
                    case 5:
                        App.clearScreen();
                        EditEntry.edit(diary, user);
                        UserFileManager.loadEntryIndex(diary, user);
                        break;
                    case 6:
                        App.clearScreen();
                        DownloadEntry.downloadMenu(diary, user);
                        break;
                    case 7:
                        App.clearScreen();
                        System.out.println("Going back to main menu...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a valid choice...");
                        System.out.println("Press Enter to continue...");
                        s1.nextLine();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Press Enter to continue to the main menu...");
                new Scanner(System.in).nextLine();
                Diary.Diarymenu(user);
                return;
            }
        }
    }
}