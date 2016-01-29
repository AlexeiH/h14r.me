package me.h14r.invoicemaker.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.h14r.invoicemaker.api.WorkLogEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {


  public static void main(String[] args) throws Exception {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Scene scene = new Scene(new Group());
    stage.setTitle("Helmentrepreneur.Me");
    stage.setWidth(500);
    stage.setHeight(600);

    WorkLogTable table = new WorkLogTable();
    ((Group) scene.getRoot()).getChildren().addAll(table);

    List<WorkLogEntry> workLogs = new ArrayList<WorkLogEntry>();
    workLogs.add(new WorkLogEntry("2015-12-01", "Desc1", new BigDecimal(8)));
    workLogs.add(new WorkLogEntry("2015-12-02", "Desc2", new BigDecimal(6)));
    workLogs.add(new WorkLogEntry("2015-12-03", "Desc3", new BigDecimal(4)));
    table.setData(workLogs);


    stage.setScene(scene);
    stage.show();
  }
}
