package com.kata13;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.newBufferedReader;

public class DirectoryCodeLineCounter {
    private static final String FILE_EXTENSION = ".java";

    private static final String EMPTY_STRING_PATTERN = "^\\s*$";
    private static final String ONE_LINE_COMMENT_PATTERN = "^\\s*(//.*|/\\*[^(\\*/)]*\\*/\\s*)$";

    private static final String MULTIPLE_LINE_COMMENT_STARTED_FROM_BEGIN_LINE_PATTERN = "^\\s*/\\*.*$";
    private static final String MULTIPLE_LINE_COMMENT_STARTED_FROM_MIDDLE_LINE_PATTERN = "^\\s*[^(/\\*)]+/\\*.*$";
    private static final String MULTIPLE_LINE_COMMENT_FINISHED_BY_END_LINE_PATTERN = "^[^\\*]*\\*/\\s*$";
    private static final String MULTIPLE_LINE_COMMENT_FINISHED_BY_MIDDLE_LINE_PATTERN = "^[^(/\\*)]*\\*/.+\\s*$";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter root directory: ");

        new DirectoryCodeLineCounter().countCodeLines(scanner.nextLine());
    }

    public int countCodeLines(String rootDirectoryPath) {
        int rootCodeSize = 0;
        Path root = Paths.get(rootDirectoryPath);
        try (Stream<Path> stream = Files.walk(root, Integer.MAX_VALUE)) {
            // Get files grouped by directory where directories are sorted by name
            Map<Path, List<Path>> filesByDirectory = stream
                    .filter(path -> path.toString().endsWith(FILE_EXTENSION))
                    .collect(Collectors.groupingBy(Path::getParent, TreeMap::new, Collectors.toList()));

            for (Map.Entry<Path, List<Path>> directory : filesByDirectory.entrySet()) {
                rootCodeSize += getDirectoryCodeLineSize(directory, root);
            }
            System.out.printf("Total code size for root directory %s equals %d%n",
                    root.getFileName().toString(), rootCodeSize);
        } catch (IOException e) {
            System.out.printf("Root directory %s does not exist", rootDirectoryPath);
        }

        return rootCodeSize;
    }

    private int getDirectoryCodeLineSize(Map.Entry<Path, List<Path>> directory, Path root) {
        int directoryCodeSize = 0;
        List<String> directoryFiles = new LinkedList<>();

        for (Path file : directory.getValue()) {
            int fileCodeLineSize = getFileCodeLineSize(file);
            directoryCodeSize += fileCodeLineSize;
            directoryFiles.add(file.getFileName().toString() + ":" + fileCodeLineSize);
        }

        //output directory info
        System.out.printf("%s:%d %s%n",
                directory.getKey().equals(root)
                        ? directory.getKey().getFileName().toString()
                        : directory.getKey().toString().substring(root.toString().length()),
                directoryCodeSize,
                directoryFiles.stream().collect(Collectors.joining(" ")));

        return directoryCodeSize;
    }

    private int getFileCodeLineSize(Path file){
        int fileSize = 0;
        int commentSize = 0;
        boolean isMultipleComment = false;

        try (BufferedReader reader = newBufferedReader(file, StandardCharsets.UTF_8)) {
            for (; ; fileSize++) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }

                if (isMultipleComment) {
                    if (line.matches(MULTIPLE_LINE_COMMENT_FINISHED_BY_END_LINE_PATTERN)
                            || line.matches(MULTIPLE_LINE_COMMENT_FINISHED_BY_MIDDLE_LINE_PATTERN)) {
                        isMultipleComment = false;

                        if (line.matches(MULTIPLE_LINE_COMMENT_FINISHED_BY_END_LINE_PATTERN)) {
                            commentSize++;
                        }
                    } else {
                        commentSize++;
                    }
                } else {
                    if (line.matches(EMPTY_STRING_PATTERN) || line.matches(ONE_LINE_COMMENT_PATTERN)) {
                        commentSize++;
                    } else if (line.matches(MULTIPLE_LINE_COMMENT_STARTED_FROM_BEGIN_LINE_PATTERN)
                            || line.matches(MULTIPLE_LINE_COMMENT_STARTED_FROM_MIDDLE_LINE_PATTERN)) {
                        isMultipleComment = true;

                        if (line.matches(MULTIPLE_LINE_COMMENT_STARTED_FROM_BEGIN_LINE_PATTERN)) {
                            commentSize++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.printf("File %s was parsed with error %s", file.getFileName().toString(), e.getMessage());
        }
        return fileSize - commentSize;
    }
}
