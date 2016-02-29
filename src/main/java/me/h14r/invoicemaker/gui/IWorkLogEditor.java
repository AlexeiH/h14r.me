package me.h14r.invoicemaker.gui;

import me.h14r.invoicemaker.api.WorkLogEntry;

import java.math.BigDecimal;
import java.util.Collection;

public interface IWorkLogEditor {

  void setData(Collection<WorkLogEntry> workLogEntries);

  void setExpectedHours(BigDecimal expectedHours);

  Collection<WorkLogEntry> getEdited();


}
