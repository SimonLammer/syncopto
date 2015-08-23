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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.BiConsumer;

public class BasicDirectoryChangeWatcher implements DirectoryChangeWatcher {
    private static final int WATCHSERVICE_TIMEOUT = 5000;

    private WatchService watcher;
    private Path directory;
    Thread checkThread;
    boolean watching;

    private final WatchEvent.Kind[] kinds = {
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
    };

    BiConsumer<ActionType, File> handler;

    @Override
    public void startWatching() {

        startWatchService();

        checkThread = new Thread(() -> {
            while (watching) {
                try {
                    // wait for event
                    final WatchKey wk = watcher.take();

                    // handle events
                    for (WatchEvent<?> event : wk.pollEvents()) {
                        if (event.context() instanceof Path) {
                            final File file = new File(((Path)event.context()).toUri());
                            ActionType type;

                            if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_CREATE.name())) {
                                type = ActionType.CREATE;
                            } else if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
                                type = ActionType.MODIFY;
                            } else if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_DELETE.name())) {
                                type = ActionType.DELETE;
                            }else {
                                continue;
                            }
                            handler.accept(type,file);
                        }
                    }
                    // reset the key that further events will be registered too.
                    boolean valid = wk.reset();
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        checkThread.setDaemon(true);
        checkThread.setPriority(Thread.MIN_PRIORITY);
        checkThread.setName("Syncopto Sync-Service");
        checkThread.start();
    }

    private void startWatchService() {
        if (directory == null) {
            throw new IllegalArgumentException("directory has to be set before calling startWatching()");
        }

        // create new watchservice
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Watchservice couldn't be created.", e);
        }

        // register watcher for root directory
        try {
            directory.register(watcher, kinds);
        } catch (IOException e) {
            throw new RuntimeException("Directory couldn't be registered to the watchservice.", e);
        }

        watching = true;
    }

    @Override
    public void stopWatching() {
        watching = false;
        if (checkThread != null && checkThread.isAlive()) {
            try {
                checkThread.join(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            watcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setChangeHandler(BiConsumer<ActionType, File> handler) {
        this.handler = handler;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }
}
