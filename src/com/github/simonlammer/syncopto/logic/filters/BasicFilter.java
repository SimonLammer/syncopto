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

import java.io.File;
import java.util.Collection;
import java.util.Stack;

public abstract class BasicFilter<T> implements Filter<T> {
    public Collection<T> determineSelectedValues(Collection<T> unfilteredValues) {
        Stack<T> selectedValues = new Stack<>();
        unfilteredValues.forEach((T value) -> {
            if (isSelected(value)) {
                selectedValues.push(value);
            }
        });
        return selectedValues;
    }

    public T[] determineSelectedValues(T[] unfilteredValues) {
        Stack<T> selectedValues = new Stack<>();
        for (T value : unfilteredValues) {
            if (isSelected(value)) {
                selectedValues.push(value);
            }
        }
        @SuppressWarnings("unchecked")
        T[] result = (T[]) selectedValues.toArray();
        return result;
    }
}
