/*
This file is part of Syncopto. ? 2015 Simon Lammer (lammer.simon@gmail.com)

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

import com.github.simonlammer.syncopto.logic.BufferedDirectorySynchronizer;
import com.github.simonlammer.syncopto.logic.DirectoryChangeWatcher;
import com.github.simonlammer.syncopto.logic.DirectoryChangeWatcherFactory;
import com.github.simonlammer.syncopto.tests.utils.RWeTestdirectoryTest;
import com.github.simonlammer.syncopto.tests.utils.TestdirectoryTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class BasicDirectoryChangeWatcherTest extends RWeTestdirectoryTest {
    private static final int WATCHSERVICE_REACTION_TIME = 5;
    private TestConsumer consumer;
    private File file;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Test
    public void createFileTest() {
        DirectoryChangeWatcher watcher = createAndStartWatcher(testDirectory.getDirectory().toPath());

        createTestFile();
        timeout();
        watcher.stopWatching();

        assertEquals(DirectoryChangeWatcher.ActionType.CREATE, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);
    }

    @Test
    public void modifyFileTest() {
        createTestFile();
        DirectoryChangeWatcher watcher = createAndStartWatcher(testDirectory.getDirectory().toPath());

        modifyTestFile();
        timeout();
        watcher.stopWatching();

        assertEquals(DirectoryChangeWatcher.ActionType.MODIFY, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);
    }

    @Test
    public void deleteFileTest() {
        createTestFile();
        DirectoryChangeWatcher watcher = createAndStartWatcher(testDirectory.getDirectory().toPath());

        deleteTestFile();
        timeout();
        watcher.stopWatching();

        assertEquals(DirectoryChangeWatcher.ActionType.DELETE, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);
    }

    @Test
    public void multipleEventsTest() {
        DirectoryChangeWatcher watcher = createAndStartWatcher(testDirectory.getDirectory().toPath());

        createTestFile();
        timeout();
        assertEquals(DirectoryChangeWatcher.ActionType.CREATE, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);

        modifyTestFile();
        timeout();
        assertEquals(DirectoryChangeWatcher.ActionType.MODIFY, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);

        deleteTestFile();
        timeout();
        assertEquals(DirectoryChangeWatcher.ActionType.DELETE, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);

        watcher.stopWatching();
    }

    @Test
    public void multipleFilesCreateModifyTest() {
        DirectoryChangeWatcher watcher = createAndStartWatcher(testDirectory.getDirectory().toPath());

        createTestFile();
        timeout();
        assertEquals(DirectoryChangeWatcher.ActionType.CREATE, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);

        File secondFile = testDirectory.createFile("secondFile", "RWe");
        timeout();
        assertEquals(DirectoryChangeWatcher.ActionType.CREATE, consumer.actionType);
        assertPathEqualsFilePath(secondFile, consumer.file);

        modifyTestFile();
        timeout();
        assertEquals(DirectoryChangeWatcher.ActionType.MODIFY, consumer.actionType);
        assertPathEqualsFilePath(consumer.file);

        watcher.stopWatching();
        secondFile.delete();
    }

    private DirectoryChangeWatcher createAndStartWatcher(Path dir) {
        DirectoryChangeWatcher watcher = DirectoryChangeWatcherFactory.createDirectoryChangeWatcher(dir);
        consumer = new TestConsumer();
        watcher.setChangeHandler(consumer);
        watcher.startWatching();
        return watcher;
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

    private void assertPathEqualsFilePath(File testFile) {
        assertPathEqualsFilePath(this.file, testFile);
    }

    private void assertPathEqualsFilePath(File expected, File testFile) {
        Path filePath = expected.toPath();
        Path dirPath = testDirectory.getDirectory().toPath();
        Assert.assertEquals(filePath.getFileName(), testFile.toPath().getFileName());
        Assert.assertEquals(filePath.toAbsolutePath(), dirPath.resolve(testFile.toPath()).toAbsolutePath());
    }

    private class TestConsumer implements BiConsumer<DirectoryChangeWatcher.ActionType, File> {

        public DirectoryChangeWatcher.ActionType actionType;
        public File file;

        @Override
        public void accept(DirectoryChangeWatcher.ActionType actionType, File file) {
            this.actionType = actionType;
            this.file = file;
        }
    }
}