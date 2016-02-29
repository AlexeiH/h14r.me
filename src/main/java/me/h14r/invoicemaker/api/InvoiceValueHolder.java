package me.h14r.invoicemaker.api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class InvoiceValueHolder {

  private String number;
  private Date invoiceStart;
  private Date invoiceEnd;
  private Date invoiceDate;
  private List<WorkLogEntry> workLogs;
  private BigDecimal totalHours;
  private BigDecimal totalAmount;
  private BigDecimal hourRate;


  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
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

  public Date getInvoiceStart() {
    return invoiceStart;
  }

  public void setInvoiceStart(Date invoiceStart) {
    this.invoiceStart = invoiceStart;
  }

  public Date getInvoiceEnd() {
    return invoiceEnd;
  }

  public void setInvoiceEnd(Date invoiceEnd) {
    this.invoiceEnd = invoiceEnd;
  }

  public Date getInvoiceDate() {
    return invoiceDate;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public BigDecimal getHourRate() {
    return hourRate;
  }

  public void setHourRate(BigDecimal hourRate) {
    this.hourRate = hourRate;
  }
}
