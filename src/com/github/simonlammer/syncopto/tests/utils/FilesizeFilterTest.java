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

import com.github.simonlammer.syncopto.tests.utils.SingleRWeFileTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.fail;

public abstract class FilesizeFilterTest extends SingleRWeFileTest {
    protected void setFilesize(int size) {
        byte[] bytes = new byte[size];
        try {
            Files.write(file.toPath(), bytes, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            fail("Could not write to file");
        }
    }
}
