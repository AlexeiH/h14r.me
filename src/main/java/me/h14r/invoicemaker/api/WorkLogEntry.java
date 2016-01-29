package me.h14r.invoicemaker.api;

import java.math.BigDecimal;
import java.util.Date;

public class WorkLogEntry {

  private String key;
  private String workLog;
  private BigDecimal hours;
  private Date workDate;

  public WorkLogEntry(String key, String workLog, BigDecimal hours, Date workDate) {
    this.key = key;
    this.workLog = workLog;
    this.hours = hours;
    this.workDate = workDate;
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

  public Date getWorkDate() {
	return workDate;
  }
	
  public void setWorkDate(Date workDate) {
	this.workDate = workDate;
  }
}