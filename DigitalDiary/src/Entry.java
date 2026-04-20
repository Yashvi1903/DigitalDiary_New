import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Entry {
    private String date;
    private String title;
    private String content;
    private String Mood;
    private String moodRate;
    private String tag;
    private Diary diary;
    private User user;
    private String filepath;
    private static Map<String, String> titleToPath = new LinkedHashMap<>();
    private static Map<String, Entry> pathToEntry = new LinkedHashMap<>();

    public Entry(String formattedDate, String title, String content, String mood, String tag, Diary diary, User user,
            String filepath, boolean writeToIndex) {
        this.diary = diary;
        this.user = user;
        this.date = formattedDate;
        this.title = title;
        this.content = content;
        this.Mood = mood;
        this.tag = tag;
        this.filepath = filepath;
        if (writeToIndex) {
            UserFileManager.saveEntryIndex(diary, this);
        }
    }

    public String getFilePath() {
        return filepath;
    }

    public String getDate() {
        return date;
    }

    public Diary getDiary() {
        return diary;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMood() {
        return Mood;
    }

    public String getTag() {
        return tag;
    }

    public static void menu(Diary diary, User user) {
        while (true) {
            try {
                UserFileManager.loadEntryIndex(diary, user);
                App.clearScreen();
                System.out.println("\u001B[95m------- welcome Back to " + diary.getDiaryname() + ", by "
                        + diary.u1.getName() + " -------- \u001B[0m");
                System.out.println();
                System.out.println("1.Add Entry");
                System.out.println("2.View Entry");
                System.out.println("3.Back to my diaries");
                System.out.println("4.Delete Entry");
                System.out.println("5.Edit Entry");
                System.out.println("6.Back to Main Menu");
                System.out.print("Enter your choice: ");

                Scanner s1 = new Scanner(System.in);
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
                        System.out.println("Going back to main menu...");
                        return; // Return to diary menu
                    case 4:
                        App.clearScreen();
                        // DeleteEntry.deleteEntry(diary, user);
                        break;
                    case 5:
                        App.clearScreen();
                        // EditEntry.edit(diary, user);
                        //Reload entries after editing
                        UserFileManager.loadEntryIndex(diary, user);
                        break;
                    case 6:
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