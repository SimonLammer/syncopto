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

import com.github.simonlammer.syncopto.logic.filters.FilterManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FilterManager_Test {
    private FilterManager<Integer> filterManager;

    @Before
    public void createFilterManger() {
        filterManager = new FilterManager<>();
    }

    @Test
    public void emptyFilterManagerTest() {
        assertTrue(filterManager.isSelected(4));
    }

    @Test
    public void singleFilterTest() {
        filterManager.addFilter((Integer i) -> i == 4);
        for (int i = -99; i < 100; i++) {
            assertEquals(i == 4, filterManager.isSelected(i));
        }
    }

    @Test
    public void multipleFiltersTest() {
        filterManager.addFilters(
                (Integer i) -> i % 2 == 0,
                (Integer i) -> i % 3 == 0,
                (Integer i) -> i != 6
        );
        for (int i = -99; i < 100; i++) {
            assertEquals(
                    i % 2 == 0 && i % 3 == 0 && i != 6,
                    filterManager.isSelected(i)
            );
        }
    }
}
