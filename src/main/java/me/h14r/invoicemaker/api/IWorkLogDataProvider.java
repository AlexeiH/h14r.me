package me.h14r.invoicemaker.api;

import java.util.Collection;

public interface IWorkLogDataProvider {

  Collection<WorkLogEntry> getWorkLogs();

}
