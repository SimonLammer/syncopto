/*
This file is part of Syncopto. © 2015 Simon Lammer (lammer.simon@gmail.com)

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

package com.github.simonlammer.syncopto.tests.filters;

import com.github.simonlammer.syncopto.logic.filters.FilenameFilter;
import com.github.simonlammer.syncopto.logic.filters.RegexFilter;
import com.github.simonlammer.syncopto.tests.utils.RWeTestdirectoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class FilenameFilter_Test extends RWeTestdirectoryTest {
    private FilenameFilter filter;
    File[] files;

    @Before
    public void createFiles() {
        files = testDirectory.createFiles("RWe",
                "1.text", "a.text", "2.text", "b.text",
                "3.nottext", "c.nottext", "4.nottext"
        );
    }

    @After
    public void deleteFiles() {
        for (File file : files) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void selectTextfilesTest() {
        test(".*\\.text$", RegexFilter.Mode.SELECT_MATCHING,
                (String filename) -> filename.endsWith(".text")
        );
    }

    @Test
    public void selectNotTextFilesTest() {
        test(".*\\.nottext", RegexFilter.Mode.SELECT_MATCHING,
                (String filename) -> filename.endsWith(".nottext")
        );
    }

    @Test
    public void selectNumericalFilesTest() {
        test(".*\\d\\.[^.]*$", RegexFilter.Mode.SELECT_MATCHING,
                (String filename) -> {
                    filename = determineFilenameWithoutExtension(filename);
                    char firstCharacter = filename.charAt(0);
                    return Character.isDigit(firstCharacter);
                }
        );
    }

    @Test
    public void selectNonNumericalFilesTest() {
        test(".*\\d\\.[^.]*$", RegexFilter.Mode.SELECT_NONMATCHING,
                (String filename) -> {
                    filename = determineFilenameWithoutExtension(filename);
                    char firstCharacter = filename.charAt(0);
                    return !Character.isDigit(firstCharacter);
                }
        );
    }

    @Test
    public void selectAlphabeticalFilesTest() {
        test(".*[a-z]\\.[^.]*$", RegexFilter.Mode.SELECT_MATCHING,
                (String filename) -> {
                    filename = determineFilenameWithoutExtension(filename);
                    char firstCharacter = filename.charAt(0);
                    return Character.isLetter(firstCharacter);
                }
        );
    }

    @Test
    public void selectNonAlphabeticalFilesTest() {
        test(".*[a-z]\\.[^.]*$", RegexFilter.Mode.SELECT_NONMATCHING,
                (String filename) -> {
                    filename = determineFilenameWithoutExtension(filename);
                    char firstCharacter = filename.charAt(0);
                    return !Character.isLetter(firstCharacter);
                }
        );
    }

    @Test
    public void selectAlphabeticalTextFilesTest() {
        test(".*[a-z]\\.text$", RegexFilter.Mode.SELECT_MATCHING,
                (String filename) -> {
                    String filenameWithoutExtension = determineFilenameWithoutExtension(filename);
                    char firstCharacter = filenameWithoutExtension.charAt(0);
                    return Character.isLetter(firstCharacter) && filename.endsWith(".text");
                }
        );
    }

    private void test(String regexPattern, RegexFilter.Mode mode, Function<String, Boolean> createria) {
        FilenameFilter filter = generateFilenameFilter(regexPattern, mode);
        Collection<File> unfilteredFileCollection = Arrays.asList(files);
        Collection<File> filteredFileCollection = filter.determineSelectedValues(unfilteredFileCollection);

        Iterator<File> unfilteredFileIterator = unfilteredFileCollection.iterator();
        Iterator<File> filteredFileIterator = filteredFileCollection.iterator();
        while(unfilteredFileIterator.hasNext() && filteredFileIterator.hasNext()) {
            File filteredFile = filteredFileIterator.next();
            File unfilteredFile = determineNextFileThatMatchesCreateria(unfilteredFileIterator, createria);
            assertEquals(unfilteredFile, filteredFile);
        }
        if (filteredFileCollection.size() == 0 &&
                determineNextFileThatMatchesCreateria(unfilteredFileIterator, createria) != null) {
            fail();
        }
    }

    private File determineNextFileThatMatchesCreateria(Iterator<File> fileIterator, Function<String, Boolean> createria) {
        if (!fileIterator.hasNext()) {
            throw new IllegalArgumentException("Iterator has no more elements");
        }
        File file;
        do {
            file = fileIterator.next();
        } while (fileIterator.hasNext() && !createria.apply(file.getName()));
        return file;
    }

    private FilenameFilter generateFilenameFilter(String regexPattern, RegexFilter.Mode mode) {
        return new FilenameFilter(new RegexFilter(Pattern.compile(regexPattern), mode));
    }

    private String determineFilenameWithoutExtension(String filename) {
        String[] arr = filename.split("\\" + files[0].toPath().getFileSystem().getSeparator());
        String name = arr[arr.length - 1];
        name = name.substring(0, 1);
        return name;
    }
}
