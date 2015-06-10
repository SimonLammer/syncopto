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

package com.github.simonlammer.syncopto.tests.independent;

import java.util.regex.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex_Test {
    @Test
    public void simpleTest() {
        String s1 = "test";
        String s2 = "text";
        String s3 = "teet";
        Pattern p;
        String pattern = "te(s|x)t";
        p = Pattern.compile(pattern);
        Assert.assertTrue(pattern.equals(p.pattern()));
        Matcher m1 = p.matcher(s1);
        Matcher m2 = p.matcher(s2);
        Matcher m3 = p.matcher(s3);

        Assert.assertTrue(m1.matches());
        Assert.assertTrue(m2.matches());
        Assert.assertFalse(m3.matches());
    }
}
