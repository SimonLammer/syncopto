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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class Link {
    private File origin; //directory
    private File destination; //directory
    private List<Filter> filters;
    private LinkMode mode;
    private String name;

    private Link(String name, LinkMode mode, Collection<Filter> filters) {
        setName(name);
        setMode(mode);
        setFilters(filters);
    }
    public Link(String name, LinkMode mode, String originDirectoryPath, String destinationDirectoryPath) { this(name, mode, originDirectoryPath, destinationDirectoryPath, null); }
    public Link(String name, LinkMode mode, String originDirectoryPath, String destinationDirectoryPath, Collection<Filter> filters) {
        this(name, mode, filters);
        setOriginDirectory(originDirectoryPath);
        setDestinationDirectory(destinationDirectoryPath);
    }
    public Link(String name, LinkMode mode, File originDirectory, File destinationDirectory) { this(name, mode, originDirectory, destinationDirectory, null); }
    public Link(String name, LinkMode mode, File originDirectory, File destinationDirectory, Collection<Filter> filters) {
        this(name, mode, filters);
        setOriginDirectory(originDirectory);
        setDestinationDirectory(destinationDirectory);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public File getDestinationDirectory() {
        return destination;
    }

    public List<Filter> getFilters() {
        return new ArrayList<>(filters); // return a clone of the list
    }

    public LinkMode getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public File getOriginDirectory() {
        return origin;
    }

    public void removeAllFilters() {
        filters.clear();
    }

    public boolean removeFilter(Filter filter) {
        return filters.remove(filter);
    }

    public Filter removeFilter(int index) {
        return filters.remove(index);
    }

    public boolean removeFilterIf(Predicate<? super Filter> predicate) {
        return filters.removeIf(predicate);
    }

    public boolean setDestinationDirectory(String dirPath) { return setDestinationDirectory(new File(dirPath)); }
    public boolean setDestinationDirectory(File dir) {
        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            this.destination = dir;
            return true;
        } else {
            return false;
        }
    }

    public void setMode(LinkMode mode) {
        this.mode = mode;
    }

    public boolean setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOriginDirectory(String dirPath) { return setOriginDirectory(new File(dirPath)); }
    public boolean setOriginDirectory(File dir) {
        if (dir.exists() && dir.isDirectory() && dir.canRead()) {
            this.origin = dir;
            return true;
        } else {
            return false;
        }
    }

    public void setFilters(Collection<Filter> filters) {
        this.filters = new ArrayList<>(filters);
    }

    public void upateFileLinks(){
        throw new NotImplementedException(); //TODO
    }
}