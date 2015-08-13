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

package com.github.simonlammer.syncopto.tests.learningtests;

import com.github.simonlammer.syncopto.tests.utils.RWeTestdirectoryTest;
import org.junit.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.Assert.*;

public class WatchService_Test extends RWeTestdirectoryTest {
    private static final int WATCHSERVICE_REACTION_TIME = 5;
    private WatchKey key;
    private WatchService watcher;
    private File file;

    @Before
    public void setUp() {
        obtainWatchService();
        registerWatcherToTestdirectory();
    }

    private void obtainWatchService() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            fail("Threw IOException while obtaining WatchService" + e.getMessage());
        }
        Assert.assertNotNull(watcher);
    }

    private void registerWatcherToTestdirectory() {
        try {
            Path directoryPath = testDirectory.getDirectory().toPath();
            WatchEvent.Kind[] kinds = {
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            };
            key = directoryPath.register(watcher, kinds);
        } catch (IOException e) {
            Assert.fail("Threw IOException while registering watcher: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        cancelWatchKey();
        closeWatchService();
        deleteTestFileIfExists();
    }

    private void cancelWatchKey() {
        if (key != null) {
            key.cancel();
        }
    }

    private void closeWatchService() {
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException e) {
                Assert.fail("WatchService threw IOException while closing: " + e.getMessage());
            }
        }
    }

    @Test
    public void noEventsOnNewWatchServiceTest() {
        List<WatchEvent<?>> watchEvents = key.pollEvents();
        Assert.assertEquals(0, watchEvents.size());
    }

    @Test
    public void fileCreationEventsTest() {
        createTestFile();
        timeout();
        assertLastWatchEventKindEquals(StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void fileCreationFileTest() {
        createTestFile();
        timeout();
        assertLastWatchEventPathEqualsFilePath();
    }

    @Test
    public void fileModificationEventsTest() {
        createTestFileWithoutEvents();
        modifyTestFile();
        timeout();
        assertLastWatchEventKindEquals(StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Test
    public void fileModificationFileTest() {
        createTestFileWithoutEvents();
        modifyTestFile();
        timeout();
        assertLastWatchEventPathEqualsFilePath();
    }

    @Test
    public void fileDeletionEventsTest() {
        createTestFileWithoutEvents();
        deleteTestFile();
        timeout();
        assertLastWatchEventKindEquals(StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void fileDeletionFileTest() {
        createTestFileWithoutEvents();
        deleteTestFile();
        timeout();
        assertLastWatchEventPathEqualsFilePath();
    }

    @Test
    public void renameFileEventsTest() {
        createTestFileWithoutEvents();
        this.file = renameFile();
        timeout();

        List<WatchEvent<?>> events = key.pollEvents();

        assertEquals(2, events.size());
        assertWatchEventKindEquals(events.get(0), StandardWatchEventKinds.ENTRY_DELETE);
        assertWatchEventKindEquals(events.get(1), StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void renameFileFilesTest() {
        createTestFileWithoutEvents();
        File renamedFile = renameFile();
        timeout();

        List<WatchEvent<?>> events = key.pollEvents();

        assertEquals(2, events.size());
        assertWatchEventPathEqualsFilePath(events.get(0));
        this.file = renamedFile;
        assertWatchEventPathEqualsFilePath(events.get(1));
    }

    private File renameFile() {
        File newFile = new File(file.getParentFile(), "renamedFile");
        boolean couldRenameFile = file.renameTo(newFile);
        assertTrue("Could not rename file", couldRenameFile);
        return newFile;
    }

    private void createTestFileWithoutEvents() {
        createTestFile();
        timeout();
        clearEvents();
    }

    private void assertLastWatchEventKindEquals(WatchEvent.Kind kind) {
        assertWatchEventKindEquals(getLastWatchEvent(), kind);
    }

    private void assertWatchEventKindEquals(WatchEvent event, WatchEvent.Kind kind) {
        WatchEvent.Kind eventKind = event.kind();
        assertEquals("Eventkinds do not match", kind, eventKind);
    }

    private WatchEvent<?> getLastWatchEvent() {
        List<WatchEvent<?>> watchEvents = key.pollEvents();
        assertFalse("No last WatchEvent existing", watchEvents.size() == 0);
        WatchEvent<?> event = watchEvents.get(watchEvents.size() - 1);
        return event;
    }

    private void assertLastWatchEventPathEqualsFilePath() {
        WatchEvent event = getLastWatchEvent();
        assertWatchEventPathEqualsFilePath(event);
    }

    private void assertWatchEventPathEqualsFilePath(WatchEvent event) {
        Path path = (Path)event.context();
        assertPathEqualsFilePath(path);
    }

    private void assertPathEqualsFilePath(Path path) {
        Path filePath = file.toPath();
        Path dirPath = testDirectory.getDirectory().toPath();
        Assert.assertEquals(filePath.getFileName(), path.getFileName());
        Assert.assertEquals(filePath, dirPath.resolve(path));
    }

    private void createTestFile() {
        file = testDirectory.createFile("file", "RWe");
    }

    private void modifyTestFile() {
        try {
            FileWriter fw = new FileWriter(file);
            fw.append('x');
            fw.close();
        } catch (IOException e) {
            Assert.fail("Threw IOException while modifiing file: " + e.getMessage());
        }
    }

    private void deleteTestFile() {
        Assert.assertTrue(file.delete());
    }

    private void deleteTestFileIfExists() {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    private void timeout() {
        timeout(WATCHSERVICE_REACTION_TIME);
    }

    private void timeout(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Assert.fail("Threw InterruptedException while sleeping: " + e.getMessage());
        }
    }

    private void clearEvents() {
        key.pollEvents();
        Assert.assertTrue("The key is no longer valid", key.reset());
    }
}
