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

package com.github.simonlammer.syncopto.logic;

import com.github.simonlammer.syncopto.logic.filters.Filter;
import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Program {
    private static Program instance;
    private Map<String, Link> links;

    private Program() {
        links = new HashMap<>();
    }

    public static Program getInstance() {
        if (instance == null) {
            instance = new Program();
        }
        return instance;
    }

    public void putLink(String name, Link link) {
        links.put(name, link);
    }

    public boolean containsLink(String name) {
        return links.containsKey(name);
    }

    public Link getLink(String name) {
        return links.get(name);
    }

    public String[] getLinkNames() {
        return links.keySet().toArray(new String[links.size()]);
    }

    @SuppressWarnings("unchecked")
    public Pair<String, Link>[] getLinks() {
        Pair<String, Link>[] links = new Pair[this.links.size()];
        Iterator iterator = this.links.entrySet().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Entry<String, Link> entry = (Entry<String, Link>) iterator.next();
            Pair<String, Link> pair = new Pair(entry.getKey(), entry.getValue());
            links[i] = pair;
        }
        return links;
    }

    public Link removeLink(String name) {
        return links.remove(name);
    }
}
