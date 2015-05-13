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

import org.junit.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class WatchService_Test {
    private static File dir;
    private static Path dirPath;
    private static File file;
    private static Path filePath;
    private static WatchKey key;
    private static WatchService watcher;

    @BeforeClass
    public static void setUp() {
        // obtain WatchService
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            Assert.fail("Threw IOException while obtaining WatchService" + e.getMessage());
        }
        Assert.assertNotNull(watcher);

        // create test directory
        dir = new File("testDirectory-WatchService_Test");
        if (dir.exists()) {
            Assert.fail("Test directory '" + dir.getAbsolutePath() + "' already exists, please delete the directory manually");
        }
        dir.mkdir();
        dirPath = dir.toPath();

        // register watcher to test directory
        try {
            key = dir.toPath().register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            Assert.fail("Threw IOException while registering watcher: " + e.getMessage());
        }

        // set file
        file = new File(dir,"file.file");
        filePath = file.toPath();
    }

    @AfterClass
    public static void tearDown() {
        // cancel WatchKey
        if (key != null) {
            key.cancel();
        }

        // close WatchService
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException e) {
                Assert.fail("WatchService threw IOException while closing: " + e.getMessage());
            }
        }

        // delete test directory
        if (dir.exists()) {
            try {
                Path filePath = file.toPath();
                Files.deleteIfExists(filePath);
                Path dirPath = dir.toPath();
                Files.delete(dirPath);
            } catch (IOException e) {
                Assert.fail("Threw IOException while deleting file: " + e.getMessage());
            }
        }
    }

    private static void createTestFile() {
        try {
            boolean createNewFile = file.createNewFile();
        } catch (IOException e) {
            Assert.fail("Threw IOException while creating file: " + e.getMessage());
        }
    }

    private static void modifyTestFile() {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write('x');
            fw.close();
        } catch (IOException e) {
            Assert.fail("Threw IOException while modifiing file: " + e.getMessage());
        }
    }

    private static void deleteTestFile() {
        Assert.assertTrue(file.delete());
    }

    private static void clearEvents() {
        key.pollEvents();
        Assert.assertTrue("The key is no longer valid", key.reset());
    }

    @Before
    public void deleteTestFileIfExisting() {
        if (file.exists()) {
            deleteTestFile();
        }
    }

    @Test
    public void simpleTest() {
        createTestFile();
        modifyTestFile();
        deleteTestFile();
    }

    @Test
    public void creationTest() {
        // clear any old events
        clearEvents();

        // create file
        createTestFile();

        // wait
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Assert.fail("Threw InterruptedException while sleeping: " + e.getMessage());
        }

        // check whether the right event occurred in watcher
        List<WatchEvent<?>> watchEvents = key.pollEvents();
        Assert.assertEquals(1, watchEvents.size()); // only one event should have occurred
        WatchEvent<?> event = watchEvents.get(0);
        WatchEvent.Kind kind = event.kind();
        Assert.assertEquals(StandardWatchEventKinds.ENTRY_CREATE.name(), kind.name()); // should be a create event

        // check whether the event occurred on the right file
        Path path = (Path)event.context();
        Assert.assertEquals(filePath.getFileName(), path.getFileName());
        Assert.assertEquals(filePath, dirPath.resolve(path));

        // delete file
        deleteTestFile();
    }

    @Test
    public void modificationTest() {
        createTestFile();

        // clear old events
        clearEvents();

        // modify the file
        try {
            FileWriter writer = new FileWriter(file);
            writer.append('x');
            writer.close();
        } catch (IOException e) {
            Assert.fail("Threw IOException while modifiing file: " + e.getMessage());
        }

        // wait
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Assert.fail("Threw InterruptedException while waiting: " + e.getMessage());
        }

        // check occurred events
        List<WatchEvent<?>> watchEvents = key.pollEvents();
        Assert.assertEquals(1, watchEvents.size()); // only one event should have occurred
        WatchEvent<?> event = watchEvents.get(0);
        WatchEvent.Kind kind = event.kind();
        Assert.assertEquals(StandardWatchEventKinds.ENTRY_MODIFY.name(), kind.name()); // should be a create event

        // delete file
        deleteTestFile();
    }

    @Test
    public void deletionTest() {
        // create file
        createTestFile();

        // clear any old events
        clearEvents();

        // delete file
        deleteTestFile();

        // wait
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Assert.fail("Threw InterruptedException while sleeping: " + e.getMessage());
        }

        // check whether the right events occurred in watcher
        List<WatchEvent<?>> watchEvents = key.pollEvents();
        Assert.assertEquals(2, watchEvents.size());

        // check modification event
        WatchEvent<?> event = watchEvents.get(0);
        WatchEvent.Kind kind = event.kind();
        Assert.assertEquals(StandardWatchEventKinds.ENTRY_MODIFY.name(), kind.name()); // should be a modify event

        // check whether the event occurred on the right file
        Path path = (Path)event.context();
        Assert.assertEquals(filePath.getFileName(), path.getFileName());
        Assert.assertEquals(filePath, dirPath.resolve(path));

        // check deletion event
        event = watchEvents.get(1);
        kind = event.kind();
        Assert.assertEquals(StandardWatchEventKinds.ENTRY_DELETE.name(), kind.name()); // should be a delete event

        // check whether the event occurred on the right file
        path = (Path)event.context();
        Assert.assertEquals(filePath.getFileName(), path.getFileName());
        Assert.assertEquals(filePath, dirPath.resolve(path));
    }
}
