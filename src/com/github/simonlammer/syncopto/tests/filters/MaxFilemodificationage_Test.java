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

import com.github.simonlammer.syncopto.logic.filters.MaxFilemodificationageFilter;
import com.github.simonlammer.syncopto.tests.utils.FilemodificationageFilterTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class MaxFilemodificationage_Test extends FilemodificationageFilterTest {
    @Test
    public void youngEnoughTest() {
        test(100,1);
    }

    @Test
    public void tooOldTest() {
        test(1,2);
    }

    private void test(long maxAge, long timeout) {
        MaxFilemodificationageFilter filter = new MaxFilemodificationageFilter(maxAge);
        timeout(timeout);
        assertEquals(timeout < maxAge, filter.isSelected(file));
    }
}
