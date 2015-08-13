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

package com.github.simonlammer.syncopto.tests.filters;

import com.github.simonlammer.syncopto.logic.filters.IgnoreHiddenFilesFilter;
import com.github.simonlammer.syncopto.tests.utils.SingleRWeFileTest;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class IgnoreHiddenFilesFilter_Test extends SingleRWeFileTest {
    @Test
    public void unhiddenFileTest() {
        assertFalse(file.isHidden()); // file should NOT be hidden by default
        IgnoreHiddenFilesFilter filter = new IgnoreHiddenFilesFilter();
        assertTrue(filter.isSelected(file));
    }

    @Test
    public void hiddenFileTest() {
        //TODO properly hide file (in any OS!) instead of fakehiding
        file = new FakeHiddenFile(file); // fake hiding file
        assertTrue(file.isHidden());
        IgnoreHiddenFilesFilter filter = new IgnoreHiddenFilesFilter();
        assertFalse(filter.isSelected(file));
    }

    private class FakeHiddenFile extends File {
        public FakeHiddenFile(File originalfile) {
            super(originalfile.getAbsolutePath());
        }

        @Override
        public boolean isHidden() {
            return true;
        }
    }
}
