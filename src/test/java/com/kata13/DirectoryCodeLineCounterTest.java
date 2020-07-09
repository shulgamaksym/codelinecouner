package com.kata13;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class DirectoryCodeLineCounterTest {

    @Test
    public void testDirectoryWithEmptyLineJavaClass() {
        int fileSize = 5;
        int commentLineSize = 1;

        File resourcesDirectory = new File("src/test/resources/data/emptylinedir");

        int codeLineSize = new DirectoryCodeLineCounter().countCodeLines(resourcesDirectory.getAbsolutePath());
        assertEquals(fileSize - commentLineSize, codeLineSize);
    }

    @Test
    public void testDirectoryWithSingleCommentJavaClass() {
        int fileSize = 6;
        int commentLineSize = 2;

        File resourcesDirectory = new File("src/test/resources/data/singlecommentdir");

        int codeLineSize = new DirectoryCodeLineCounter().countCodeLines(resourcesDirectory.getAbsolutePath());
        assertEquals(fileSize - commentLineSize, codeLineSize);
    }

    @Test
    public void testDirectoryWithMultipleCommentStartedFromBeginJavaClass() {
        int fileSize = 7;
        int commentLineSize = 3;

        File resourcesDirectory = new File("src/test/resources/data/multicommentbegindir");

        int codeLineSize = new DirectoryCodeLineCounter().countCodeLines(resourcesDirectory.getAbsolutePath());
        assertEquals(fileSize - commentLineSize, codeLineSize);
    }

    @Test
    public void testDirectoryWithMultipleCommentStartedFromMiddleJavaClass() {
        int fileSize = 5;
        int commentLineSize = 1;

        File resourcesDirectory = new File("src/test/resources/data/multicommentmiddledir");

        int codeLineSize = new DirectoryCodeLineCounter().countCodeLines(resourcesDirectory.getAbsolutePath());
        assertEquals(fileSize - commentLineSize, codeLineSize);
    }
}
