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

package com.github.simonlammer.syncopto.tests.utils;

import javafx.util.Pair;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class TestDirectory {
    private File directory;
    private boolean closed = false;

    public TestDirectory(String directoryName, String permissions) {
        throwExceptionIfPermissionsAreInvalid(permissions);
        this.directory = createDirectory(directoryName);
        throwExceptionOnInsufficientPermissions(directory, permissions);
    }

    private void throwExceptionIfPermissionsAreInvalid(String permissions) {
        if (!Pattern.matches("^[rR][wW][eE]$", permissions)) {
            throw new IllegalArgumentException("Invalid permissions format: '" + permissions + "'");
        }
    }

    private void throwExceptionIfFileExists(File file) {
        if (file.exists()) {
            throw new IllegalStateException("There is a existing directory at '" + file.getAbsolutePath() + "' already!");
        }
    }

    private void throwExceptionOnInsufficientPermissions(File file, String permissions) {
        if (permissions.charAt(0) == 'R' && !file.canRead()) {
            throw new IllegalStateException("Insufficient read permissions: " + file.getAbsolutePath());
        }
        if (permissions.charAt(1) == 'W' && !file.canWrite()) {
            throw new IllegalStateException("Insufficient write permissions: " + file.getAbsolutePath());
        }
        if (permissions.charAt(2) == 'E' && !file.canExecute()) {
            throw new IllegalStateException("Insufficient execute permissions: " + file.getAbsolutePath());
        }
    }

    private void throwExceptionIfClosed() {
        if (closed) {
            throw new IllegalStateException("The TestDirectory instance has already been closed");
        }
    }

    public File createDirectory(String directoryName, String permissions) {
        throwExceptionIfClosed();
        throwExceptionIfPermissionsAreInvalid(permissions);
        File directory = createDirectory(directoryName);
        throwExceptionOnInsufficientPermissions(directory, permissions);
        return directory;
    }

    private File createDirectory(String directoryName) {
        File directory = new File(this.directory, directoryName);
        throwExceptionIfFileExists(directory);
        if (!directory.mkdirs()) {
            throw new IllegalStateException("Could not create directory at '" + directory.getAbsolutePath() + "'");
        }
        return directory;
    }

    public File[] createDirectories(Pair<String, String> ... directoryNamePermissionPairs) {
        File[] files = new File[directoryNamePermissionPairs.length];
        for (int i = 0; i < directoryNamePermissionPairs.length; i++) {
            Pair<String, String> directoryPermissionPair = directoryNamePermissionPairs[i];
            String directoryname = directoryPermissionPair.getKey();
            String permissions = directoryPermissionPair.getValue();
            files[i] = createDirectory(directoryname, permissions);
        }
        return files;
    }

    public File[] createDirectories(String permissions, String ... directoryNames) {
        File[] files = new File[directoryNames.length];
        for (int i = 0; i < directoryNames.length; i++) {
            files[i] = createFile(directoryNames[i], permissions);
        }
        return files;
    }

    public File createFile(String filename, String permissions) {
        throwExceptionIfPermissionsAreInvalid(permissions);
        File file = new File(directory, filename);
        createFile(file);
        throwExceptionOnInsufficientPermissions(file, permissions);
        return file;
    }

    private void createFile(File file) {
        throwExceptionIfClosed();
        try {
            if (!file.createNewFile()) {
                throw new IllegalStateException("File already exists: " + file.getAbsolutePath());
            }
        } catch (IOException ex) {
            throw new IllegalStateException("IOException occured");
        }
    }

    public File[] createFiles(Pair<String, String> ... filenamePermissionPairs) {
        File[] files = new File[filenamePermissionPairs.length];
        for (int i = 0; i < filenamePermissionPairs.length; i++) {
            Pair<String, String> filePermissionPair = filenamePermissionPairs[i];
            String filename = filePermissionPair.getKey();
            String permissions = filePermissionPair.getValue();
            files[i] = createFile(filename, permissions);
        }
        return files;
    }

    public File[] createFiles(String permissions, String ... filenames) {
        File[] files = new File[filenames.length];
        for (int i = 0; i < filenames.length; i++) {
            files[i] = createFile(filenames[i], permissions);
        }
        return files;
    }

    public void close() {
        if (!directory.delete()) {
            throw new InvalidStateException("Could not delete directory '" + directory.getAbsolutePath() + "'");
        }
        closed = true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!closed) {
            close();
        }
    }

    public File getDirectory() {
        throwExceptionIfClosed();
        return directory;
    }
}
