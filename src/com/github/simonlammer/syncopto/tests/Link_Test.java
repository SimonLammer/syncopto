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

import com.github.simonlammer.syncopto.logic.*;
import com.github.simonlammer.syncopto.logic.filters.BasicFilter;
import com.github.simonlammer.syncopto.logic.filters.Filter;
import com.github.simonlammer.syncopto.logic.filters.FilterManager;
import com.github.simonlammer.syncopto.tests.utils.OriginDestinationDirectoriesTest;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class Link_Test extends OriginDestinationDirectoriesTest {
    private Link link;
    private boolean directorySynchronizationTriggered;
    private FakeDirectoryChangeWatcher originWatcher;
    private FakeDirectoryChangeWatcher destinationWatcher;

    @Before
    public void createLink() {
        DirectorySynchronizer directorySynchronizer = createDirectorySynchronizer();
        originWatcher = new FakeDirectoryChangeWatcher();
        destinationWatcher = new FakeDirectoryChangeWatcher();
        LinkBuilder builder = new LinkBuilder()
                .setOriginDirectory(originDirectory)
                .setDestinationDirectory(destinationDirectory)
                .setOriginWatcher(originWatcher)
                .setDestinationWatcher(destinationWatcher)
                .setDirectorySynchronizer(directorySynchronizer)
                .setFilterManager(new FilterManager<>());
        this.link = builder.buildLink();
    }

    private DirectorySynchronizer createDirectorySynchronizer() {
        Consumer<File> fileDeleter = (File f) -> {
            File file = new File(originDirectory, f.getPath());
            file.delete();
        };
        BasicFilter<File> filter = createFilter();
        DirectorySynchronizerBuilder directorySynchronizerBuilder = new DirectorySynchronizerBuilder()
                .setOriginDirectory(originDirectory)
                .setDestinationDirectory(destinationDirectory)
                .setNewFileInOriginHandler(fileDeleter)
                .setNewFileInDestinationHandler(fileDeleter)
                .setFilesDivergedHandler(fileDeleter)
                .setFilter(filter);
        DirectorySynchronizer customDirectorySynchronizer = new DirectorySynchronizer(directorySynchronizerBuilder) {
            @Override
            public void synchronizeDirectories() {
                directorySynchronizationTriggered = true;
                super.synchronizeDirectories();
            }
        };
        return customDirectorySynchronizer;
    }

    private BasicFilter<File> createFilter() {
        FilterManager<File> filterManager = new FilterManager<>();
        return filterManager;
    }

    @Test
    public void directorySynchronizerCalledTest() {
        assertFalse(directorySynchronizationTriggered);
        link.start();
        assertTrue(directorySynchronizationTriggered);
    }

    @Test
    public void createOriginFileTest() {
        link.start();
        File originFile = testDirectory.createFile("origin/file", "RWe");
        originWatcher.fakeAction(DirectoryChangeWatcher.ActionType.CREATE, originFile);
        File destinationFile = new File(destinationDirectory, "file");

        assertTrue(destinationFile.exists());
        assertTrue(filesAreLinked(originFile, destinationFile));

        link.stop();
        assertTrue(originFile.exists());
        assertTrue(destinationFile.exists());
        deleteFiles(originFile, destinationFile);
    }

    @Test
    public void createDestinationFileTest() {
        link.start();
        File destinationFile = testDirectory.createFile("destination/file", "RWe");
        destinationWatcher.fakeAction(DirectoryChangeWatcher.ActionType.CREATE, destinationFile);
        File originFile = new File(originDirectory, "file");

        assertTrue(originFile.exists());
        assertTrue(filesAreLinked(originFile, destinationFile));

        link.stop();
        assertTrue(originFile.exists());
        assertTrue(destinationFile.exists());
        deleteFiles(originFile, destinationFile);
    }

    @Test
    public void modifyOriginFileTest() throws IOException {
        File originFile = testDirectory.createFile("origin/file", "RWe");
        File destinationFile = Files.createLink(new File(destinationDirectory, "file").toPath(), originFile.toPath()).toFile();
        byte[] messageBytes = "Hello World!".getBytes(Charset.defaultCharset());

        link.start();
        Files.write(originFile.toPath(), messageBytes, StandardOpenOption.CREATE);
        originWatcher.fakeAction(DirectoryChangeWatcher.ActionType.MODIFY, originFile);
        assertArrayEquals(messageBytes, Files.readAllBytes(originFile.toPath()));
        assertTrue(filesAreLinked(originFile, destinationFile));

        link.stop();
        assertTrue(originFile.exists());
        assertTrue(destinationFile.exists());
        deleteFiles(originFile, destinationFile);
    }

    @Test
    public void modifyDestinationFileTest() throws IOException {
        File originFile = testDirectory.createFile("origin/file", "RWe");
        File destinationFile = Files.createLink(new File(destinationDirectory, "file").toPath(), originFile.toPath()).toFile();
        byte[] messageBytes = "Hello World!".getBytes();

        link.start();
        Files.write(destinationFile.toPath(), messageBytes, StandardOpenOption.CREATE);
        destinationWatcher.fakeAction(DirectoryChangeWatcher.ActionType.MODIFY, destinationFile);
        assertArrayEquals(messageBytes, Files.readAllBytes(destinationFile.toPath()));
        assertTrue(filesAreLinked(originFile, destinationFile));

        link.stop();
        assertTrue(originFile.exists());
        assertTrue(destinationFile.exists());
        deleteFiles(originFile, destinationFile);
    }

    @Test
    public void deleteOriginFileTest() throws IOException {
        File originFile = testDirectory.createFile("origin/file", "RWe");
        File destinationFile = Files.createLink(new File(destinationDirectory, "file").toPath(), originFile.toPath()).toFile();

        link.start();
        deleteFile(originFile);
        originWatcher.fakeAction(DirectoryChangeWatcher.ActionType.DELETE, originFile);
        assertFalse(destinationFile.exists());

        link.stop();
        assertFalse(originFile.exists());
        assertFalse(destinationFile.exists());
    }

    @Test
    public void deleteDestinationFileTest() throws IOException {
        File originFile = testDirectory.createFile("origin/file", "RWe");
        File destinationFile = Files.createLink(new File(destinationDirectory, "file").toPath(), originFile.toPath()).toFile();

        link.start();
        deleteFile(destinationFile);
        destinationWatcher.fakeAction(DirectoryChangeWatcher.ActionType.DELETE, destinationFile);
        assertFalse(originFile.exists());

        link.stop();
        assertFalse(originFile.exists());
        assertFalse(destinationFile.exists());
    }

    private void deleteFiles(File ... files) {
        for (File file : files) {
            deleteFile(file);
        }
    }

    private void deleteFile(File file) {
        assertTrue("Could not delete '" + file.getAbsolutePath(),
                file.delete()
        );
    }

    private boolean filesAreLinked(File a, File b) {
        boolean filesAreLinked = false;
        if (a.lastModified() == b.lastModified()) {
            long lastModified = a.lastModified();
            long modifiedLastModified = lastModified - 10000;
            assertTrue(a.setLastModified(modifiedLastModified));
            if (b.lastModified() == modifiedLastModified) {
                filesAreLinked = true;
            }
            assertTrue(a.setLastModified(lastModified));
        }
        return filesAreLinked;
    }

    private class FakeDirectoryChangeWatcher implements DirectoryChangeWatcher {
        private boolean watching;
        private BiConsumer<ActionType, File> handler;

        @Override
        public void startWatching() {
            watching = true;
        }

        @Override
        public void stopWatching() {
            watching = false;
        }

        @Override
        public void setChangeHandler(BiConsumer<ActionType, File> handler) {
            this.handler = handler;
        }

        public void fakeAction(ActionType actionType, File file) {
            if (watching) {
                handler.accept(actionType, file);
            }
        }
    }
}
