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
import java.util.function.Consumer;

public class DirectorySynchronizerBuilder {
    private File originDirectory;
    private File destinationDirectory;
    private Consumer<File> newFileInOriginHandler;
    private Consumer<File> newFileInDestinationHandler;
    private Consumer<File> filesDivergedHandler;
    private Filter<File> filter;

    public DirectorySynchronizer buildDirectorySynchronizer() {
        return new DirectorySynchronizer(this);
    }

    public DirectorySynchronizerBuilder setOriginDirectory(File directory) {
        this.originDirectory = directory;
        return this;
    }

    public DirectorySynchronizerBuilder setDestinationDirectory(File directory) {
        this.destinationDirectory = directory;
        return this;
    }

    public DirectorySynchronizerBuilder setNewFileInOriginHandler(Consumer<File> handler) {
        this.newFileInOriginHandler = handler;
        return this;
    }

    public DirectorySynchronizerBuilder setNewFileInDestinationHandler(Consumer<File> handler) {
        this.newFileInDestinationHandler = handler;
        return this;
    }

    public DirectorySynchronizerBuilder setFilesDivergedHandler(Consumer<File> handler) {
        this.filesDivergedHandler = handler;
        return this;
    }

    public DirectorySynchronizerBuilder setFilter(Filter<File> filter) {
        this.filter = filter;
        return this;
    }

    public File getOriginDirectory() {
        return originDirectory;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public Consumer<File> getNewFileInOriginHandler() {
        return newFileInOriginHandler;
    }

    public Consumer<File> getNewFileInDestinationHandler() {
        return newFileInDestinationHandler;
    }

    public Consumer<File> getFilesDivergedHandler() {
        return filesDivergedHandler;
    }

    public Filter<File> getFilter() {
        return filter;
    }
}
