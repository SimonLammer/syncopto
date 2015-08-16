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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.*;

public class MainWindowController {
    @FXML private ImageView imgView_status;
    @FXML private ListView<?> listView_status;
    @FXML private ListView<?> listView_links;
    @FXML private TextField textField_originDirectoryPath;
    @FXML private TextField textField_destinationDirectoryPath;
    @FXML private ListView<?> listView_filters;
    @FXML private TextField textField_name;
    @FXML private Button button_addLink;
    @FXML private Button button_removeLink;
    @FXML private Button button_previewLink;
    @FXML private Button button_addFilter;
    @FXML private Button button_removeFilter;
    @FXML private Button button_editFilter;
    @FXML private Label label_version;

    private Stage stage;

    @FXML
    void addFilter(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Action not supported yet!");
    }

    @FXML
    void addLink(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Action not supported yet!");
    }

    @FXML
    void chooseDestinationDirectory(ActionEvent event) {
        chooseDirectory(textField_destinationDirectoryPath, "Choose destination directory");
    }

    @FXML
    void chooseOriginDirectory(ActionEvent event) {
        chooseDirectory(textField_originDirectoryPath, "Choose origin directory");
    }

    private void chooseDirectory(TextField textField, String message) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(message);
        File selectedDirectory = directoryChooser.showDialog(stage);
        textField.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    void editFilter(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Action not supported yet!");
    }

    @FXML
    void previewLink(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Action not supported yet!");
    }

    @FXML
    void removeFilter(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Action not supported yet!");
    }

    @FXML
    void removeLink(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Action not supported yet!");
    }

    @FXML
    void showLicense(ActionEvent event) {
        openURL("http://www.gnu.org/licenses/gpl-3.0.en.html");
    }

    @FXML
    void showSourceCode(ActionEvent event) {
        openURL("https://www.github.com/simonlammer/syncopto");
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

    @FXML
    void initialize() {
        assert imgView_status != null : "fx:id=\"imgView_status\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert listView_status != null : "fx:id=\"listView_status\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert listView_links != null : "fx:id=\"listView_links\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert textField_originDirectoryPath != null : "fx:id=\"textField_originDirectoryPath\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert textField_destinationDirectoryPath != null : "fx:id=\"textField_destinationDirectoryPath\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert listView_filters != null : "fx:id=\"listView_filters\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert textField_name != null : "fx:id=\"textField_name\" was not injected: check your FXML file 'mainWindow.fxml'.";
        assert label_version != null : "fx:id=\"label_version\" was not injected: check your FXML file 'mainWindow.fxml'.";

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
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
