
// MoodTracker.java
import java.util.Scanner;

public class MoodTracker {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private static final String[] MOOD_LABELS = {
            ":) Happy",
            ":( Sad",
            "^-^ Angry",
            "^.^ Anxious",
            "+.+ Calm",
            "`.` Tired",
            ":) Excited",
            "*-* Confused",
            "Other"
    };

    public static String selectMood() {
        Scanner scanner = AppContext.scanner();

        System.out.println(ANSI_CYAN + "\n╔════════════════════════════════════╗");
        System.out.println("║         How are you feeling?       ║");
        System.out.println("╚════════════════════════════════════╝" + ANSI_RESET);

        for (int i = 0; i < MOOD_LABELS.length; i++) {
            System.out.println("  " + (i + 1) + ". " + MOOD_LABELS[i]);
        }

        System.out.println();

        while (true) {
            System.out.print(ANSI_YELLOW + "Enter mood number (1-" + MOOD_LABELS.length + "): " + ANSI_RESET);
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice < 1 || choice > MOOD_LABELS.length) {
                System.out.println("Please choose between 1 and " + MOOD_LABELS.length + ".");
                continue;
            }

            if (choice == MOOD_LABELS.length) {
                System.out.print("Describe your mood: ");
                String customMood = scanner.nextLine().trim();
                if (customMood.isEmpty()) {
                    System.out.println("Mood cannot be empty. Try again.");
                    continue;
                }
                System.out.println(ANSI_GREEN + "Mood set to: " + customMood + ANSI_RESET);
                return customMood;
            }

            String selected = MOOD_LABELS[choice - 1];
            System.out.println(ANSI_GREEN + "Mood set to: " + selected + ANSI_RESET);
            return selected;
        }
    }
}