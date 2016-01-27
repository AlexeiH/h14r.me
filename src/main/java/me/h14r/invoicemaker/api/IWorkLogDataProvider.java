package me.h14r.invoicemaker.api;

import java.util.List;

public interface IWorkLogDataProvider {

  List<WorkLogEntry> getWorkLogs();

}
