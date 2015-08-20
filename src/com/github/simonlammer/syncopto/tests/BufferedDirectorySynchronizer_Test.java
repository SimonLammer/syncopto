package com.github.simonlammer.syncopto.tests;

import com.github.simonlammer.syncopto.logic.BufferedDirectorySynchronizer;
import com.github.simonlammer.syncopto.logic.filters.FilterManager;
import com.github.simonlammer.syncopto.tests.utils.OriginDestinationDirectoriesTest;
import com.github.simonlammer.syncopto.logic.BufferedDirectorySynchronizer.ActionType;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.Buffer;
import java.util.Collection;

import static org.junit.Assert.*;

public class BufferedDirectorySynchronizer_Test extends OriginDestinationDirectoriesTest {
    private BufferedDirectorySynchronizer bufferedDirectorySynchronizer;
    private Collection<Pair<ActionType, File>> events;

    @Before
    public void createBufferedDirectorySynchronizer() {
        bufferedDirectorySynchronizer = new BufferedDirectorySynchronizer(originDirectory, destinationDirectory,
                new FilterManager<>(), (events) -> {
            BufferedDirectorySynchronizer_Test.this.events = events;
        });
    }

    @Test
    public void noActionTest() {
        bufferedDirectorySynchronizer.synchronizeDirectories();
        assertEquals(0, events.size());
    }

    @Test
    public void newFileInOriginTest() {
        File file = testDirectory.createFile("origin/file", "RWe");
        bufferedDirectorySynchronizer.synchronizeDirectories();
        assertEquals(1, events.size());
        Pair<ActionType, File> pair = events.iterator().next();
        ActionType expectedType = ActionType.NEW_FILE_IN_ORIGIN;
        ActionType actualType = pair.getKey();
        File actualFile = new File(originDirectory, pair.getValue().getPath());
        assertEquals(expectedType, actualType);
        assertEquals(file, actualFile);
        assertTrue(file.delete());
    }

    @Test
    public void newFileInDestinationTest() {
        File file = testDirectory.createFile("destination/file", "RWe");
        bufferedDirectorySynchronizer.synchronizeDirectories();
        assertEquals(1, events.size());
        Pair<ActionType, File> pair = events.iterator().next();
        ActionType expectedType = ActionType.NEW_FILE_IN_DESTINATION;
        ActionType actualType = pair.getKey();
        File actualFile = new File(destinationDirectory, pair.getValue().getPath());
        assertEquals(expectedType, actualType);
        assertEquals(file, actualFile);
        assertTrue(file.delete());
    }

    @Test
    public void filesDivergedTest() {
        File originFile = testDirectory.createFile("origin/file", "RWe");
        File destinationFile = testDirectory.createFile("destination/file", "RWe");
        bufferedDirectorySynchronizer.synchronizeDirectories();
        assertEquals(1, events.size());
        Pair<ActionType, File> pair = events.iterator().next();
        ActionType expectedType = ActionType.FILES_DIVERGED;
        ActionType actualType = pair.getKey();
        File actualOriginFile = new File(originDirectory, pair.getValue().getPath());
        File actualDestinationFile = new File(destinationDirectory, pair.getValue().getPath());
        assertEquals(expectedType, actualType);
        assertEquals(originFile, actualOriginFile);
        assertEquals(destinationFile, actualDestinationFile);
        assertTrue(originFile.delete());
        assertTrue(destinationFile.delete());
    }
}
