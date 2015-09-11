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

package com.github.simonlammer.syncopto.view.windows.directorysynchronizer;

import com.github.simonlammer.syncopto.logic.BufferedDirectorySynchronizer;
import com.github.simonlammer.syncopto.logic.DirectorySynchronizer;
import com.github.simonlammer.syncopto.logic.DirectorySynchronizerBuilder;
import com.github.simonlammer.syncopto.logic.filters.Filter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

public class GraphicalDirectorySynchronizer extends BufferedDirectorySynchronizer {
    public GraphicalDirectorySynchronizer(File originDirectory, File destinationDirectory, Filter<File> filter) {
        super(originDirectory, destinationDirectory, filter, GraphicalDirectorySynchronizer::eventsHandler);
    }

    private static void eventsHandler(Collection<Pair<ActionType, File>> events) {
        GraphicalDirectorySynchronizerWindow window = new GraphicalDirectorySynchronizerWindow();
    }

    public static void main(String[] args) {
        GraphicalDirectorySynchronizerWindow.launch();
    }
}
