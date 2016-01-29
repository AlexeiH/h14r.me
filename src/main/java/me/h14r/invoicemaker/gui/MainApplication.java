package me.h14r.invoicemaker.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.h14r.invoicemaker.ConfigurationProvider;
import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.xlsparser.XLSWorkLogDataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public class MainApplication extends Application {

  private WorkLogTable table = new WorkLogTable();
  private TextField invoiceNo = new TextField();
  private DatePicker month = new DatePicker();
  private ImportDataLayer importDataLayer = new ImportDataLayer(new SourceActionProcessor());
  private TextField hourRate = new TextField();
  private TextField amount = new TextField();
  private TextField hours = new TextField();

  public static void main(String[] args) throws Exception {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Scene scene = new Scene(new Group());
    stage.setTitle("Helmentrepreneur.Me");
    stage.setWidth(800);
    stage.setHeight(900);


    HBox invoiceData = new HBox();
    Label invoiceNoLabel = new Label("InvoiceNo");
    Label monthLabel = new Label("Month");
    invoiceData.getChildren().addAll(invoiceNoLabel, invoiceNo, monthLabel, month);
    invoiceData.setSpacing(10);
    invoiceData.setPadding(new Insets(15, 12, 15, 12));

    HBox amountCalc = new HBox();
    Label amountLabel = new Label("Amount due");
    Label hourRateLabel = new Label("Rate");
    Label expectedHoursLabel = new Label("Expected Hours");
    amount.textProperty().addListener(new HoursCalcListener());
    hourRate.textProperty().addListener(new HoursCalcListener());
    hours.setDisable(true);
    amountCalc.getChildren().addAll(amountLabel, amount, hourRateLabel, hourRate, expectedHoursLabel, hours);
    amountCalc.setSpacing(10);
    amountCalc.setPadding(new Insets(15, 12, 15, 12));

    HBox toolbar = new HBox();
    final Button generateButton = new Button("Generate");
    generateButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        generateTemplate();
      }
    });
    toolbar.getChildren().addAll(generateButton);
    toolbar.setSpacing(10);
    toolbar.setPadding(new Insets(15, 12, 15, 12));

    VBox mainScene = new VBox();
    mainScene.setSpacing(5);
    mainScene.getChildren().addAll(invoiceData, amountCalc, importDataLayer, table, toolbar);


    ((Group) scene.getRoot()).getChildren().addAll(mainScene);
    stage.setScene(scene);
    stage.show();
  }

  private void generateTemplate() {
    //TODO
  }

  private class SourceActionProcessor implements ImportDataLayer.ActionProcessor {

    public void processFile(File selectedFile) {
      //      List<WorkLogEntry> workLogs = new ArrayList<WorkLogEntry>();
      //      for (int i = 0; i < 12; i++) {
      //        workLogs.add(new WorkLogEntry("2015-12-01", "Desc1", new BigDecimal(8)));
      //        workLogs.add(new WorkLogEntry("2015-12-02", "Desc2", new BigDecimal(6)));
      //        workLogs.add(new WorkLogEntry("2015-12-03", "Desc3", new BigDecimal(4)));
      //      }
      try {
        IWorkLogDataProvider workLogDataProvider = new XLSWorkLogDataProvider(ConfigurationProvider.getInstance(),
            new FileInputStream(selectedFile));
        List<WorkLogEntry> workLogs = workLogDataProvider.getWorkLogs();
        BigDecimal expectedHoursBD = new BigDecimal(hours.getText());
        table.setData(workLogs, expectedHoursBD);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }


    }

    public void processJira(String login, String password) {

    }
  }

  private class HoursCalcListener implements ChangeListener<String> {

    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      try {
        BigDecimal amount = new BigDecimal(MainApplication.this.amount.getText());
        BigDecimal rate = new BigDecimal(MainApplication.this.hourRate.getText());
        if (amount != null && rate != null) {
          MainApplication.this.hours.setText(amount.divideToIntegralValue(rate).toPlainString());
        }
      } catch (Exception e) {
      }
    }
  }

}
