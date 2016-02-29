package me.h14r.invoicemaker.api;

import java.util.Comparator;

public class WorkLogEntryComparator implements Comparator<WorkLogEntry> {
  public int compare(WorkLogEntry wl1, WorkLogEntry wl2) {
    if (wl1.getKey() == null && wl2.getKey() == null) {
      return 0;
    } else if (wl1.getKey() == null && wl2.getKey() != null) {
      return -1;
    } else if (wl1.getKey() != null && wl2.getKey() == null) {
      return 1;
    } else {
      return wl1.getKey().compareTo(wl2.getKey());
    }
  }
}
