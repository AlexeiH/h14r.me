package me.h14r.invoicemaker.api;

import java.math.BigDecimal;

public class WorkLogEntry {

  private String key;
  private String workLog;
  private BigDecimal hours;

  public WorkLogEntry(String key, String workLog, BigDecimal hours) {
    this.key = key;
    this.workLog = workLog;
    this.hours = hours;
  }

  public String getWorkLog() {
    return workLog;
  }

  public void setWorkLog(String workLog) {
    this.workLog = workLog;
  }

  public BigDecimal getHours() {
    return hours;
  }

  public void setHours(BigDecimal hours) {
    this.hours = hours;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
