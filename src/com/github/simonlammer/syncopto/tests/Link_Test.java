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
import com.github.simonlammer.syncopto.logic.Link;
import com.github.simonlammer.syncopto.logic.LinkMode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class Link_Test {
    private static File dirOrigin;
    private static Path dirOriginPath;
    private static File dirDestination;
    private static Path dirDestinationPath;

    @BeforeClass
    public static void setUp() {
        // origin
        // create test directory
        dirOrigin = new File("testOrigin");
        Assert.assertTrue("Test directory '" + dirOrigin.getAbsolutePath() + "' already exists, please delete the directory manually", dirOrigin.exists());
        dirOrigin.mkdir();
        dirOriginPath = dirOrigin.toPath();

        // destination
        // create test directory
        dirDestination = new File("testDestination");
        Assert.assertTrue("Test directory '" + dirDestination.getAbsolutePath() + "' already exists, please delete the directory manually", dirDestination.exists());
        dirDestination.mkdir();
        dirDestinationPath = dirDestination.toPath();
    }

    @AfterClass
    public static void tearDown() {
        // delete test directory
        if (dirOrigin.exists()) {
            try {
                Files.delete(dirOriginPath);
            } catch (IOException e) {
                Assert.fail("Threw IOException while deleting file: " + e.getMessage());
            }
        }
        if (dirDestination.exists()) {
            try {
                Files.delete(dirDestinationPath);
            } catch (IOException e) {
                Assert.fail("Threw IOException while deleting file: " + e.getMessage());
            }
        }
    }

    @Test
    public void testConstructors() throws Exception {
        Collection<Filter> filterCollection = new LinkedList<Filter>();
        Link link1 = new Link("link1", LinkMode.SELECTION,dirOrigin, dirDestination);
        Assert.assertTrue("Link Constructor not working properly", isLinkOk(link1,LinkMode.SELECTION,dirOrigin,dirDestination));
        Link link2 = new Link("link2", LinkMode.SELECTION,dirOrigin,dirDestination,filterCollection);
        Assert.assertTrue("Link Constructor not working properly", isLinkOk(link1,LinkMode.SELECTION,dirOrigin,dirDestination));
        Link link3 = new Link("link3", LinkMode.SELECTION,dirOriginPath.toString(),dirDestinationPath.toString());
        Assert.assertTrue("Link Constructor not working properly", isLinkOk(link1,LinkMode.SELECTION,dirOrigin,dirDestination));
        Link link4 = new Link("link4", LinkMode.SELECTION,dirOriginPath.toString(),dirDestinationPath.toString(),filterCollection);
        Assert.assertTrue("Link Constructor not working properly", isLinkOk(link1,LinkMode.SELECTION,dirOrigin,dirDestination));
    }
    private boolean isLinkOk(Link link, LinkMode mode, File origin, File dest){
        if(link == null){
            return false;
        }
        if(link.getName() == null || link.getName().isEmpty()){
            return false;
        }
        if(link.getMode() == null || link.getMode().equals(mode)){
            return false;
        }
        if(link.getOriginDirectory() == null || !link.getOriginDirectory().equals(origin)){
            return false;
        }
        if(link.getDestinationDirectory() == null || !link.getDestinationDirectory().equals(dest)){
            return false;
        }
        return true;
    }

    /**
     * Tests the behaviour of a link in watching mode (<Link>.startWatch()).
     * In this mode, if a file is created or deleted in either of the two directories,
     * the action should be mirrored in the other directory.
     * @throws IOException
     */
    @Test
    public void watchingBehaviourTest() throws IOException {
        Link link = new Link("BehaviourTest link", LinkMode.SELECTION, dirOrigin, dirDestination);
        Filter filter = new Filter("BehaviourTest filter", ".*\\.txt");
        link.addFilter(filter);
        link.startWatch();
        String filename = "file.txt";
        String content = "Hello World!";
        long timeBuffer = 1;

        createAndDeleteFile(dirOrigin, dirDestination, filename, content, timeBuffer);
        createAndDeleteFile(dirDestination, dirOrigin, filename, content, timeBuffer);
    }
    private void createAndDeleteFile(File dirOrigin, File dirDestination, String filename, String content, long timeBuffer) throws IOException {
        // Create a file in origin
        File originFile = new File(dirOrigin, filename);
        File destinationFile = new File(dirDestination, filename);
        Assert.assertFalse("The destinationFile already exists", destinationFile.exists());
        Files.write(originFile.toPath(),content.getBytes());
        Assert.assertTrue("Could not create new originFile", originFile.createNewFile());
        try {
            Thread.sleep(timeBuffer);
        } catch (InterruptedException ex) {}
        Assert.assertTrue("The destinationFile does not exist", destinationFile.exists());
        FileReader reader = new FileReader(destinationFile);
        char[] buffer = new char[content.length()];
        reader.read(buffer);
        reader.close();
        Assert.assertTrue("The contents of destinationFile and originFile do not match", String.valueOf(buffer).equals(content));

        // Delete a file in origin
        Files.delete(originFile.toPath());
        try {
            Thread.sleep(timeBuffer);
        } catch (InterruptedException ex) {}
        Assert.assertFalse("The destinationFile is still existing", destinationFile.exists());
    }
}