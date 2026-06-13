import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EntryRepository {

    // Load all entries for this diary/user into memory and return them
    Map<String, Entry> loadAll(Diary diary, User user);

    // Get the title -> filepath index for this diary/user
    Map<String, String> getTitleToPath();

    // Get the filepath -> Entry index for this diary/user
    Map<String, Entry> getPathToEntry();

    // Persist a brand new entry (writes the entry file + appends to entry_index.txt)
    void save(Entry entry);

    // Rewrite entry_index.txt from current in-memory state (used after edit/delete)
    void rewriteIndex(Diary diary, User user);

    // Find an entry by its title (exact match)
    Optional<Entry> findByTitle(String title);

    // Remove an entry (both from maps and from disk)
    boolean delete(String title, Diary diary, User user);

    // ── Trie-powered search ───────────────────────────────────────────────
    List<String> searchByPrefix(String prefix);
}