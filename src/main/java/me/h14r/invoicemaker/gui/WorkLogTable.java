package me.h14r.invoicemaker.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.api.WorkLogEntryComparator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkLogTable extends VBox implements IWorkLogEditor {

  private TableView<WorkLogWrapper> table = new TableView<WorkLogWrapper>();
  private ObservableList<WorkLogWrapper> data;
  private List<WorkLogEntry> workLogs;
  final HBox hb = new HBox();


  public WorkLogTable() {

    final Label label = new Label("WorkLogs");
    label.setFont(new Font("Arial", 20));

    table.setEditable(true);
    Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
      public TableCell call(TableColumn p) {
        return new EditingCell();
      }
    };

    TableColumn keyColumn = new TableColumn("Key");
    keyColumn.setMinWidth(100);
    keyColumn.setCellValueFactory(new PropertyValueFactory<WorkLogWrapper, String>("key"));
    keyColumn.setCellFactory(cellFactory);
    keyColumn.setOnEditCommit(new EventHandler<CellEditEvent<WorkLogWrapper, String>>() {
      public void handle(CellEditEvent<WorkLogWrapper, String> t) {
        ((WorkLogWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setKey(t.getNewValue());
      }
    });


    TableColumn lastNameCol = new TableColumn("Description");
    lastNameCol.setMinWidth(200);
    lastNameCol.setCellValueFactory(new PropertyValueFactory<WorkLogWrapper, String>("desc"));
    lastNameCol.setCellFactory(cellFactory);
    lastNameCol.setOnEditCommit(new EventHandler<CellEditEvent<WorkLogWrapper, String>>() {
          public void handle(CellEditEvent<WorkLogWrapper, String> t) {
            ((WorkLogWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDesc(t.getNewValue());
          }
        });

    TableColumn emailCol = new TableColumn("Hours");
    emailCol.setMinWidth(50);
    emailCol.setCellValueFactory(new PropertyValueFactory<WorkLogWrapper, String>("hrs"));
    emailCol.setCellFactory(cellFactory);
    emailCol.setOnEditCommit(new EventHandler<CellEditEvent<WorkLogWrapper, String>>() {
      public void handle(CellEditEvent<WorkLogWrapper, String> t) {
        ((WorkLogWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setHrs(t.getNewValue());
      }
    });


    table.getColumns().addAll(keyColumn, lastNameCol, emailCol);

    final TextField keyField = new TextField();
    keyField.setPromptText("Key");
    keyField.setMaxWidth(keyColumn.getPrefWidth());
    final TextField descField = new TextField();
    descField.setMaxWidth(lastNameCol.getPrefWidth());
    descField.setPromptText("Desc");
    final TextField hrsField = new TextField();
    hrsField.setMaxWidth(emailCol.getPrefWidth());
    hrsField.setPromptText("Hrs");

    final Button addButton = new Button("Add");
    addButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        data.add(new WorkLogWrapper(keyField.getText(), descField.getText(), hrsField.getText()));
        keyField.clear();
        descField.clear();
        hrsField.clear();
      }
    });

    final Button refreshButton = new Button("Restore");
    refreshButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        if (workLogs != null) {
          setData(workLogs);
        }
      }
    });

    hb.getChildren().addAll(keyField, descField, hrsField, addButton);
    hb.setSpacing(3);

    this.setSpacing(5);
    this.setPadding(new Insets(10, 0, 0, 10));
    this.getChildren().addAll(label, refreshButton, table, hb);
  }

  public void setData(List<WorkLogEntry> workLogs) {
    List<WorkLogWrapper> workLogWrappers = new ArrayList<WorkLogWrapper>();
    for (WorkLogEntry workLogEntry : workLogs) {
      workLogWrappers.add(new WorkLogWrapper(workLogEntry));
    }
    this.data = FXCollections.observableArrayList(workLogWrappers);
    this.workLogs = workLogs;
    table.setItems(data);
  }

  public List<WorkLogEntry> getEdited() {
    List<WorkLogEntry> result = new ArrayList<WorkLogEntry>();
    for (WorkLogWrapper wrapper : data) {
      WorkLogEntry entry = new WorkLogEntry(wrapper.getKey(), wrapper.getDesc(), new BigDecimal(wrapper.getHrs()));
      result.add(entry);
    }
    Collections.sort(result, new WorkLogEntryComparator());
    return result;
  }

  public static class WorkLogWrapper {

    private final SimpleStringProperty key;
    private final SimpleStringProperty desc;
    private final SimpleStringProperty hrs;

    private WorkLogWrapper(String key, String desc, String hrs) {
      this.key = new SimpleStringProperty(key);
      this.desc = new SimpleStringProperty(desc);
      this.hrs = new SimpleStringProperty(hrs);
    }

    private WorkLogWrapper(WorkLogEntry entry) {
      this.key = new SimpleStringProperty(entry.getKey());
      this.desc = new SimpleStringProperty(entry.getWorkLog());
      this.hrs = new SimpleStringProperty(entry.getHours() != null ? entry.getHours().toPlainString() : "");
    }

    public String getKey() {
      return key.get();
    }

    public void setKey(String aKey) {
      key.set(aKey);
    }

    public String getDesc() {
      return desc.get();
    }

    public void setDesc(String aDesc) {
      desc.set(aDesc);
    }

    public String getHrs() {
      return hrs.get();
    }

    public void setHrs(String aHrs) {
      hrs.set(aHrs);
    }
  }

  class EditingCell extends TableCell<WorkLogWrapper, String> {

    private TextField textField;

    public EditingCell() {
    }

    @Override
    public void startEdit() {
      if (!isEmpty()) {
        super.startEdit();
        createTextField();
        setText(null);
        setGraphic(textField);
        textField.selectAll();
        Platform.runLater(new Runnable() {
          public void run() {
            textField.requestFocus();
          }
        });
      }
    }

    @Override
    public void cancelEdit() {
      super.cancelEdit();

      setText((String) getItem());
      setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (empty) {
        setText(null);
        setGraphic(null);
      } else {
        if (isEditing()) {
          if (textField != null) {
            textField.setText(getString());
          }
          setText(null);
          setGraphic(textField);
        } else {
          setText(getString());
          setGraphic(null);
        }
      }
    }

    private void createTextField() {
      textField = new TextField(getString());
      textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
      textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
          if (!arg2) {
            commitEdit(textField.getText());
          }
        }
      });
    }

    private String getString() {
      return getItem() == null ? "" : getItem().toString();
    }
  }
}