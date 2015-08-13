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

package com.github.simonlammer.syncopto.tests.learningtests;

import com.github.simonlammer.syncopto.tests.utils.RWeTestdirectoryTest;
import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.*;

public class FilesystemLink_Test extends RWeTestdirectoryTest {
    private static final String content = "Hello World!";
    private File originalFile;
    private File linkFile;

    @Before
    public void setUp() {
        createFiles();
        writeContentToFile();
    }

    private void createFiles() {
        createOriginalFile();
        createLinkFile();
    }

    private void createOriginalFile() {
        originalFile = testDirectory.createFile("file.txt", "RWe");
    }

    private void createLinkFile() {
        linkFile = new File(testDirectory.getDirectory(), "link.txt");
        Path linkPath = null;
        try {
            linkPath = Files.createLink(linkFile.toPath(), originalFile.toPath());
        } catch (IOException ex) {
            fail("IOException occured: " + ex.getMessage());
        }
        linkFile = linkPath.toFile();
    }

    private void writeContentToFile() {
        byte[] bytes = content.getBytes();
        try {
            Files.write(originalFile.toPath(), bytes, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            fail("IOException occured: " + ex.getMessage());
        }
    }

    @After
    public void deleteFiles() {
        originalFile.delete();
        linkFile.delete();
    }

    @Test
    public void linkFileMirrorsModificationStampChangeTest() {
        long newLastModified = amplifyLastModified(originalFile, 1000);
        Assert.assertEquals(newLastModified, linkFile.lastModified());
    }

    @Test
    public void originalFileMirrorsModificationStampChangeTest() {
        long newLastModified = amplifyLastModified(linkFile, 1000);
        Assert.assertEquals(newLastModified, originalFile.lastModified());
    }

    private long amplifyLastModified(File file, long delta) {
        long modified = file.lastModified();
        modified += delta;
        assertTrue("Could not setLastModified on '" + file.getAbsolutePath() + "'",
                file.setLastModified(modified)
        );
        return modified;
    }

    @Test
    public void deleteOriginalFileTest() {
        deleteTest(originalFile, linkFile);
    }

    @Test
    public void deleteLinkFileTest() {
        deleteTest(linkFile, originalFile);
    }

    private void deleteTest(File toDelete, File toValidate) {
        try {
            Files.delete(toDelete.toPath());
            assertFileContainsContent(toValidate);
        } catch (IOException ex) {
            fail("IOException occured: " + ex.getMessage());
        }
    }

    private void assertFileContainsContent(File file) throws IOException {
        FileReader reader = new FileReader(file);
        char[] buffer = new char[content.length()];
        reader.read(buffer);
        reader.close();
        Assert.assertTrue(String.valueOf(buffer).equals(content));
    }
}
