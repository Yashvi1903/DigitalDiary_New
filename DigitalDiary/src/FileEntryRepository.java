import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileEntryRepository implements EntryRepository {

    
    private final Map<String, String> titleToPath = new LinkedHashMap<>();
    private final Map<String, Entry>  pathToEntry = new LinkedHashMap<>();

    
    private final EntryTrie trie = new EntryTrie();

    @Override
    public Map<String, String> getTitleToPath() { return titleToPath; }

    @Override
    public Map<String, Entry> getPathToEntry()  { return pathToEntry; }

    
    @Override
    public Map<String, Entry> loadAll(Diary diary, User user) {
        titleToPath.clear();
        pathToEntry.clear();
        trie.clear();   

        String path = "Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/entry_index.txt";
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("No entries indexed yet.");
            return pathToEntry;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=");
                if (parts.length < 3) continue;

                String filepath = parts[1].trim();
                File entryFile  = new File(
                    "Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + filepath);
                if (!entryFile.exists()) continue;

                try (BufferedReader entryReader = new BufferedReader(new FileReader(entryFile))) {
                    String date = "", mood = "", tag = "", fileTitle = "";
                    StringBuilder content = new StringBuilder();
                    boolean contentStarted = false;

                    String entryLine;
                    while ((entryLine = entryReader.readLine()) != null) {
                        if      (entryLine.startsWith("Date: "))    date      = entryLine.substring(6).trim();
                        else if (entryLine.startsWith("Title: "))   fileTitle = entryLine.substring(7).trim();
                        else if (entryLine.startsWith("Mood <3: ")) mood      = entryLine.substring(9).trim();
                        else if (entryLine.startsWith("Tag: "))     tag       = entryLine.substring(5).trim();
                        else if (entryLine.startsWith("-----"))     contentStarted = true;
                        else if (contentStarted)                    content.append(entryLine).append(System.lineSeparator());
                    }

                    Entry entry = new Entry.Builder(date, fileTitle, diary, user)
                            .content(content.toString())
                            .mood(mood)
                            .tag(tag)
                            .filepath(filepath)
                            .build();

                    titleToPath.put(fileTitle, filepath);
                    pathToEntry.put(filepath, entry);
                    trie.insert(fileTitle);   
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading entry index.");
        }

        return pathToEntry;
    }

    // ── save ──────────────────────────────────────────────────────────────
    @Override
    public void save(Entry entry) {
        String[] dateTimeParts = entry.getDate().split(" ");
        String dateFolder = dateTimeParts[0];
        String timePart   = dateTimeParts[1].replace(":", "-");

        String dirPath = "Users/" + entry.getUser().getUserID()
                + "/" + entry.getDiary().getDiaryID() + "/" + dateFolder;
        File folder = new File(dirPath);
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, timePart + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Date: "    + entry.getDate()  + "\n");
            writer.write("Title: "   + entry.getTitle() + "\n");
            writer.write("Mood <3: " + entry.getMood()  + "\n");
            writer.write("---------------------------------------\n");
            writer.write(entry.getContent());
            System.out.println("\u001b[1;32m(^.^) Entry saved successfully!\u001B[0m");
        } catch (IOException e) {
            System.out.println("Error saving the entry..");
            return;
        }

        
        titleToPath.put(entry.getTitle(), entry.getFilePath());
        pathToEntry.put(entry.getFilePath(), entry);
        trie.insert(entry.getTitle());   // keep Trie in sync after save

        
        File indexFile = new File("Users/" + entry.getUser().getUserID()
                + "/" + entry.getDiary().getDiaryID() + "/entry_index.txt");
        try (FileWriter writer = new FileWriter(indexFile, true)) {
            writer.write(entry.getDate() + "=" + entry.getFilePath() + "=" + entry.getTitle() + "\n");
        } catch (IOException e) {
            System.out.println("Failed to save entry index.");
        }
    }

    
    @Override
    public void rewriteIndex(Diary diary, User user) {
        String path = "Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/entry_index.txt";
        File indexFile = new File(path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile, false))) {
            for (Map.Entry<String, String> e : titleToPath.entrySet()) {
                Entry entry = pathToEntry.get(e.getValue());
                if (entry != null) {
                    writer.write(entry.getDate() + "=" + e.getValue() + "=" + e.getKey());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to update entry index: " + e.getMessage());
        }
    }

    @Override
    public Optional<Entry> findByTitle(String title) {
        String path = titleToPath.get(title);
        if (path == null) return Optional.empty();
        return Optional.ofNullable(pathToEntry.get(path));
    }


    @Override
    public boolean delete(String title, Diary diary, User user) {
        String filePath = titleToPath.get(title);
        if (filePath == null) return false;

        File entryFile = new File(
            "Users/" + user.getUserID() + "/" + diary.getDiaryID() + "/" + filePath);
        if (entryFile.exists() && !entryFile.delete()) return false;

        titleToPath.remove(title);
        pathToEntry.remove(filePath);
        trie.delete(title);   
        rewriteIndex(diary, user);
        return true;
    }

    @Override
    public List<String> searchByPrefix(String prefix) {
        return trie.searchByPrefix(prefix);
    }
}