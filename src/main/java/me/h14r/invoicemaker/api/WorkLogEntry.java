package me.h14r.invoicemaker.api;

import java.math.BigDecimal;
import java.util.Date;

public class WorkLogEntry {

  private Date date;
  private String key;
  private String workLog;
  private BigDecimal hours;

  public WorkLogEntry(Date date, String key, String workLog, BigDecimal hours) {
    this.date = date;
    this.key = key;
    this.workLog = workLog;
    this.hours = hours;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
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
