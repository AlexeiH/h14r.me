package me.h14r.invoicemaker.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.h14r.invoicemaker.ConfigurationProvider;
import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import me.h14r.invoicemaker.api.InvoiceValueHolder;
import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.jira.JIRAWorkLogDataProvider;
import me.h14r.invoicemaker.template.YARGTemplateProcessor;
import me.h14r.invoicemaker.util.CommonUtils;
import me.h14r.invoicemaker.xlsparser.XLSWorkLogDataProvider;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class MainApplication extends Application {

  private WorkLogTable table = new WorkLogTable();
  private TextField invoiceNo = new TextField();
  private DatePicker startDate = new DatePicker();
  private DatePicker endDate = new DatePicker();
  private DatePicker invoiceDate = new DatePicker();
  private ImportDataLayer importDataLayer = new ImportDataLayer(new SourceActionProcessor());
  private TextField hourRate = new TextField();
  private TextField amount = new TextField();
  private TextField hours = new TextField();
  private Configuration configuration = ConfigurationProvider.getInstance();
  private SimpleDateFormat lastUsedDateFormat = new SimpleDateFormat("dd/MM/yyyy");
  private Stage stage;

  public static void main(String[] args) throws Exception {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
  /*try {
    Parent root = FXMLLoader.load(getClass().getResource("Scene.fxml"));
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();
	} catch (Exception e) {
	}*/
    this.stage = stage;

    Scene scene = new Scene(new Group());
    stage.setTitle("Helmentrepreneur.Me");
    stage.setWidth(800);
    stage.setHeight(900);

    VBox invoiceData = new VBox();

    HBox invoiceDataL1 = new HBox();
    Label invoiceNoLabel = new Label("InvoiceNo");
    Label invoiceDateLabel = new Label("Invoice Date");
    invoiceDataL1.getChildren().addAll(invoiceNoLabel, invoiceNo, invoiceDateLabel, invoiceDate);
    invoiceDataL1.setPadding(new Insets(5, 5, 5, 5));
    invoiceDataL1.setSpacing(10);


    HBox invoiceDataL2 = new HBox();
    Label startDateLabel = new Label("Work Start Date");
    Label endDateLabel = new Label("Work End Date");
    invoiceDataL2.getChildren().addAll(startDateLabel, startDate, endDateLabel, endDate);
    invoiceDataL2.setPadding(new Insets(5, 5, 5, 5));
    invoiceDataL2.setSpacing(10);

    invoiceData.getChildren().addAll(invoiceDataL1, invoiceDataL2);
    invoiceData.setPadding(new Insets(15, 12, 15, 12));


    invoiceNo.setText(configuration.getString("userpreference.invoice.lastUsedNo"));
    String lastUsedDateStr = configuration.getString("userpreference.invoice.lastUsedDate");
    if (StringUtils.isNotEmpty(lastUsedDateStr)) {
      Date lastUsedDate = lastUsedDateFormat.parse(lastUsedDateStr);
      invoiceDate.setValue(convertToLocalDate(lastUsedDate));
      setMonthStartAndEnd(invoiceDate.getValue());
      invoiceDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
          if (oldValue.getMonth() != newValue.getMonth() || oldValue.getYear() != newValue.getYear()) {
            setMonthStartAndEnd(newValue);
          }
        }
      });
    }


    HBox amountCalc = new HBox();
    Label amountLabel = new Label("Amount due");
    Label hourRateLabel = new Label("Rate");
    Label expectedHoursLabel = new Label("Expected Hours");
    hourRate.setText(configuration.getString("userpreference.invoice.hourRate"));
    amount.textProperty().addListener(new HoursCalcListener());
    amount.setText("1000");
    hourRate.textProperty().addListener(new HoursCalcListener());
    hours.setText(new BigDecimal("1000").divideToIntegralValue(new BigDecimal(hourRate.getText())).toPlainString());
    hours.setDisable(true);
    amountCalc.getChildren().addAll(amountLabel, amount, hourRateLabel, hourRate, expectedHoursLabel, hours);
    amountCalc.setSpacing(10);
    amountCalc.setPadding(new Insets(15, 12, 15, 12));

    HBox toolbar = new HBox();
    final Button generateButton = new Button("Generate");
    toolbar.setAlignment(Pos.CENTER_RIGHT);
    toolbar.setPadding(new Insets(15, 0, 0, 0));
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

  private LocalDate convertToLocalDate(Date aDate) {
    return aDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  private void setMonthStartAndEnd(LocalDate value) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(convertToDate(value));
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
      cal.add(Calendar.DAY_OF_MONTH, 2);
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    startDate.setValue(convertToLocalDate(cal.getTime()));

    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
      cal.add(Calendar.DAY_OF_MONTH, -1);
    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      cal.add(Calendar.DAY_OF_MONTH, -2);
    }
    endDate.setValue(convertToLocalDate(cal.getTime()));

  }

  private void generateTemplate() {
    InvoiceValueHolder vh = new InvoiceValueHolder();
    vh.setWorkLogs(table.getEdited());
    vh.setInvoiceDate(convertToDate(invoiceDate.getValue()));
    vh.setInvoiceStart(convertToDate(startDate.getValue()));
    vh.setInvoiceEnd(convertToDate(endDate.getValue()));
    vh.setNumber(invoiceNo.getText());
    vh.setTotalHours(CommonUtils.total(vh.getWorkLogs()));
    vh.setTotalAmount(new BigDecimal(amount.getText()));
    vh.setHourRate(new BigDecimal(hourRate.getText()));


    String invoiceTemplate = configuration.getString("template.invoice.source");
    String invoiceTargetPath = processFilePath(configuration.getString("template.invoice.target"), vh);
    new File(invoiceTargetPath).mkdirs();
    try {
      YARGTemplateProcessor tt = new YARGTemplateProcessor(invoiceTemplate, Paths.get(invoiceTemplate).getFileName()
          .toString());
      tt.generate(vh, new FileOutputStream(invoiceTargetPath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    String actTemplate = configuration.getString("template.act.source");
    String actTargetPath = processFilePath(configuration.getString("template.act.target"), vh);
    new File(actTargetPath).mkdirs();
    try {
      YARGTemplateProcessor tt = new YARGTemplateProcessor(actTemplate, Paths.get(actTemplate).getFileName().toString
          ());
      tt.generate(vh, new FileOutputStream(actTargetPath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }


    configuration.setProperty("userpreference.invoice.lastUsedNo", vh.getNumber());
    configuration.setProperty("userpreference.invoice.lastUsedDate", lastUsedDateFormat.format(vh.getInvoiceDate()));
    configuration.setProperty("userpreference.invoice.hourRate", hourRate.getText());
    configuration.setProperty("worklog.source.jira.login.username", importDataLayer.getLogin());
    configuration.setProperty("worklog.source.jira.login.password", importDataLayer.getPassword());

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.WINDOW_MODAL);
    dialogStage.setScene(new Scene(VBoxBuilder.create().
        children(new Text("All done")).
        alignment(Pos.CENTER).padding(new Insets(5)).build()));
    dialogStage.show();
  }

  private String processFilePath(String origin, InvoiceValueHolder vh) {
    Map<String, String> values = new HashMap<String, String>();
    values.put("year", String.valueOf(convertToLocalDate(vh.getInvoiceDate()).getYear()));
    values.put("invoiceNo", vh.getNumber());
    values.put("date", new SimpleDateFormat("yyyy-MM-dd").format(vh.getInvoiceDate()));
    String result = origin;
    for (String key : values.keySet()) {
      result = result.replaceAll("\\{" + key + "\\}", values.get(key));
    }
    return result;
  }


  private Date convertToDate(LocalDate aDate) {
    return Date.from(aDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private class SourceActionProcessor implements ImportDataLayer.ActionProcessor {

    public void processFile(File selectedFile) {
      try {
        IWorkLogDataProvider workLogDataProvider = new XLSWorkLogDataProvider(configuration, new FileInputStream
            (selectedFile));
        Collection<WorkLogEntry> workLogs = workLogDataProvider.getWorkLogs();
        BigDecimal expectedHoursBD = new BigDecimal(hours.getText());
        table.setData(workLogs);
        table.setExpectedHours(expectedHoursBD);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    public void processJira(String login, String password) {
      Date fromDateV = convertToDate(startDate.getValue());
      Date endDateV = convertToDate(endDate.getValue());
      IWorkLogDataProvider workLogDataProvider = new JIRAWorkLogDataProvider(configuration, fromDateV, endDateV);
      Collection<WorkLogEntry> workLogs = workLogDataProvider.getWorkLogs();
      BigDecimal expectedHoursBD = new BigDecimal(hours.getText());
      table.setData(workLogs);
      table.setExpectedHours(expectedHoursBD);
    }
  }

  private class HoursCalcListener implements ChangeListener<String> {

    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      try {
        BigDecimal amount = new BigDecimal(MainApplication.this.amount.getText());
        BigDecimal rate = new BigDecimal(MainApplication.this.hourRate.getText());
        if (amount != null && rate != null) {
          MainApplication.this.hours.setText(amount.divideToIntegralValue(rate).toPlainString());
          MainApplication.this.table.setExpectedHours(amount.divideToIntegralValue(rate));
        }
      } catch (Exception e) {
      }
    }
  }

}
