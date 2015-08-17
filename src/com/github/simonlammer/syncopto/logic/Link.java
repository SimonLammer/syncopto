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

package com.github.simonlammer.syncopto.logic;

import com.github.simonlammer.syncopto.logic.filters.FilterManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Link {
    private File originDirectory;
    private DirectoryChangeWatcher originWatcher;
    private File destinationDirectory;
    private DirectoryChangeWatcher destinationWatcher;
    private FilterManager<File> filterManager;
    private DirectorySynchronizer directorySynchronizer;

    public Link(LinkBuilder builder) {
        throwExceptionOnNullValues(builder);
        assignFields(builder);
        setChangedHandlers();
    }

    private void throwExceptionOnNullValues(LinkBuilder builder) {
        if (builder.getOriginDirectory() == null) {
            throw new IllegalArgumentException("originDirectory must not be null");
        } else if (builder.getDestinationDirectory() == null) {
            throw new IllegalArgumentException("destinationDirectory must not be null");
        } else if (builder.getOriginWatcher() == null) {
            throw new IllegalArgumentException("originWatcher must not be null");
        } else if (builder.getDestinationWatcher() == null) {
            throw new IllegalArgumentException("destinationWatcher must not be null");
        } else if (builder.getDirectorySynchronizer() == null) {
            throw new IllegalArgumentException("directorySynchronizer must not be null");
        } else if (builder.getFilterManager() == null) {
            throw new IllegalArgumentException("filterManager must not be null");
        }
    }

    private void assignFields(LinkBuilder builder) {
        this.originDirectory = builder.getOriginDirectory();
        this.originWatcher = builder.getOriginWatcher();
        this.destinationDirectory = builder.getDestinationDirectory();
        this.destinationWatcher = builder.getDestinationWatcher();
        this.filterManager = builder.getFilterManager();
        this.directorySynchronizer = builder.getDirectorySynchronizer();
    }

    private void setChangedHandlers() {
        originWatcher.setChangeHandler(this::handleChangeInOrigin);
        destinationWatcher.setChangeHandler(this::handleChangeInDestination);
    }

    private void handleChangeInOrigin(DirectoryChangeWatcher.ActionType actionType, File file) {
        handleChange(actionType, file, originDirectory, destinationDirectory);
    }

    private void handleChangeInDestination(DirectoryChangeWatcher.ActionType actionType, File file) {
        handleChange(actionType, file, destinationDirectory, originDirectory);
    }

    private void handleChange(DirectoryChangeWatcher.ActionType actionType, File file, File originDirectory, File destinationDirectory) {
        try {
            switch(actionType) {
                case CREATE:
                    handleFileCreation(file, originDirectory, destinationDirectory);
                    break;
                case MODIFY:
                    handleFileModification(file, originDirectory, destinationDirectory);
                    break;
                case DELETE:
                    handleFileDeletion(file, originDirectory, destinationDirectory);
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex);
        }
    }

    private void handleFileCreation(File file, File originDirectory, File destinationDirectory) throws IOException {
        Path existing = file.toPath();
        File relativizedFile = originDirectory.toPath().relativize(existing).toFile();
        File destinationFile = new File(destinationDirectory, relativizedFile.getPath());
        Path link = destinationFile.toPath();
        Files.createLink(link, existing);
    }

    private void handleFileModification(File file, File originDirectory, File destinationDirectory) throws IOException {
        File relativizedFile = originDirectory.toPath().relativize(file.toPath()).toFile();
        File destinationFile = new File(destinationDirectory, relativizedFile.getPath());
        if (!filesAreLinked(file, destinationFile)) {
            boolean couldDelete = destinationFile.delete();
            if (!couldDelete) {
                throw new IllegalStateException("Could not delete '" + destinationFile.getAbsolutePath() + "'");
            }
            Files.createLink(destinationFile.toPath(), file.toPath());
        }
    }

    private boolean filesAreLinked(File a, File b) {
        if (!a.canWrite() || !b.canWrite()) {
            throw new IllegalStateException("Can not check whether files are linked, due to lack of write permissions");
        }
        boolean filesAreLinked = false;
        if (a.lastModified() == b.lastModified()) {
            long lastModified = a.lastModified();
            long modifiedLastModified = lastModified - 10000;
            a.setLastModified(modifiedLastModified);
            if (b.lastModified() == modifiedLastModified) {
                filesAreLinked = true;
            }
            a.setLastModified(lastModified);
        }
        return filesAreLinked;
    }

    private void handleFileDeletion(File file, File originDirectory, File destinationDirectory) throws IOException {
        Path deleted = file.toPath();
        File relativizedFile = originDirectory.toPath().relativize(deleted).toFile();
        File destinationFile = new File(destinationDirectory, relativizedFile.getPath());
        boolean couldDelete = destinationFile.delete();
        if (!couldDelete) {
            throw new IllegalStateException("Could not delete '" + destinationFile.getAbsolutePath() + "'");
        }
    }

    public void start() {
        directorySynchronizer.synchronizeDirectories();
        originWatcher.startWatching();
        destinationWatcher.startWatching();
    }

    public void stop() {
        originWatcher.stopWatching();
        destinationWatcher.stopWatching();
    }

    public FilterManager<File> getFilterManager() {
        return filterManager;
    }
}
