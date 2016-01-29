package me.h14r.invoicemaker.gui;

import me.h14r.invoicemaker.api.WorkLogEntry;

import java.math.BigDecimal;
import java.util.List;

public interface IWorkLogEditor {

  void setData(List<WorkLogEntry> workLogEntries, BigDecimal expectedHrs);

  List<WorkLogEntry> getEdited();


}
