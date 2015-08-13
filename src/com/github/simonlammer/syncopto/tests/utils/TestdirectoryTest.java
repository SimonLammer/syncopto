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

import org.junit.Before;
import org.junit.After;

public abstract class TestdirectoryTest {
    protected TestDirectory testDirectory;

    protected abstract String determineTestdirectoryPermissions();

    @Before
    public void createTestDirectory() {
        String directoryName = determineTestdirectoryName();
        String directoryPermissions = determineTestdirectoryPermissions();
        testDirectory = new TestDirectory(directoryName, directoryPermissions);
    }

    private String determineTestdirectoryName() {
        return this.getClass().getSimpleName() + "-Testdirectory";
    }

    @After
    public void deleteTestDirectory() {
        testDirectory.close();
    }
}
