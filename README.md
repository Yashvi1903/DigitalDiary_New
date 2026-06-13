# Digital Diary — Java Console Application

A file-based personal diary application built in Java demonstrating OOP, SOLID principles, and DSA concepts.

---

## Tech Stack
- **Language:** Java 
- **Storage:** File system (plain text files)
- **Security:** password hashing

## How to Run
```bash
javac *.java
java App
```

---

## Features
- Register / Login with primary + secondary password
- Create and manage multiple diaries per user
- Add, View, Edit, Delete, Download diary entries
- **Prefix-based entry search** powered by Trie (O(L) time)
- Mood tracking per entry
- Self-healing index — corrupted/missing user data auto-removed on startup
- Export single entry or entire diary to `.txt`

---



## OOP Concepts Used

| Concept | Where |
|---|---|
| **Encapsulation** | `User`, `Entry`, `Diary` — private fields, public getters only |
| **Builder Pattern** | `Entry.Builder` — safe object creation with optional fields |
| **Repository Pattern** | `EntryRepository` interface + `FileEntryRepository` impl |
| **Singleton** | `AppContext.scanner()` — one Scanner across entire app |
| **Abstraction** | `EntryRepository` hides all file I/O from business logic |

---

## SOLID Principles

| Principle | Applied |
|---|---|
| **S** — Single Responsibility | `PasswordUtils` only hashes, `MoodTracker` only tracks mood, `EntryTrie` only searches |
| **O** — Open/Closed | New repository type (DB, cloud) can be added without touching existing code |
| **D** — Dependency Inversion | `DeleteEntry`, `ViewPastEntry`, `EditEntry` all depend on `EntryRepository` interface, not `FileEntryRepository` directly |

---

## DSA — Trie Implementation

**Class:** `EntryTrie.java`

**Why Trie over linear scan:**
| Operation | Linear Scan | Trie |
|---|---|---|
| Search by prefix | O(N × L) | O(L + K) |
| Insert | — | O(L) |
| Delete | — | O(L) |

*N = total entries, L = prefix length, K = number of matches*

**Design choice:** `Map<Character, TrieNode>` instead of `char[26]` array — handles spaces, numbers, any Unicode character without wasting memory on unused slots.

**Sync points** — Trie stays in sync with data at all 4 operations:
- `loadAll()` → `trie.clear()` + `trie.insert()` per entry
- `save()` → `trie.insert()`
- `delete()` → `trie.delete()`
- `searchByPrefix()` → `trie.searchByPrefix()`

---

## Security
- Passwords hashed with **SHA-256** via `MessageDigest` — never stored in plaintext
- **3-attempt lockout** on primary password, then falls back to secondary key
- Secondary key login forces **mandatory password reset**

---

## Known Limitations / Future Improvements
- Database backend (swap `FileEntryRepository` for `DBEntryRepository` — zero other changes needed)
- LRU cache for repeated entry access in DB-backed version
- `FileLock` for concurrent session safety
- Move `Entry.menu()` to a dedicated controller class
