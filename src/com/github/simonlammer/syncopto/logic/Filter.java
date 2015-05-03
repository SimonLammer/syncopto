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
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Filter {
    private boolean checkHiddenFiles;
    private Pattern dirPattern;
    private Pattern pattern;
    private String name;

    private Filter(String name) {
        setName(name);
    }
    public Filter(String name, String patternRegex) {
        this(name, patternRegex, null, false);
    }
    public Filter(String name, String patternRegex, boolean checkHiddenFiles) {
        this(name, patternRegex, null, checkHiddenFiles);
    }
    public Filter(String name, String patternRegex, String directoryPatternRegex) {
        this(name, patternRegex, directoryPatternRegex, false);
    }
    public Filter(String name, String patternRegex, String directoryPatternRegex, boolean checkHiddenFiles) {
        this(name);
        setPattern(patternRegex);
        setDirectoryPattern(directoryPatternRegex);
        this.checkHiddenFiles = checkHiddenFiles;
    }
    public Filter(String name, Pattern pattern) {
        this(name, pattern, null);
    }
    public Filter(String name, Pattern pattern, boolean checkHiddenFiles) {
        this(name, pattern, null, checkHiddenFiles);
    }
    public Filter(String name, Pattern pattern, Pattern directoryPattern) {
        this(name, pattern, directoryPattern, false);
    }
    public Filter(String name, Pattern pattern, Pattern directoryPattern, boolean checkHiddenFiles) {
        this(name);
        setPattern(pattern);
        setDirectoryPattern(directoryPattern);
        this.checkHiddenFiles = checkHiddenFiles;
    }

    public Pattern getDirectoryPattern() {
        return dirPattern;
    }

    public String getName() {
        return name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Retrieves all files within the directory that pass the filter.
     * @param directory directory that get's examined
     * @return all files within the directory that pass the filter
     */
    public File[] getSelectedFiles(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Directory not found or does not exist: " + directory.getAbsolutePath());
        }
        List<File> files = new LinkedList<>();
        File[] dirFiles = directory.listFiles();
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                File[] subFiles = getSelectedFiles(file);
                Collections.addAll(files, subFiles);
            } else if(file.isFile()) {
                if (isSelected(file)) {
                    files.add(file);
                }
            } else {
                throw new IllegalStateException("The path '" + file.getAbsolutePath() + "' leads neither to a file nor a directory");
            }
        }
        return files.toArray(new File[files.size()]);
    }

    public boolean isCheckingHiddenFiles() {
        return checkHiddenFiles;
    }

    public boolean isRecursive() {
        return dirPattern != null;
    }

    /**
     * Determines whether the file passes the filter.
     * @param file Path to a file
     * @return true if the file passes the filter; false if the file is a directory or does not pass the filter.
     */
    public boolean isSelected(File file) {
        throw new NotImplementedException(); // TODO implement method
    }

    public void setCheckHiddenFiles(boolean newValue) {
        checkHiddenFiles = newValue;
    }

    public void setDirectoryPattern(Pattern pattern) {
        dirPattern = pattern;
    }

    public void setDirectoryPattern(String regex) {
        if (regex == null) {
            setDirectoryPattern((Pattern)null);
        } else {
            setDirectoryPattern(Pattern.compile(regex));
        }
    }

    public void setName(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name must not be null or empty!");
        }
        this.name = name;
    }

    public void setNonRecursive() {
        dirPattern = null;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String regex) {
        if (regex == null) {
            setPattern((Pattern) null);
        } else {
            setPattern(Pattern.compile(regex));
        }
    }
}
