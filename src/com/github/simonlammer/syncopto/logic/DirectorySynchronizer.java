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

import com.github.simonlammer.syncopto.logic.filters.Filter;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class DirectorySynchronizer {
    private File originDirectory;
    private File destinationDirectory;
    private Consumer<File> newFileInOriginHandler;
    private Consumer<File> newFileInDestinationHandler;
    private Consumer<File> filesDivergedHandler;
    private Filter<File> filter;

    public DirectorySynchronizer(DirectorySynchronizerBuilder builder) {
        throwExceptionOnNullValues(builder);
        assignFields(builder);
    }

    private void throwExceptionOnNullValues(DirectorySynchronizerBuilder builder) {
        if (builder.getOriginDirectory() == null) {
            throw new IllegalArgumentException("originDirectory must not be null");
        } else if (builder.getDestinationDirectory() == null) {
            throw new IllegalArgumentException("destinationDirectory must not be null");
        } else if (builder.getNewFileInOriginHandler() == null) {
            throw new IllegalArgumentException("newFileInOriginHandler must not be null");
        } else if (builder.getNewFileInDestinationHandler() == null) {
            throw new IllegalArgumentException("newFileInDestinationHandler must not be null");
        } else if (builder.getFilesDivergedHandler() == null) {
            throw new IllegalArgumentException("filesDivergedHandler must not be null");
        } else if (builder.getFilter() == null) {
            throw new IllegalArgumentException("filter must not be null");
        }
    }

    private void assignFields(DirectorySynchronizerBuilder builder) {
        this.originDirectory = builder.getOriginDirectory();
        this.destinationDirectory = builder.getDestinationDirectory();
        this.newFileInOriginHandler = builder.getNewFileInOriginHandler();
        this.newFileInDestinationHandler = builder.getNewFileInDestinationHandler();
        this.filesDivergedHandler = builder.getFilesDivergedHandler();
        this.filter = builder.getFilter();
    }

    public void synchronizeDirectories() {
        synchronizeDirectories(originDirectory, destinationDirectory, newFileInOriginHandler, false);
        synchronizeDirectories(destinationDirectory, originDirectory, newFileInDestinationHandler, true);
    }

    private void synchronizeDirectories(File originDirectory, File destinationDirectory, Consumer<File> newFileHandler, boolean handleDivergedFiles) {
        File[] originFiles = originDirectory.listFiles();
        for (int originFilesIndex = 0; originFilesIndex < originFiles.length; originFilesIndex++) {
            File originFile = originFiles[originFilesIndex];
            File relativizedFile = originDirectory.toPath().relativize(originFile.toPath()).toFile();
            File destinationFile = new File(destinationDirectory, relativizedFile.getPath());
            if (filter.isSelected(originFile)) {
                if (destinationFile.exists()) {
                    if (originFile.isDirectory() && destinationFile.isDirectory()) {
                        synchronizeDirectories(originFile, destinationFile, newFileHandler, handleDivergedFiles);
                    } else if (handleDivergedFiles && !filesAreLinked(originFile, destinationFile)) {
                        filesDivergedHandler.accept(relativizedFile);
                    }
                } else {
                    newFileHandler.accept(relativizedFile);
                }
            }
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
}
