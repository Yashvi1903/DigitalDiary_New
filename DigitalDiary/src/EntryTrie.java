import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntryTrie {

    
    private static class TrieNode {
        
        Map<Character, TrieNode> children = new HashMap<>();

       
        boolean isEndOfWord = false;
        String originalTitle = null;
    }

    
    private final TrieNode root = new TrieNode();

    public void insert(String title) {
        if (title == null || title.isEmpty()) return;

        TrieNode current = root;
        String lowerTitle = title.toLowerCase();

        for (char ch : lowerTitle.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }

        current.isEndOfWord = true;
        current.originalTitle = title;   
    }

    
    public List<String> searchByPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return results;

        TrieNode current = root;
        String lowerPrefix = prefix.toLowerCase();

       
        for (char ch : lowerPrefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return results;   
            }
            current = current.children.get(ch);
        }
        collectAllTitles(current, results);
        return results;
    }

    
    public boolean contains(String title) {
        if (title == null || title.isEmpty()) return false;

        TrieNode current = root;
        for (char ch : title.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(ch)) return false;
            current = current.children.get(ch);
        }
        return current.isEndOfWord;
    }

    public void delete(String title) {
        if (title == null || title.isEmpty()) return;
        deleteHelper(root, title.toLowerCase(), 0);
    }

    public void clear() {
        root.children.clear();
        root.isEndOfWord = false;
        root.originalTitle = null;
    }

    private void collectAllTitles(TrieNode node, List<String> results) {
        if (node.isEndOfWord) {
            results.add(node.originalTitle);
        }
        for (TrieNode child : node.children.values()) {
            collectAllTitles(child, results);
        }
    }
    
    private boolean deleteHelper(TrieNode current, String lowerTitle, int depth) {
        if (depth == lowerTitle.length()) {
            if (!current.isEndOfWord) return false;   // title not in trie
            current.isEndOfWord = false;
            current.originalTitle = null;
            return current.children.isEmpty();         // safe to delete if leaf
        }

        char ch = lowerTitle.charAt(depth);
        TrieNode child = current.children.get(ch);
        if (child == null) return false;               // path doesn't exist

        boolean shouldDeleteChild = deleteHelper(child, lowerTitle, depth + 1);

        if (shouldDeleteChild) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }
        return false;
    }
}