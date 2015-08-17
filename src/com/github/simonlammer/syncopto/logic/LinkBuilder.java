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

public class LinkBuilder {
    private File originDirectory;
    private DirectoryChangeWatcher originWatcher;
    private File destinationDirectory;
    private DirectoryChangeWatcher destinationWatcher;
    private FilterManager<File> filterManager;
    private DirectorySynchronizer directorySynchronizer;

    public Link buildLink() {
        return new Link(this);
    }

    public File getOriginDirectory() {
        return originDirectory;
    }

    public LinkBuilder setOriginDirectory(File originDirectory) {
        this.originDirectory = originDirectory;
        return this;
    }

    public DirectoryChangeWatcher getOriginWatcher() {
        return originWatcher;
    }

    public LinkBuilder setOriginWatcher(DirectoryChangeWatcher originWatcher) {
        this.originWatcher = originWatcher;
        return this;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public LinkBuilder setDestinationDirectory(File destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
        return this;
    }

    public DirectoryChangeWatcher getDestinationWatcher() {
        return destinationWatcher;
    }

    public LinkBuilder setDestinationWatcher(DirectoryChangeWatcher destinationWatcher) {
        this.destinationWatcher = destinationWatcher;
        return this;
    }

    public FilterManager<File> getFilterManager() {
        return filterManager;
    }

    public LinkBuilder setFilterManager(FilterManager<File> filterManager) {
        this.filterManager = filterManager;
        return this;
    }

    public DirectorySynchronizer getDirectorySynchronizer() {
        return directorySynchronizer;
    }

    public LinkBuilder setDirectorySynchronizer(DirectorySynchronizer directorySynchronizer) {
        this.directorySynchronizer = directorySynchronizer;
        return this;
    }
}
