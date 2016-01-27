package me.h14r.invoicemaker.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class InvoiceValueHolder {

  private String number;
  private Date month;
  private List<WorkLogEntry> workLogs;
  private BigDecimal totalHours;
  private BigDecimal totalAmount;

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public Date getMonth() {
    return month;
  }

  public void setMonth(Date month) {
    this.month = month;
  }

  public List<WorkLogEntry> getWorkLogs() {
    return workLogs;
  }

  public void setWorkLogs(List<WorkLogEntry> workLogs) {
    this.workLogs = workLogs;
  }

  public BigDecimal getTotalHours() {
    return totalHours;
  }

  public void setTotalHours(BigDecimal totalHours) {
    this.totalHours = totalHours;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }
}
