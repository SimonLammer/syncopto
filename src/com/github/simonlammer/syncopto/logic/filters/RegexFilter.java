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

package com.github.simonlammer.syncopto.logic.filters;

import java.io.Serializable;
import java.util.regex.Pattern;

public class RegexFilter extends BasicFilter<String> implements Serializable {
    private Mode mode;
    private Pattern pattern;

    public RegexFilter(Pattern pattern, Mode mode) {
        this.pattern = pattern;
        this.mode = mode;
    }

    @Override
    public boolean isSelected(String value) {
        return pattern.matcher(value).matches() == mode.equals(Mode.SELECT_MATCHING);
    }

    public Mode getMode() {
        return mode;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public enum Mode {
        SELECT_MATCHING,
        SELECT_NONMATCHING
    }
}
