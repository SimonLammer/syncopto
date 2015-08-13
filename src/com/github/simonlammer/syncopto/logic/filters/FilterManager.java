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

import java.util.*;

public class FilterManager<T> extends BasicFilter<T> {
    List<Filter> filters;

    public FilterManager(Collection<Filter<T>> filters) {
        this();
        addFilters(filters);
    }

    public FilterManager(Filter<T> ... filters) {
        this();
        addFilters(filters);
    }

    public FilterManager() {
        this.filters = new ArrayList<>();
    }

    @Override
    public boolean isSelected(T value) {
        boolean isSelected = true;
        Iterator<Filter> filterIterator = filters.iterator();
        while(isSelected && filterIterator.hasNext()) {
            Filter filter = filterIterator.next();
            isSelected = filter.isSelected(value);
        }
        return isSelected;
    }

    public void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    public void addFilters(Filter<T> ... filters) {
        for (int i = 0; i < filters.length; i++) {
            this.filters.add(filters[i]);
        }
    }

    public void addFilters(Collection<Filter<T>> filters) {
        this.filters.addAll(filters);
    }
}
