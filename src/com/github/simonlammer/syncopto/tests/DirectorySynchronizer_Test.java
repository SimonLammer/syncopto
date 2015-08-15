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

import com.github.simonlammer.syncopto.logic.DirectorySynchronizer;
import com.github.simonlammer.syncopto.logic.DirectorySynchronizerBuilder;
import com.github.simonlammer.syncopto.logic.filters.FilterManager;
import com.github.simonlammer.syncopto.tests.utils.RWeTestdirectoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.*;

public class DirectorySynchronizer_Test extends RWeTestdirectoryTest {
    private File originDirectory;
    private File destinationDirectory;
    private DirectorySynchronizerBuilder directorySynchronizerBuilder;
    private boolean newFileInOriginHandlerTriggered;
    private boolean newFileInDestinationHandlerTriggered;
    private boolean filesDivergedHandlerTriggered;
    private File fileToHandle;

    @Before
    public void setUp() {
        createDirectories();
        createDirectorySynchronizerBuilder();
        resetHandlerTriggeredFlags();
        resetFileToHandle();
    }

    private void createDirectories() {
        originDirectory = testDirectory.createDirectory("origin", "RWe");
        destinationDirectory = testDirectory.createDirectory("destination", "RWe");
    }

    private void createDirectorySynchronizerBuilder() {
        directorySynchronizerBuilder = new DirectorySynchronizerBuilder()
                .setOriginDirectory(originDirectory)
                .setDestinationDirectory(destinationDirectory)
                .setFilter(new FilterManager<File>())
                .setNewFileInOriginHandler((File f) -> newFileInOriginHandlerTriggered = true)
                .setNewFileInDestinationHandler((File f) -> newFileInDestinationHandlerTriggered = true)
                .setFilesDivergedHandler((File f) -> filesDivergedHandlerTriggered = true);
    }

    private void resetHandlerTriggeredFlags() {
        newFileInOriginHandlerTriggered = false;
        newFileInDestinationHandlerTriggered = false;
        filesDivergedHandlerTriggered = false;
    }

    private void resetFileToHandle() {
        fileToHandle = new File("THIS FILE DOES NOT EXIST");
    }

    @After
    public void tearDown() {
        deleteDirectories();
    }

    private void deleteDirectories() {
        assertTrue(originDirectory.delete());
        assertTrue(destinationDirectory.delete());
    }

    @Test
    public void noActionTest() {
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        synchronizer.synchronizeDirectories();

        assertFalse(newFileInOriginHandlerTriggered);
        assertFalse(newFileInDestinationHandlerTriggered);
        assertFalse(filesDivergedHandlerTriggered);
    }

    @Test
    public void createNewFileInOrigin_HandlersTriggeredTest() {
        directorySynchronizerBuilder.setNewFileInOriginHandler((File file) -> {
            newFileInOriginHandlerTriggered = true;
        });
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        File createdFile = createFileInOrigin();
        synchronizer.synchronizeDirectories();

        assertTrue(newFileInOriginHandlerTriggered);
        assertFalse(newFileInDestinationHandlerTriggered);
        assertFalse(filesDivergedHandlerTriggered);
        assertTrue(createdFile.delete());
    }

    @Test
    public void createNewFileInOrigin_FilesMatchTest() {
        directorySynchronizerBuilder.setNewFileInOriginHandler((File file) -> {
            fileToHandle = file;
        });
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        File createdFile = createFileInOrigin();
        synchronizer.synchronizeDirectories();
        File obtainedFile = new File(originDirectory, fileToHandle.getPath());

        assertEquals(createdFile, obtainedFile);
        assertTrue(createdFile.delete());
    }

    @Test
    public void createNewFileInDestination_HandlersTriggeredTest() {
        directorySynchronizerBuilder.setNewFileInDestinationHandler((File file) -> {
            newFileInDestinationHandlerTriggered = true;
        });
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        File createdFile = createFileInDestination();
        synchronizer.synchronizeDirectories();

        assertTrue(newFileInDestinationHandlerTriggered);
        assertFalse(newFileInOriginHandlerTriggered);
        assertFalse(filesDivergedHandlerTriggered);
        assertTrue(createdFile.delete());
    }

    @Test
    public void createNewFileInDestination_FilesMatchTest() {
        directorySynchronizerBuilder.setNewFileInDestinationHandler((File file) -> {
            fileToHandle = file;
        });
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        File createdFile = createFileInDestination();
        synchronizer.synchronizeDirectories();
        File obtainedFile = new File(destinationDirectory, fileToHandle.getPath());

        assertEquals(createdFile, obtainedFile);
        assertTrue(createdFile.delete());
    }

    @Test
    public void filesDiverged_HandlersTriggeredTest() {
        directorySynchronizerBuilder.setFilesDivergedHandler((File file) -> {
            filesDivergedHandlerTriggered = true;
        });
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        File createdFileInOrigin = createFileInOrigin();
        File createdFileInDestination = createFileInDestination();
        modifyFile(createdFileInOrigin);
        synchronizer.synchronizeDirectories();

        assertTrue(filesDivergedHandlerTriggered);
        assertFalse(newFileInOriginHandlerTriggered);
        assertFalse(newFileInDestinationHandlerTriggered);
        assertTrue(createdFileInOrigin.delete());
        assertTrue(createdFileInDestination.delete());
    }

    @Test
    public void filesDiverged_FilesMatchTest() {
        directorySynchronizerBuilder.setFilesDivergedHandler((File file) -> {
            fileToHandle = file;
        });
        DirectorySynchronizer synchronizer = directorySynchronizerBuilder.buildDirectorySynchronizer();

        File createdFileInOrigin = createFileInOrigin();
        File createdFileInDestination = createFileInDestination();
        modifyFile(createdFileInOrigin);
        synchronizer.synchronizeDirectories();
        File obtainedFileInOrigin = new File(originDirectory, fileToHandle.getPath());
        File obtainedFileInDestination = new File(destinationDirectory, fileToHandle.getPath());

        assertEquals(createdFileInOrigin, obtainedFileInOrigin);
        assertEquals(createdFileInDestination, obtainedFileInDestination);
        assertTrue(createdFileInOrigin.delete());
        assertTrue(createdFileInDestination.delete());
    }

    private File createFileInOrigin() {
        return testDirectory.createFile("origin/file", "RWe");
    }

    private File createFileInDestination() {
        return testDirectory.createFile("destination/file", "RWe");
    }

    private void modifyFile(File file) {
        assertTrue(file.setLastModified(new Date().getTime() - 10000));
    }
}
