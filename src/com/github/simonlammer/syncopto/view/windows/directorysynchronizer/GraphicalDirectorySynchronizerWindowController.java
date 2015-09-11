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


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GraphicalDirectorySynchronizerWindowController {
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private ListView<String> listView_changes;
    @FXML private ComboBox<String> combobox_actions;
    @FXML private TextField textField_relativeFilename;
    @FXML private TableView<?> tableView_details;

    @FXML
    void applyAction(ActionEvent event) {

    }

    private String[] determineSelectedFiles() {
        throw new NotImplementedException();
    }

    @FXML
    void initialize() {
        assertFXElementNotNull();
        addCheckboxesToListviewEntries();

        String[] changes = {
                "/dir/file.ext", "/dir/sub.file2.ext", "/dir/x.tyr"
        };
        listView_changes.setItems(FXCollections.observableArrayList(changes));

        combobox_actions.getItems().addAll(
                "Use origin version",
                "Use destination version",
                "Move both files elsewhere for later comparison"
        );
    }

    private void assertFXElementNotNull() {
        assert listView_changes != null : "fx:id=\"listView_changes\" was not injected: check your FXML file 'graphicalDirectorySynchronizer.fxml'.";
        assert combobox_actions != null : "fx:id=\"combobox_actions\" was not injected: check your FXML file 'graphicalDirectorySynchronizer.fxml'.";
    }

    private void addCheckboxesToListviewEntries() {
        listView_changes.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) ->
                                System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected)
                );
                return observable;
            }
        }));
    }

}

