package me.h14r.invoicemaker.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FXMLController {

	@FXML
	private RadioButton fileRadio;

	@FXML
	private RadioButton jiraRadio;
	
	@FXML
	private Button processBtn;
	
	@FXML
	private TextField usernameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private TextField amountOfInvoiceField;
	
	@FXML 
	private TextField rateField;
	
	@FXML
	private Text hoursToInvoiceText;
	
	@FXML
	private Button chooseFileBtn;
	
	@FXML
	protected void fileRadioChosen(ActionEvent ae) {
		jiraRadio.setSelected(false);
		processBtn.setDisable(true);
		usernameField.setDisable(true);
		passwordField.setDisable(true);
		chooseFileBtn.setDisable(false);
	}

	@FXML
	protected void jiraRadioChosen(ActionEvent ae) {
		fileRadio.setSelected(false);
		processBtn.setDisable(false);
		usernameField.setDisable(false);
		passwordField.setDisable(false);
		chooseFileBtn.setDisable(true);
	}

	@FXML
	protected void getXLSReportFromJira(ActionEvent ae) {
		String userName = usernameField.getText();
		String password = passwordField.getText();
		//TODO: retrieve report from JIRA
	}
	
	@FXML
	protected void addLineToTable(ActionEvent ae) {
		//TODO:
	}
	
	@FXML
	protected void generateReports(ActionEvent ae) {
		//TODO:
	}
	
	@FXML
	protected void browseFile(ActionEvent ae) {
		//TODO:
	}
	
}
