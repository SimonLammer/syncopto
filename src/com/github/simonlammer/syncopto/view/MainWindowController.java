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

package com.github.simonlammer.syncopto.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindowController {

    @FXML
    private ImageView imgView_status;

    @FXML
    private ListView<?> listView_status;

    @FXML
    private Label label_about_github;

    @FXML
    private Label label_about_license;

    public void init(Stage stage) {
        ObservableList observableList = FXCollections.observableArrayList();
        observableList.addAll(
                "Created:    Pictures > pic001.png",
                "Created:    Pictures > pic002.png",
                "Modified:  Stuff > notes.txt",
                "Removed: Documents > OldBill.pdf",
                "Created:    Pictures > pic003.png",
                "Created:    Pictures > pic004.png",
                "Modified:  School > Hello World.java",
                "Removed: Stuff > someRandomFileIDontWantAnymore.txt"
        );
        listView_status.setItems(observableList);

        label_about_license.setOnMouseClicked((MouseEvent event) -> {
            openURL("http://www.gnu.org/licenses/gpl-3.0.en.html");
        });
        label_about_github.setOnMouseClicked((MouseEvent event) -> {
            openURL("https://www.github.com/simonlammer/syncopto");
        });
    }

    private void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
