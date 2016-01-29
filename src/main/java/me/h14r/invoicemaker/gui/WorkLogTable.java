package me.h14r.invoicemaker.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.api.WorkLogEntryComparator;
import me.h14r.invoicemaker.util.DefaultWorkLogTotalHrsAdjuster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkLogTable extends VBox implements IWorkLogEditor {

  private TableView<WorkLogWrapper> table = new TableView<WorkLogWrapper>();
  private ObservableList<WorkLogWrapper> data;
  private List<WorkLogEntry> workLogs;
  private BigDecimal expectedHrs;
  private Label totalLabel;

  public WorkLogTable() {

    table.setEditable(true);
    table.setMinWidth(760);

    Callback<TableColumn, TableCell> editCellFactory = new Callback<TableColumn, TableCell>() {
      public TableCell call(TableColumn p) {
        return new EditingCell();
      }
    };

    TableColumn keyColumn = new TableColumn("Key");
    keyColumn.setResizable(false);
    keyColumn.setMinWidth(100);
    keyColumn.setCellValueFactory(new PropertyValueFactory<WorkLogWrapper, String>("key"));
    keyColumn.setCellFactory(editCellFactory);
    keyColumn.setOnEditCommit(new EventHandler<CellEditEvent<WorkLogWrapper, String>>() {
      public void handle(CellEditEvent<WorkLogWrapper, String> t) {
        ((WorkLogWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setKey(t.getNewValue());
      }
    });


    TableColumn descColumn = new TableColumn("Description");
    keyColumn.setResizable(false);
    descColumn.setMinWidth(500);
    descColumn.setCellValueFactory(new PropertyValueFactory<WorkLogWrapper, String>("desc"));
    descColumn.setCellFactory(editCellFactory);
    descColumn.setOnEditCommit(new EventHandler<CellEditEvent<WorkLogWrapper, String>>() {
      public void handle(CellEditEvent<WorkLogWrapper, String> t) {
        ((WorkLogWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDesc(t.getNewValue());
      }
    });

    TableColumn hrsColumn = new TableColumn("Hours");
    keyColumn.setResizable(false);
    hrsColumn.setMinWidth(50);
    hrsColumn.setCellValueFactory(new PropertyValueFactory<WorkLogWrapper, String>("hrs"));
    hrsColumn.setCellFactory(editCellFactory);
    hrsColumn.setOnEditCommit(new EventHandler<CellEditEvent<WorkLogWrapper, String>>() {
      public void handle(CellEditEvent<WorkLogWrapper, String> t) {
        ((WorkLogWrapper) t.getTableView().getItems().get(t.getTablePosition().getRow())).setHrs(t.getNewValue());
      }
    });

    Callback<TableColumn, TableCell> actionCellFactory = new Callback<TableColumn, TableCell>() {
      public TableCell call(TableColumn p) {
        return new ActionCell();
      }
    };

    TableColumn actionColumn = new TableColumn("Actions");
    actionColumn.setMinWidth(50);
    actionColumn.setCellFactory(actionCellFactory);

    table.getColumns().addAll(keyColumn, descColumn, hrsColumn, actionColumn);


    final TextField keyField = new TextField();
    keyField.setPromptText("Key");
    keyField.setMaxWidth(keyColumn.getMinWidth());
    final TextField descField = new TextField();
    descField.setMinWidth(descColumn.getMinWidth());
    descField.setMaxWidth(descColumn.getMinWidth());
    descField.setPromptText("Desc");
    final TextField hrsField = new TextField();
    hrsField.setMaxWidth(hrsColumn.getMinWidth());
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
        refreshData();
      }
    });

    final Button adjustData = new Button("Adjust");
    adjustData.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        if (workLogs != null) {
          DefaultWorkLogTotalHrsAdjuster adjuster = new DefaultWorkLogTotalHrsAdjuster();
          refreshData(adjuster.adjustTotals(workLogs, expectedHrs));
        }
      }
    });

    HBox hbToolbar = new HBox();
    hbToolbar.getChildren().addAll(refreshButton, adjustData);
    hbToolbar.setSpacing(3);

    HBox hbAdd = new HBox();
    hbAdd.getChildren().addAll(keyField, descField, hrsField, addButton);
    hbAdd.setSpacing(3);

    totalLabel = new Label("Total");
    totalLabel.setTextAlignment(TextAlignment.RIGHT);
    totalLabel.setAlignment(Pos.CENTER_RIGHT);
    totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

    HBox hbTotal = new HBox();
    hbTotal.getChildren().addAll(totalLabel);
    hbTotal.setSpacing(3);
    hbTotal.setMinWidth(keyColumn.getMinWidth() + descColumn.getMinWidth() + hrsColumn.getMinWidth());
    hbTotal.setMaxWidth(keyColumn.getMinWidth() + descColumn.getMinWidth() + hrsColumn.getMinWidth());
    hbTotal.setAlignment(Pos.CENTER_RIGHT);

    this.setSpacing(5);
    this.setPadding(new Insets(10, 0, 0, 10));
    this.getChildren().addAll(hbToolbar, table, hbTotal, hbAdd);
  }

  public void setData(List<WorkLogEntry> workLogs, BigDecimal expectedHrs) {
    this.workLogs = workLogs;
    this.expectedHrs = expectedHrs;
    refreshData();
    recalcTotal();
  }

  private void refreshData(List<WorkLogEntry> aWorkLogs) {
    if (aWorkLogs != null) {
      this.data = FXCollections.observableArrayList(convertFromWorkLogs(aWorkLogs));
      table.setItems(data);
    }
  }

  private void refreshData() {
    if (workLogs != null) {
      this.data = FXCollections.observableArrayList(convertFromWorkLogs(workLogs));
      table.setItems(data);
    }
  }

  private List<WorkLogWrapper> convertFromWorkLogs(List<WorkLogEntry> workLogs) {
    List<WorkLogWrapper> workLogWrappers = new ArrayList<WorkLogWrapper>();
    for (WorkLogEntry workLogEntry : workLogs) {
      workLogWrappers.add(new WorkLogWrapper(workLogEntry));
    }
    return workLogWrappers;
  }

  private List<WorkLogEntry> convertToWorkLogs(List<WorkLogWrapper> workLogs) {
    List<WorkLogEntry> result = new ArrayList<WorkLogEntry>();
    for (WorkLogWrapper wrapper : data) {
      WorkLogEntry entry = new WorkLogEntry(wrapper.getKey(), wrapper.getDesc(), new BigDecimal(wrapper.getHrs()));
      result.add(entry);
    }
    Collections.sort(result, new WorkLogEntryComparator());
    return result;
  }

  private void recalcTotal() {
    BigDecimal total = new BigDecimal("0.00");
    for (WorkLogWrapper workLogWrapper : data) {
      if (workLogWrapper.getHrs() != null) {
        try {
          BigDecimal hrs = new BigDecimal(workLogWrapper.getHrs());
          total = total.add(hrs);
        } catch (Exception ignored) {
        }
      }
    }
    totalLabel.setText("Total: " + total);
    if (expectedHrs != null) {
      if (total.doubleValue() == expectedHrs.doubleValue()) {
        totalLabel.setTextFill(Color.GREEN);
      } else {
        totalLabel.setTextFill(Color.RED);
      }
    }
  }

  public List<WorkLogEntry> getEdited() {
    return convertToWorkLogs(data);
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

    private boolean escapePressed;

    private TablePosition<WorkLogWrapper, ?> tablePos = null;

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
        escapePressed = false;
        final TableView<WorkLogWrapper> table = getTableView();
        tablePos = table.getEditingCell();
      }
    }

    @Override
    public void cancelEdit() {
      if (escapePressed) {
        // this is a cancel event after escape key
        super.cancelEdit();
        setText((String) getItem());
      } else {
        // this is not a cancel event after escape key
        // we interpret it as commit.
        commitEdit(textField.getText());
      }
      setGraphic(null);
    }

    @Override
    public void commitEdit(String newValue) {
      if (!isEditing())
        return;

      final TableView<WorkLogWrapper> table = getTableView();
      if (table != null) {
        // Inform the TableView of the edit being ready to be committed.
        CellEditEvent editEvent = new CellEditEvent(table, tablePos, TableColumn.editCommitEvent(), newValue);

        Event.fireEvent(getTableColumn(), editEvent);
      }

      // we need to setEditing(false):
      super.cancelEdit(); // this fires an invalid EditCancelEvent.

      // update the item within this cell, so that it represents the new value
      updateItem(newValue, false);

      if (table != null) {
        // reset the editing cell on the TableView
        table.edit(-1, null);

        // request focus back onto the table, only if the current focus
        // owner has the table as a parent (otherwise the user might have
        // clicked out of the table entirely and given focus to something else.
        // It would be rude of us to request it back again.
        // requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(table);
      }
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
      recalcTotal();
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
      textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
        public void handle(KeyEvent event) {
          if (event.getCode() == KeyCode.ESCAPE)
            escapePressed = true;
          else
            escapePressed = false;
        }
      });
    }

    private String getString() {
      return getItem() == null ? "" : getItem().toString();
    }
  }

  class ActionCell extends TableCell<WorkLogWrapper, String> {
    final Button deleteButton = new Button("Delete");

    ActionCell() {

      deleteButton.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent t) {
          data.remove(ActionCell.this.getTableRow().getItem());
          recalcTotal();
        }
      });
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(String value, boolean empty) {
      super.updateItem(value, empty);
      if (!empty) {
        setGraphic(deleteButton);
      } else {
        setGraphic(null);
      }
    }
  }
}