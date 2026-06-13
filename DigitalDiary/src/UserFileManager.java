import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserFileManager {
    private static Map<String, String> UserIndex = new HashMap<>();
    private static Map<String, User> UserObjMap = new HashMap<>();
    private static final String Index_File = "Users/user_index.txt";
    private static Map<String, String> DiaryIndex = new HashMap<>();
    private static Map<String, Diary> DiaryObjMap = new HashMap<>();
    // private static Map<String, String> titleToPath = new LinkedHashMap<>();
    // private static Map<String, Entry> pathToEntry = new LinkedHashMap<>();

    public static void saveToFile(User u1) {
        try (FileWriter writer = new FileWriter(new File(u1.getUserFolder(), "P_" + u1.getUserName() + ".txt"))) {
            writer.write("UserID: " + u1.getUserID() + "\n");
            writer.write("Username: " + u1.getUserName() + "\n");
            writer.write("Full Name: " + u1.getName() + "\n");
            writer.write("Age: " + u1.getAge() + "\n");
            writer.write("Gender: " + u1.getGender() + "\n");
            writer.write("Primary Key: " + u1.getPrimary_key() + "\n");
            writer.write("Secondary Key: " + u1.getSecondary_key() + "\n");
            writer.write("Diary name: " + u1.getDiaryName() + "\n");

        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    public static void saveIndexToFile(String UserName, String UserID) {
        if (UserIndex.containsKey(UserName))
            return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Index_File, true))) {
            writer.write(UserName + "=" + UserID);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving index file: " + e.getMessage());
        }
    }

    public static void saveDiaryIndex(User user, String diaryname, String DiaryId) {
        String diaryIndexPath = Diary.getDiaryIndexPath(user);
        if (DiaryIndex.containsKey(diaryname))
            return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(diaryIndexPath, true))) {
            writer.write(diaryname + "=" + DiaryId);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving index file: " + e.getMessage());
        }
    }

    // public static void saveEntryIndex(Diary diary, Entry entry) {
    //     String userID = entry.getUser().getUserID();
    //     String diaryID = diary.getDiaryID();

    //     String entryID = entry.getDate();
    //     String filepath = entry.getFilePath();

    //     File indexFile = new File("Users/" + userID + "/" + diaryID + "/entry_index.txt");

    //     try (FileWriter writer = new FileWriter(indexFile, true)) {
    //         writer.write(entryID + "=" + filepath + "=" + entry.getTitle() + "\n");
    //     } catch (IOException e) {
    //         System.out.println("Failed to save entry index. ");
    //     }
    // }

    public static void updateUser(User user) {
        File userProfile = new File(user.getUserFolder(), "P_" + user.getUserName() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userProfile, false))) {
            writer.write("UserID: " + user.getUserID() + "\n");
            writer.write("Username: " + user.getUserName() + "\n");
            writer.write("Full Name: " + user.getName() + "\n");
            writer.write("Age: " + user.getAge() + "\n");
            writer.write("Gender: " + user.getGender() + "\n");
            writer.write("Primary Key: " + user.getPrimary_key() + "\n");
            writer.write("Secondary Key: " + user.getSecondary_key() + "\n");
            writer.write("Diary name: " + user.getDiaryName() + "\n");

            // Also update the in-memory map
            UserObjMap.put(user.getUserName(), user);
            System.out.println("\u001B[32mUser profile updated successfully!\u001B[0m");
        } catch (IOException e) {
            System.out.println("\u001B[31mError updating user file: " + e.getMessage() + "\u001B[0m");
        }
    }

    public static void updateUserIndex(String oldusername, String newUsername, String userID) {
        File indexFile = new File(Index_File);
        File tempFile = new File("Users/temp_index.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(oldusername + "=")) {
                    writer.write(newUsername + "=" + userID + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating index file: " + e.getMessage());
            return;
        }

        if (indexFile.delete()) {
            tempFile.renameTo(indexFile);
        }

        UserIndex.remove(oldusername);
        UserIndex.put(newUsername, userID);

        User user = UserObjMap.remove(oldusername);
        if (user != null) {
            File oldFolder = new File("Users", oldusername);
            File newFolder = new File("Users", newUsername);
            if (oldFolder.exists()) {
                if (oldFolder.renameTo(newFolder)) {
                    user.setUserFolder(newFolder);
                } else {
                    System.out.println("Failed to renaming user Folder. ");
                    return;
                }
            }
            File oldprofile = new File(user.getUserFolder(), "P_" + oldusername + ".txt");
            File newprofile = new File(user.getUserFolder(), "P_" + newUsername + ".txt");
            if (oldprofile.exists()) {
                oldprofile.renameTo(newprofile);
            }
            user.setUserName(newUsername);
            UserObjMap.put(newUsername, user);
        }
        System.out.println("\u001B[32mUsername updated successfully!\u001B[0m");
    }

    // Rewrites the entire entry_index.txt from the current in-memory maps
    // public static void saveEntryIndex(Diary diary, User user) {
    //     String path = "Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/entry_index.txt";
    //     File indexFile = new File(path);

    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile, false))) { // false = overwrite
    //         for (Map.Entry<String, String> e : titleToPath.entrySet()) {
    //             String title = e.getKey();
    //             String filePath = e.getValue();
    //             Entry entry = pathToEntry.get(filePath);
    //             if (entry != null) {
    //                 writer.write(entry.getDate() + "=" + filePath + "=" + title);
    //                 writer.newLine();
    //             }
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Failed to update entry index: " + e.getMessage());
    //     }
    // }

    // public static void loadEntryIndex(Diary diary, User user) {
    //     titleToPath.clear();
    //     pathToEntry.clear();
    //     String path = "Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/entry_index.txt";
    //     File file = new File(path);

    //     if (!file.exists()) {
    //         System.out.println("No entries indexed yet.");
    //         return;
    //     }

    //     try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             if (line.trim().isEmpty() || !line.contains("="))
    //                 continue;
    //             String[] parts = line.split("=");
    //             if (parts.length < 3)
    //                 continue;
    //             String entryID = parts[0].trim();
    //             String filepath = parts[1].trim();
    //             String title = parts[2].trim();

    //             File entryFile = new File("Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + filepath);
    //             if (!entryFile.exists())
    //                 continue;

    //             try (BufferedReader entryReader = new BufferedReader(new FileReader(entryFile))) {
    //                 String date = "", mood = "", tag = "", fileTitle = "";
    //                 StringBuilder content = new StringBuilder();
    //                 boolean contentStarted = false;

    //                 String entryLine;
    //                 while ((entryLine = entryReader.readLine()) != null) {
    //                     if (entryLine.startsWith("Date: ")) {
    //                         date = entryLine.substring(6).trim();
    //                     } else if (entryLine.startsWith("Title: ")) {
    //                         fileTitle = entryLine.substring(7).trim();
    //                     } else if (entryLine.startsWith("Mood <3: ")) {
    //                         mood = entryLine.substring(9).trim();
    //                     } else if (entryLine.startsWith("Tag: ")) {
    //                         tag = entryLine.substring(5).trim();
    //                     } else if (entryLine.startsWith("-----")) {
    //                         contentStarted = true;
    //                     } else if (contentStarted) {
    //                         content.append(entryLine).append(System.lineSeparator());
    //                     }
    //                 }

    //                 // Entry entry = new Entry(date, fileTitle, content.toString(), mood, tag,
    //                 // diary, user, filepath,
    //                 // false);
    //                 Entry entry = new Entry.Builder(date, fileTitle, diary, user)
    //                         .content(content.toString())
    //                         .mood(mood)
    //                         .filepath(filepath)
    //                         .build();
    //                 titleToPath.put(fileTitle, filepath);
    //                 pathToEntry.put(filepath, entry);
    //             }
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Error loading entry index.");
    //     }

    // }

    public static void loadDiaryIndexFromFile(String userID, User user) {
        String path = "Users/" + userID + "/diary_index.txt";
        File file = new File(path);

        if (!file.exists()) {
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || !line.contains("="))
                    continue;

                String[] parts = line.split("=", 2);
                String diaryName = parts[0].trim();
                String diaryID = parts[1].trim();

                DiaryIndex.put(diaryName, diaryID);
                DiaryObjMap.put(diaryName, new Diary(user, diaryName, diaryID, false));
            }
        } catch (IOException e) {
            System.out.println("Diary index is not present...");
        }
    }

    public static void loadFromIndexFile() {
        File indexFile = new File("Users/user_index.txt");

        if (!indexFile.exists()) {
            System.out.println("\u001B[90mNo existing users found. Starting fresh.\u001B[0m");
            return;
        }
        try (Scanner scanner = new Scanner(indexFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    String username = parts[0].trim();
                    String userId = parts[1].trim();

                    UserIndex.put(username, userId);

                    File userFolder = new File("Users/" + userId);
                    File userprofile = new File(userFolder, "P_" + username + ".txt");
                    if (userprofile.exists()) {
                        try (Scanner fileScanner = new Scanner(userprofile)) {
                            String name = "", gender = "", primary_key = "", secondary_key = "", diaryName = "";
                            int age = 0;
                            while (fileScanner.hasNextLine()) {
                                String dataline = fileScanner.nextLine();
                                if (dataline.startsWith("Full Name: "))
                                    name = dataline.substring(10).trim();
                                else if (dataline.startsWith("Age: "))
                                    age = Integer.parseInt(dataline.substring(4).trim());
                                else if (dataline.startsWith("Gender: "))
                                    gender = dataline.substring(7).trim();
                                else if (dataline.startsWith("Primary Key: "))
                                    primary_key = dataline.substring(13).trim();
                                else if (dataline.startsWith("Secondary Key: "))
                                    secondary_key = dataline.substring(15).trim();
                                else if (dataline.startsWith("Diary name: "))
                                    diaryName = dataline.substring(12).trim();
                            }
                            User u = new User(userId, username, primary_key, secondary_key, name, age, gender,
                                    userFolder, diaryName, false);
                            UserObjMap.put(username, u);

                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading index file: " + e.getMessage());
        }
    }

    // public static void saveEntryToFile(Entry entry) {
    //     String[] dateTimeParts = entry.getDate().split(" ");
    //     String dateFolder = dateTimeParts[0];
    //     String timePart = dateTimeParts[1].replace(":", "-");

    //     String path = "Users/" + entry.getUser().getUserID() + "/" + entry.getDiary().getDiaryID() + "/" + dateFolder;

    //     File folder = new File(path);
    //     if (!folder.exists()) {
    //         folder.mkdirs();
    //     }

    //     File file = new File(folder, timePart + ".txt");
    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
    //         writer.write("Date: " + entry.getDate() + "\n");
    //         writer.write("Title: " + entry.getTitle() + "\n");
    //         writer.write("Mood <3: " + entry.getMood() + "\n");
    //         // writer.write("Tag: " + entry.getTag() + "\n");
    //         writer.write("---------------------------------------\n");
    //         writer.write(entry.getContent());
    //         System.out.println("\u001b[1;32m(^.^) Entry saved successfully!\u001B[0m");
    //     } catch (IOException e) {
    //         System.out.println("Error saving the entry..");
    //     }
    // }

    public static Map<String, String> getUserIndex() {
        return UserIndex;
    }

    public static Map<String, User> getUserObjMap() {
        return UserObjMap;
    }

    public static Map<String, String> getDiaryIndex() {
        return DiaryIndex;
    }

    public static Map<String, Diary> getDiaryObjMap() {
        return DiaryObjMap;
    }

    // public static Map<String, String> getTitleToPath() {
    //     return titleToPath;
    // }

    // public static Map<String, Entry> getPathToEntry() {
    //     return pathToEntry;
    // }

}
