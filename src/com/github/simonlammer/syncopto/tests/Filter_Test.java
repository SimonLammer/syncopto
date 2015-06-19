/*
This file is part of Syncopto. � 2015 Simon Lammer (lammer.simon@gmail.com)

Syncopto is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Syncopto is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Syncopto.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.simonlammer.syncopto.tests;

import com.github.simonlammer.syncopto.logic.Filter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class Filter_Test {
    private static File dir;
    private static Path dirPath;
    private static String dirString = "testDirectory-Filter_Test/";

    /* test directory structure:
    .../testDirectory-Filter_Test/
        directoryA/
            a.file
            b.file
            c.file
        directoryB/
            d.file
            e.file
            f.file
        directoryR/sub1/sub2/sub3/sub4/
                file.file
        test.file
        text.file
        teet.file
     */
    private static String[] folderNames = {"directoryA/", "directoryB/", "directoryR/", "directoryR/sub1/","directoryR/sub1/sub2/","directoryR/sub1/sub2/sub3/","directoryR/sub1/sub2/sub3/sub4/"};
    private static String[] fileNames = {"directoryA/a.file","directoryA/b.file","directoryA/c.file","directoryB/d.file","directoryB/e.file","directoryB/f.file", "directoryR/sub1/sub2/sub3/sub4/file.file","test.file", "text.file", "teet.file"};

    @BeforeClass
    public static void setUp() {
        // create test directory
        dir = new File(dirString);
        if (dir.exists()) {
            Assert.fail("Test directory '" + dir.getAbsolutePath() + "' already exists, please delete the directory manually");
        }
        dir.mkdir();
        dirPath = dir.toPath();

        // create test directory structure
        for (String item : folderNames) {
            File f = new File(dirString + item);
            Assert.assertTrue(f.mkdir());
        }
        try {
            for (String item : fileNames) {
                File f = new File(dirString + item);
                Assert.assertTrue(f.createNewFile());
            }
        } catch (IOException e) {
            System.out.println("Threw IOException while creating new file: " + e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        // delete test-directory structure
        try {
            for (String item : fileNames) {
                File f = new File(dirString + item);
                Files.delete(f.toPath());
            }
            for (int i = folderNames.length - 1; i >= 0; i--) {
                File f = new File(dirString + folderNames[i]);
                boolean res = f.delete();
                if (!res) {
                    Assert.fail("Could not delete folder: " + f.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // delete test directory
        try {
            Files.delete(dirPath);
        } catch (IOException e) {
            Assert.fail("Threw IOException while deleting test-directory: " + e.getMessage());
        }
    }

    @Test
    public void testSimple() {
        Pattern pattern = Pattern.compile("te[sx]t\\.file");
        Filter f = new Filter("test", pattern);
        Assert.assertEquals("test", f.getName());
        Assert.assertEquals(pattern, f.getPattern());
        File[] selected = f.getSelectedFiles(dir);
        Arrays.sort(selected);
        Assert.assertEquals(2, selected.length);
        Assert.assertEquals(new File(dirString + fileNames[7]), selected[0]);
        Assert.assertEquals(new File(dirString + fileNames[8]), selected[1]);
        Assert.assertFalse(f.isSelected(new File(dirString + fileNames[9])));
    }

    @Test
    public void testDirectories() {
        Pattern pattern = Pattern.compile("[a-f](ile)?\\.file");
        Pattern dirPattern = Pattern.compile("directory[AB]");
        Filter f = new Filter("test", pattern, dirPattern);
        Assert.assertEquals(pattern, f.getPattern());
        Assert.assertEquals(dirPattern, f.getDirectoryPattern());
        File[] selected = f.getSelectedFiles(dir);
        Assert.assertEquals(6, selected.length);
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(f.isSelected(new File(dirString + fileNames[i])));
        }
        for (int i = 7; i < fileNames.length; i++) {
            Assert.assertFalse(f.isSelected(new File(dirString + fileNames[i])));
        }
    }

    @Test
    public void testRecursions() {
        Pattern pattern = Pattern.compile("f.*\\.file");
        Pattern dirPattern = Pattern.compile(".*");
        Filter f = new Filter("test", pattern, dirPattern);
        Assert.assertEquals("test", f.getName());
        Assert.assertEquals(pattern, f.getPattern());
        Assert.assertEquals(dirPattern, f.getDirectoryPattern());
        File[] selected = f.getSelectedFiles(dir);
        Assert.assertEquals(2, selected.length);
        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(f.isSelected(new File(dirString + fileNames[i])));
        }
        for (int i = 5; i < 7; i++) {
            Assert.assertTrue(f.isSelected(new File(dirString + fileNames[i])));
        }
        for (int i = 7; i < fileNames.length; i++) {
            Assert.assertFalse(f.isSelected(new File(dirString + fileNames[i])));
        }
    }

    /**
     * Checks whether all Filter constructors deliver an equal object when getting called with the same data.
     */
    @Test
    public void testEquality() {
        Boolean[] checkHiddenFilesValues = new Boolean[] {true, false};
        String[] patternStrings = {"a", "b", "c", "d", "e"};
        String[] dirPatternStrings = {"1", "2", "3", "4", null};
        String[] names = {"Alpha", "Beta", "Gamma", "Delta"};

        Pattern[] patterns = compileStringsToPatterns(patternStrings);
        Pattern[] dirPatterns = compileStringsToPatterns(dirPatternStrings);
        int numberOfIndividualFilters = checkHiddenFilesValues.length * patternStrings.length * dirPatternStrings.length * names.length;

        /* Filter has 8 public constructors:
        0.: Filter(String name, String patternRegex)
        1.: Filter(String name, String patternRegex, boolean checkHiddenFiles)
        2.: Filter(String name, String patternRegex, String directoryPatternRegex)
        3.: Filter(String name, String patternRegex, String directoryPatternRegex, boolean checkHiddenFiles)
        4.: Filter(String name, Pattern pattern)
        5.: Filter(String name, Pattern pattern, boolean checkHiddenFiles)
        6.: Filter(String name, Pattern pattern, Pattern directoryPattern)
        7.: Filter(String name, Pattern pattern, Pattern directoryPattern, boolean checkHiddenFiles)

        The returned objects should equal each other.
        (After calling setters on filters created through simple constructors)
         */
        // Create filters
        Filter[][] filters = new Filter[8][];
        for (int i = 0; i < filters.length; i++) {
            filters[i] = new Filter[numberOfIndividualFilters];
        }
        int filterIndex = 0;
        for (int checkHiddenFilesValueIndex = 0; checkHiddenFilesValueIndex < checkHiddenFilesValues.length; checkHiddenFilesValueIndex++) {
            for (int patternsIndex = 0; patternsIndex < patternStrings.length; patternsIndex++) {
                for (int dirPatternsIndex = 0; dirPatternsIndex < dirPatternStrings.length; dirPatternsIndex++) {
                    for (int namesIndex = 0; namesIndex < names.length; namesIndex++) {
                        // Constructor 0
                        filters[0][filterIndex] = new Filter(names[namesIndex], patternStrings[patternsIndex]);
                        filters[0][filterIndex].setCheckHiddenFiles(checkHiddenFilesValues[checkHiddenFilesValueIndex]);
                        filters[0][filterIndex].setDirectoryPattern(dirPatternStrings[dirPatternsIndex]);

                        // Constructor 1
                        filters[1][filterIndex] = new Filter(names[namesIndex], patternStrings[patternsIndex], checkHiddenFilesValues[checkHiddenFilesValueIndex]);
                        filters[1][filterIndex].setDirectoryPattern(dirPatternStrings[dirPatternsIndex]);

                        // Constructor 2
                        filters[2][filterIndex] = new Filter(names[namesIndex], patternStrings[patternsIndex], dirPatternStrings[dirPatternsIndex]);
                        filters[2][filterIndex].setCheckHiddenFiles(checkHiddenFilesValues[checkHiddenFilesValueIndex]);

                        // Constructor 3
                        filters[3][filterIndex] = new Filter(names[namesIndex], patternStrings[patternsIndex], dirPatternStrings[dirPatternsIndex], checkHiddenFilesValues[checkHiddenFilesValueIndex]);

                        // Constructor 4
                        filters[4][filterIndex] = new Filter(names[namesIndex], patterns[patternsIndex]);
                        filters[4][filterIndex].setCheckHiddenFiles(checkHiddenFilesValues[checkHiddenFilesValueIndex]);
                        filters[4][filterIndex].setDirectoryPattern(dirPatterns[dirPatternsIndex]);

                        // Constructor 5
                        filters[5][filterIndex] = new Filter(names[namesIndex], patterns[patternsIndex], checkHiddenFilesValues[checkHiddenFilesValueIndex]);
                        filters[5][filterIndex].setDirectoryPattern(dirPatterns[dirPatternsIndex]);

                        // Constructor 6
                        filters[6][filterIndex] = new Filter(names[namesIndex], patterns[patternsIndex], dirPatterns[dirPatternsIndex]);
                        filters[6][filterIndex].setCheckHiddenFiles(checkHiddenFilesValues[checkHiddenFilesValueIndex]);

                        // Constructor 7
                        filters[7][filterIndex] = new Filter(names[namesIndex], patterns[patternsIndex], dirPatterns[dirPatternsIndex], checkHiddenFilesValues[checkHiddenFilesValueIndex]);

                        filterIndex++;
                    }
                }
            }
        }

        // check equality
        for (int i = 0; i < filters.length; i++) {
            for (int j = 0; j < filters[i].length; j++) {
                Filter filter = filters[i][j];
                for (int k = 0; k < filters.length; k++) {
                    for (int l = 0; l < filters[k].length; l++) {
                        Filter other = filters[k][l];
                        if (j == l) {
                            Assert.assertTrue(filter.hashCode() == other.hashCode());
                            Assert.assertTrue(filter.equals(other));
                        } else {
                            Assert.assertFalse(filter.equals(other));
                        }
                    }
                }
            }
        }
    }
    private Pattern[] compileStringsToPatterns(String[] strings) {
        Pattern[] patterns = new Pattern[strings.length];
        for (int i = 0; i < strings.length; i++) {
            patterns[i] = strings[i] == null ? null : Pattern.compile(strings[i]);
        }
        return patterns;
    }
}