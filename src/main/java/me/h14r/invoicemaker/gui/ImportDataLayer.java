package me.h14r.invoicemaker.gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ImportDataLayer extends HBox {

  public ImportDataLayer() {
    VBox vbox = createLeftVBox();
    setHgrow(vbox, Priority.ALWAYS);
    getChildren().add(vbox);

    vbox = createRightVBox();
    getChildren().add(vbox);
    setHgrow(vbox, Priority.ALWAYS);

    setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: black;");
    setPadding(new Insets(25, 25, 25, 25));
  }

  private VBox createLeftVBox(){
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(25, 25, 25, 25));

    Label label = new Label("File");
    label.setStyle("-fx-font-size: 20pt;");

    // File choose button
    Button chooseFileButton = new Button("Choose a file...");
    chooseFileButton.setOnAction(new FileChooserButtonListener());

    vbox.getChildren().addAll(label, chooseFileButton);

    return vbox;
  }

  private VBox createRightVBox(){
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(25, 25, 25, 25));

    Label label = new Label("JIRA");
    label.setStyle("-fx-font-size: 20pt;");
    vbox.getChildren().add(label);

    //Add login label and text field
    HBox line = new HBox(30);
    line.setAlignment(Pos.BOTTOM_RIGHT);
    label = new Label("Login");
    TextField login = new TextField();
    login.setPromptText("JIRA login");
    line.getChildren().addAll(label, login);
    vbox.getChildren().add(line);

    //Add password label and password field
    line = new HBox(30);
    line.setAlignment(Pos.BOTTOM_RIGHT);
    label = new Label("Password");
    PasswordField password = new PasswordField();
    password.setPromptText("JIRA password");
    line.getChildren().addAll(label, password);
    vbox.getChildren().add(line);

    //Add process button
    line = new HBox();
    line.setAlignment(Pos.BOTTOM_RIGHT);
    Button process = new Button("Process");
    process.setOnAction(new ProcessButtonListener());
    line.getChildren().addAll(process);
    vbox.getChildren().add(line);

    return vbox;
  }
  
  private class FileChooserButtonListener implements EventHandler<ActionEvent> {

    public void handle(ActionEvent e) {
      showFileChooser();
    }
  }

  private void showFileChooser() {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    File selectedFile = fileChooser.showOpenDialog(null);

    if (selectedFile != null) {
        //TODO put code for file processing here
    }
  }

  private class ProcessButtonListener implements EventHandler<ActionEvent> {

    public void handle(ActionEvent e) {

    }
  }
}
