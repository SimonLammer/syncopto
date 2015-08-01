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

    /**
     * Simple private constructor to reduce code duplicates in other constructors
     * @param name Link's name
     * @param mode Link's mode
     * @param filters Collection of filters, which the Link uses to connect two directories
     */
    private Link(String name, LinkMode mode, Collection<Filter> filters) {
        setName(name);
        setMode(mode);
        setFilters(filters);
    }

    /**
     * This constructor does not set any filters.
     * @param name Link's name
     * @param mode Link's mode
     * @param originDirectoryPath Path to the origin directory as a String
     * @param destinationDirectoryPath Path to the destination directory as a String
     */
    public Link(String name, LinkMode mode, String originDirectoryPath, String destinationDirectoryPath) { this(name, mode, originDirectoryPath, destinationDirectoryPath, null); }

    /**
     *
     * @param name Link's name
     * @param mode Link's mode
     * @param originDirectoryPath Path to the origin directory as a String
     * @param destinationDirectoryPath Path to the destination directory as a String
     * @param filters Collection of filters, which the Link uses to connect two directories
     */
    public Link(String name, LinkMode mode, String originDirectoryPath, String destinationDirectoryPath, Collection<Filter> filters) {
        this(name, mode, filters);
        setOriginDirectory(originDirectoryPath);
        setDestinationDirectory(destinationDirectoryPath);
    }

    /**
     * This constructor does not set any filters.
     * @param name Link's name
     * @param mode Link's mode
     * @param originDirectory Origin directory
     * @param destinationDirectory Destination directory
     */
    public Link(String name, LinkMode mode, File originDirectory, File destinationDirectory) { this(name, mode, originDirectory, destinationDirectory, null); }

    /**
     *
     * @param name Link's name
     * @param mode Link's mode
     * @param originDirectory Origin directory
     * @param destinationDirectory Destination directory
     * @param filters Collection of filters, which the Link uses to connect two directories
     */
    public Link(String name, LinkMode mode, File originDirectory, File destinationDirectory, Collection<Filter> filters) {
        this(name, mode, filters);
        setOriginDirectory(originDirectory);
        setDestinationDirectory(destinationDirectory);
    }

    /**
     * Adds all Filters
     * @param filters Filters to be added
     */
    public void addAllFilters(Collection<Filter> filters) {
        filters.addAll(filters);
    }

    /**
     * Adds the Filter to the Link
     * @param filter Filter to be added
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    /**
     * Retrieves the destination directory
     * @return Destination directory
     */
    public File getDestinationDirectory() {
        return destination;
    }

    /**
     * Retrieves all Filters of the Link
     * @return All Link's Filters
     */
    public List<Filter> getFilters() {
        return new ArrayList<>(filters); // return a clone of the list
    }

    /**
     * Retrieves the mode
     * @return Mode
     */
    public LinkMode getMode() {
        return mode;
    }

    /**
     * Retrieves the name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the origin directory
     * @return Origin directory
     */
    public File getOriginDirectory() {
        return origin;
    }

    /**
     * Checkes if any of the Link's Filters selects the file
     * @param file File to be checked
     * @return Whether or not any of the Link's Filters select the file
     */
    public boolean isFileSelected(File file) {
        for (Filter filter : filters) {
            if (filter.isSelected(file)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all Filters from Link
     */
    public void removeAllFilters() {
        filters.clear();
    }

    /**
     * Removes a Filter from the Link
     * @param filter Filter to be removed
     * @return Whether or not the removal was successful
     */
    public boolean removeFilter(Filter filter) {
        return filters.remove(filter);
    }

    /**
     * Removes a Filter from the Link
     * @param index Index of the Filter to be removed
     * @return Whether or not the removal was successful
     */
    public Filter removeFilter(int index) {
        return filters.remove(index);
    }

    /**
     * Removes all Filters of the Link that match the criteria
     * @param predicate Criteria that determines whether a Filter gets removed
     * @return {@code true} if any elements were removed
     */
    public boolean removeFilterIf(Predicate<? super Filter> predicate) {
        return filters.removeIf(predicate);
    }

    /**
     * Start watching the directories using a WatchService to quickly react to chanages.
     */
    public void startWatch() {
        throw new NotImplementedException(); // TODO: implement method
    }

    /**
     * Sets the destination directory
     * @param dirPath Path of to the new destination directory as a String
     * @return Whether dirPath leads to a directory in which the the Link is granted writing permissions
     */
    public boolean setDestinationDirectory(String dirPath) { return setDestinationDirectory(new File(dirPath)); }

    /**
     * Sets the destination directory
     * @param dir New destination directory
     * @return Whether dirPath leads to a directory in which the the Link is granted writing permissions
     */
    public boolean setDestinationDirectory(File dir) {
        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            this.destination = dir;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes all current filters and adds the passed filters afterwards
     * @param filters New Filters
     */
    public void setFilters(Collection<Filter> filters) {
        removeAllFilters();
        addAllFilters(filters);
    }

    /**
     * Sets the mode
     * @param mode New Mode
     */
    public void setMode(LinkMode mode) {
        this.mode = mode;
    }

    /**
     * Sets the name
     * @param name New name
     * @return Whether the name is valid (Neither null nor empty)
     */
    public boolean setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the origin directory
     * @param dirPath Path of to the new origin directory as a String
     * @return Whether dirPath leads to a directory in which the the Link is granted reading permissions
     */
    public boolean setOriginDirectory(String dirPath) { return setOriginDirectory(new File(dirPath)); }

    /**
     * Sets the origin directory
     * @param dir New origin directory
     * @return Whether dirPath leads to a directory in which the the Link is granted reading permissions
     */
    public boolean setOriginDirectory(File dir) {
        if (dir.exists() && dir.isDirectory() && dir.canRead()) {
            this.origin = dir;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Stop watching the directories.
     * This link will no longer react to changes.
     * Call updateFileLinks() to update the connection.
     */
    public void stopWatch() {
        throw new NotImplementedException(); // TODO: implement method
    }

    /**
     * Updates the connection between origin and destination directory.
     */
    public void upateFileLinks(){
        throw new NotImplementedException(); //TODO
    }
}