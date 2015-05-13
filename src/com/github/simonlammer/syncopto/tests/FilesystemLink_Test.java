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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Simon Lammer on 13.05.15.
 */
public class FilesystemLink_Test {
    private static final String dirName = "FilesystemLink-Testdirectory";
    private static Path file;
    private static Path link;

    @BeforeClass
    public static void setUp() throws IOException {
        new File(dirName).mkdir();
        File file = new File(dirName + "/file.file");
        boolean res = file.createNewFile();
        link = Files.createLink(new File(dirName + "/link").toPath(), FilesystemLink_Test.file = file.toPath());
    }

    @AfterClass
    public static void tearDown() throws IOException {
        Files.delete(link);
        Files.delete(file);
        new File(dirName).delete();
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
}
