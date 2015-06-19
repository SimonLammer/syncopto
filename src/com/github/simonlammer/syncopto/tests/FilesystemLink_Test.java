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

package com.github.simonlammer.syncopto.tests;

import org.junit.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 * Created by Simon Lammer on 13.05.15.
 */
public class FilesystemLink_Test {
    private static final String dirName = "FilesystemLink-Testdirectory";
    private static Path file;
    private static Path link;
    private static final String content = "Hello World!";

    @BeforeClass
    public static void setUp() throws IOException {
        new File(dirName).mkdir();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        Files.delete(link);
        Files.delete(file);
        new File(dirName).delete();
    }

    @Before
    public void createFiles() throws IOException {
        if (file != null) {
            Files.deleteIfExists(file);
        }
        if (link != null) {
            Files.deleteIfExists(link);
        }
        File file = new File(dirName + "/file.txt");
        FilesystemLink_Test.file = file.toPath();
        boolean res = file.createNewFile();
        link = Files.createLink(new File(dirName + "/link.txt").toPath(), FilesystemLink_Test.file = file.toPath());

        Files.write(FilesystemLink_Test.file, content.getBytes(Charset.forName("UTF-8")));
    }

    @Test
    public void modifyStampTest() {
        File linkFile = link.toFile();
        long modified = linkFile.lastModified();
        modified += 1000;
        boolean res = linkFile.setLastModified(modified);
        Assert.assertTrue(res);
        Assert.assertEquals(modified, linkFile.lastModified());
        Assert.assertEquals(modified, file.toFile().lastModified());
    }

    /**
     * Tests whether the actual file (accessable through the link) remains when the original file is being deleted.
     * @throws IOException
     */
    @Test
    public void deleteFileTest() throws IOException {
        deleteTest(file, link);
    }

    /**
     * Test whether the file remains when the link is being deleted.
     * @throws IOException
     */
    @Test
    public void deleteLinkTest() throws IOException {
        deleteTest(link, file);
    }
    private void deleteTest(Path toDelete, Path toValidate) throws IOException {
        Files.delete(toDelete);
        FileReader reader = new FileReader(toValidate.toFile());
        char[] buffer = new char[content.length()];
        reader.read(buffer);
        reader.close();
        Assert.assertTrue(String.valueOf(buffer).equals(content));
    }
}
