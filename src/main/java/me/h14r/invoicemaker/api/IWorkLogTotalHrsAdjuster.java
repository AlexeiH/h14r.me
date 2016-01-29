package me.h14r.invoicemaker.api;

import java.math.BigDecimal;
import java.util.List;

public interface IWorkLogTotalHrsAdjuster {

  List<WorkLogEntry> adjustTotals(List<WorkLogEntry> workLogs, BigDecimal expectedHr);

}
