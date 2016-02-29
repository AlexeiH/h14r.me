package me.h14r.invoicemaker.gui;

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
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

public class ImportDataLayer extends HBox {

  private Insets padding = new Insets(5, 5, 5, 5);

  private ActionProcessor actionProcessor;

  public ImportDataLayer(ActionProcessor actionProcessor) {
    this.actionProcessor = actionProcessor;
    VBox vbox = createLeftVBox();
    setHgrow(vbox, Priority.ALWAYS);
    getChildren().add(vbox);

    vbox = createRightVBox();
    getChildren().add(vbox);
    setHgrow(vbox, Priority.ALWAYS);

    setPadding(padding);
  }

  private VBox createLeftVBox(){
    VBox vbox = new VBox(10);
    vbox.setPadding(padding);

    Label label = new Label("File");
    label.setStyle("-fx-font-size: 12pt;");

    // File choose button
    Button chooseFileButton = new Button("Choose a file...");
    chooseFileButton.setOnAction(new FileChooserButtonListener());

    vbox.getChildren().addAll(label, chooseFileButton);

    return vbox;
  }

  private TextField login;
  private PasswordField password;

  private VBox createRightVBox(){
    VBox vbox = new VBox(10);
    vbox.setPadding(padding);

    HBox line = new HBox(10);
    line.setAlignment(Pos.CENTER);
    Label label = new Label("JIRA");
    label.setStyle("-fx-font-size: 12pt;");
    line.getChildren().add(label);
    vbox.getChildren().add(line);

    //Add login label and text field
    line = new HBox(10);
    line.setAlignment(Pos.BOTTOM_RIGHT);
    label = new Label("Login");
    login = new TextField();
    login.setPromptText("JIRA login");
    line.getChildren().addAll(label, login);
    vbox.getChildren().add(line);

    //Add password label and password field
    line = new HBox(10);
    line.setAlignment(Pos.BOTTOM_RIGHT);
    label = new Label("Password");
    password = new PasswordField();
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
    fileChooser.getExtensionFilters().addAll(
        new ExtensionFilter("Text Files", "*.txt"),
        new ExtensionFilter("All Files", "*.*"));
    File selectedFile = fileChooser.showOpenDialog(null);

    if (selectedFile != null) {
      actionProcessor.processFile(selectedFile);
    }
  }

  private class ProcessButtonListener implements EventHandler<ActionEvent> {

    public void handle(ActionEvent e) {
      actionProcessor.processJira(login.getText(), password.getText());
    }

  }

  public interface ActionProcessor {
    void processFile(File selectedFile);

    void processJira(String login, String password);
  }

  public String getPassword() {
    return password.getText();
  }

  public String getLogin() {
    return login.getText();
  }

}
