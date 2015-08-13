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

import com.github.simonlammer.syncopto.logic.filters.RegexFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class RegexFilter_Test {
    private String[] unfilteredValues;

    @Before
    public void setUnfilteredValues() {
        unfilteredValues = new String[] {
                "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c"
        };
    }

    @Test
    public void selectNumbersTest() {
        test("\\d", RegexFilter.Mode.SELECT_MATCHING, new String[] {
                "1", "2", "3", "4", "5", "6", "7", "8", "9"
        });
    }

    @Test
    public void selectNonNumbersTest() {
        test("\\d", RegexFilter.Mode.SELECT_NONMATCHING, new String[] {
                "a", "b", "c"
        });
    }

    @Test
    public void selectLettersTest() {
        test("[a-z]", RegexFilter.Mode.SELECT_MATCHING, new String[] {
                "a", "b", "c"
        });
    }

    @Test
    public void selectNonLettersTest() {
        test("[a-z]", RegexFilter.Mode.SELECT_NONMATCHING, new String[] {
                "1", "2", "3", "4", "5", "6", "7", "8", "9"
        });
    }

    @Test
    public void selectLowerNumbers() {
        test("[0-4]", RegexFilter.Mode.SELECT_MATCHING, new String[] {
                "1", "2", "3", "4"
        });
    }

    @Test
    public void selectUpperNumbers() {
        test("[5-9]", RegexFilter.Mode.SELECT_MATCHING, new String[] {
                "5", "6", "7", "8", "9"
        });
    }

    @Test
    public void selectNonLowerNumbers() {
        test("[0-4]", RegexFilter.Mode.SELECT_NONMATCHING, new String[] {
                "5", "6", "7", "8", "9",
                "a", "b", "c"
        });
    }

    @Test
    public void selectNonUpperNumbers() {
        test("[5-9]", RegexFilter.Mode.SELECT_NONMATCHING, new String[] {
                "1", "2", "3", "4",
                "a", "b", "c"
        });
    }

    private void test(String regexPattern, RegexFilter.Mode mode, String[] expectedMatches) {
        Pattern pattern = Pattern.compile(regexPattern);
        RegexFilter filter = new RegexFilter(pattern, mode);
        Collection<String> unfilteredValuesCollection = Arrays.asList(unfilteredValues);
        Collection<String> actualMatchesCollection = filter.determineSelectedValues(unfilteredValuesCollection);
        String[] actualMatches = actualMatchesCollection.toArray(new String[expectedMatches.length]);
        assertArrayEquals(expectedMatches, actualMatches);
    }
}
