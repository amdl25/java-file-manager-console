# Java File Manager Console

Java console application that keeps track of the directories you choose, shows their multimedia files as a tree and generates statistics about them. The list of monitored directories is saved between runs.

## Features
- Add a directory and display its content as a tree, including subdirectories
- Remove a monitored directory
- Save the monitored directories to a file and reload them on the next start
- Generate a statistics report with:
  - files grouped by extension
  - the size of every file and the total size of every directory, including subdirectories
  - duplicate files (same name and same size)
- Only multimedia files are considered: `mp3`, `wav`, `jpg` and `png`

## Concepts used
Object-oriented design with an abstract base class, recursion for walking directory trees, collections (`List`, `HashMap`), regular expressions for validating paths, file I/O with `BufferedReader` and `BufferedWriter`, and custom exceptions.

## Project structure

| Class | Role |
|---|---|
| `Main` | entry point |
| `Menu` | console menu and tree printing |
| `DirectoryManager` | monitored directories, validation, persistence and statistics |
| `FileSystemEntry` | abstract base class for files and directories |
| `FileEntry` | a file (path, name, extension) |
| `Directory` | a directory with its files and subdirectories |
| `DirectoryAlreadyExistsException`, `InvalidOptionException` | custom exceptions |

## Getting started

Requires JDK 11 or newer.

```bash
javac *.java
java Main
```

Menu options:
1. Add a directory and show its content
2. Delete a directory
3. Generate statistics (written to `statistics.txt`)
4. Save and exit

The monitored directories are stored in `monitored_directories.txt`. Both files are created automatically in the working folder.

## Notes
- Paths must be Windows paths that start with a drive letter, for example `C:\Users\me\Music`.
- The menu texts and code comments are in Romanian.
