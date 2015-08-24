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
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class BufferedDirectorySynchronizer extends DirectorySynchronizer {
    private Collection<Pair<ActionType, File>> events;
    private Consumer<Collection<Pair<ActionType, File>>> eventHandler;

    public BufferedDirectorySynchronizer(File originDirectory, File destinationDirectory, Filter<File> filter, Consumer<Collection<Pair<ActionType, File>>> eventHandler) {
        this(new BufferedDirectorySynchronizerCreator(originDirectory, destinationDirectory, filter, eventHandler));
    }

    private BufferedDirectorySynchronizer(BufferedDirectorySynchronizerCreator object) {
        super(object.getBuilder());
        this.events = object.getEvents();
        this.eventHandler = object.getEventHandler();
    }

    @Override
    public void synchronizeDirectories() {
        events.clear();
        super.synchronizeDirectories();
        eventHandler.accept(events);
    }

    public enum ActionType {
        NEW_FILE_IN_ORIGIN,
        NEW_FILE_IN_DESTINATION,
        FILES_DIVERGED
    }

    private static class BufferedDirectorySynchronizerCreator {
        private DirectorySynchronizerBuilder builder;
        private Consumer<Collection<Pair<ActionType, File>>> eventHandler;
        private Collection<Pair<ActionType, File>> events;

        public BufferedDirectorySynchronizerCreator(File originDirectory, File destinationDirectory, Filter<File> filter, Consumer<Collection<Pair<ActionType, File>>> eventHandler) {
            this.eventHandler = eventHandler;
            this.events = new ArrayList<>();
            this.builder = newDirectorySynchronizerBuilder(originDirectory, destinationDirectory, filter, events);
        }

        private DirectorySynchronizerBuilder newDirectorySynchronizerBuilder(File originDirectory, File destinationDirectory, Filter<File> filter, Collection<Pair<ActionType, File>> events) {
            Consumer<File> newFileInOriginHandler = (file) -> events.add(new Pair<>(ActionType.NEW_FILE_IN_ORIGIN, file));
            Consumer<File> newFileInDestinationHandler = (file) -> events.add(new Pair<>(ActionType.NEW_FILE_IN_DESTINATION, file));
            Consumer<File> filesDivergedHandler = (file) -> events.add(new Pair<>(ActionType.FILES_DIVERGED, file));
            return new DirectorySynchronizerBuilder()
                    .setOriginDirectory(originDirectory)
                    .setDestinationDirectory(destinationDirectory)
                    .setFilter(filter)
                    .setNewFileInOriginHandler(newFileInOriginHandler)
                    .setNewFileInDestinationHandler(newFileInDestinationHandler)
                    .setFilesDivergedHandler(filesDivergedHandler);
        }

        public DirectorySynchronizerBuilder getBuilder() {
            return builder;
        }

        public Consumer<Collection<Pair<ActionType, File>>> getEventHandler() {
            return eventHandler;
        }

        public Collection<Pair<ActionType, File>> getEvents() {
            return events;
        }
    }
}
