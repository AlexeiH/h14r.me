package me.h14r.invoicemaker.api;

import java.math.BigDecimal;
import java.util.Collection;

public interface IWorkLogTotalHrsAdjuster {

  Collection<WorkLogEntry> adjustTotals(Collection<WorkLogEntry> workLogs, BigDecimal expectedHr);

}
