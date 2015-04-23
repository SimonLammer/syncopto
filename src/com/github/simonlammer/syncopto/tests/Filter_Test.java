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

import com.github.simonlammer.syncopto.logic.Filter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Filter_Test {
    private static File dir;
    private static Path dirPath;
    private static Path[] files;
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
        Pattern pattern = Pattern.compile("te[sx]t");
        Filter f = new Filter("test", pattern);
        Path[] selected = f.getSelectedFiles(dirPath);
        Assert.assertEquals(2, selected.length);
        Assert.assertEquals(files[0], selected[0]);
        Assert.assertEquals(files[1], selected[1]);
        Assert.assertFalse(f.isSelected(files[2]));
    }
}
