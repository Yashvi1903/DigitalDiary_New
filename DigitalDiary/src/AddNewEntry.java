import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;




public class AddNewEntry {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static Entry AddEntry(Diary diary, User user) {

        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String dateFolder = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String fileTime = now.format(DateTimeFormatter.ofPattern("HH-mm-ss")) + ".txt";
        String filePath = dateFolder + "/" + fileTime;

        System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════╗");
        System.out.println("║            Hey! How was your day?          ║");
        System.out.println("╚════════════════════════════════════════════╝" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "Date: " + dateFolder + "     Time: " + time + ANSI_RESET);
        System.out.println();

        Scanner s1 = new Scanner(System.in);
        String title;
        System.out.print("Give Title: ");
        title = s1.nextLine().trim();
        System.out.println("'''''''''''''''''''''''''''''''''''''''''''''");
        System.out.println("--> Start writing your thoughts... Type '-1' to finish:" + ANSI_RESET);
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String line = s1.nextLine();
            if (line.trim().equals("-1")) {
                break;
            }
            contentBuilder.append(line).append(System.lineSeparator());
        }
        String content = contentBuilder.toString();
        System.out.println("\u001b[1;34m (^.^) Page added to your story!..Keep going!..\u001B[0m");
        System.out.println("---------------------------------------------");
        System.out.println();
        System.out.print("Now time to enter your MOOD <3: ");
        String mood = s1.nextLine().trim();
        System.out.print("Give any tag(special event) to your diary: ");
        String tag = s1.nextLine().trim();


        File folder = new File("Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + dateFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File diaryFile = new File(folder, fileTime);
        try {
            if (diaryFile.createNewFile()) {
                System.out.println("File created successfully!...");
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
        }
        Entry neweEntry = new Entry(formattedDate, title, content, mood, tag, diary, user,filePath,true);
        UserFileManager.saveEntryToFile(neweEntry);
        return neweEntry;
    }
}
