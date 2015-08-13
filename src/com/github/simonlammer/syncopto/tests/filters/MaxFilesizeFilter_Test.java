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

import com.github.simonlammer.syncopto.logic.filters.MaxFilesizeFilter;
import com.github.simonlammer.syncopto.tests.utils.FilesizeFilterTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxFilesizeFilter_Test extends FilesizeFilterTest {
    @Test
    public void smallEnoughTest() {
        test(5,1);
    }

    @Test
    public void sizeEqualsLimitTest() {
        test(1,1);
    }

    @Test
    public void tooBigTest() {
        test(1,5);
    }

    private void test(int maxFilesize, int filesize) {
        setFilesize(filesize);
        MaxFilesizeFilter filter = new MaxFilesizeFilter(maxFilesize);
        assertEquals(maxFilesize >= filesize, filter.isSelected(file));
    }
}
