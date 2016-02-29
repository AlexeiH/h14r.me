package me.h14r.invoicemaker.template;

import me.h14r.invoicemaker.api.InvoiceValueHolder;
import me.h14r.invoicemaker.api.WorkLogEntry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataConverter {

  public static Map<String, Object> convertFirstLevelValueHolder(InvoiceValueHolder holder) {
    Map<String, Object> data = new HashMap<String, Object>();

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    DecimalFormat df = new DecimalFormat("#,##0.##");

    SimpleDateFormat dfFull = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));


    data.put("number", holder.getNumber());
    data.put("invoiceDate", sdf.format(holder.getInvoiceDate()));
    data.put("invoiceDateFull", dfFull.format(holder.getInvoiceDate()));
    data.put("startDate", sdf.format(holder.getInvoiceStart()));
    data.put("endDate", sdf.format(holder.getInvoiceEnd()));
    data.put("startDateFull", dfFull.format(holder.getInvoiceStart()));
    data.put("endDateFull", dfFull.format(holder.getInvoiceEnd()));
    data.put("totalHours", df.format(holder.getTotalHours()));
    data.put("totalAmount", df.format(holder.getTotalAmount()));
    data.put("totalAmountText", RussianMoney.digits2text(holder.getTotalAmount()));
    data.put("hourRate", df.format(holder.getHourRate()));
    return data;
  }

  public static List<Map<String, Object>> convertInnerLevelValueHolder(InvoiceValueHolder holder) {
    List<WorkLogEntry> workLogs = holder.getWorkLogs();
    if (workLogs != null) {
      DecimalFormat df = new DecimalFormat("#,##0.##");

      List<Map<String, Object>> workLogsReport = new ArrayList<Map<String, Object>>(workLogs.size());
      for (WorkLogEntry entry : workLogs) {
        Map<String, Object> entryReport = new HashMap<String, Object>();

        entryReport.put("key", entry.getKey());
        entryReport.put("workLog", entry.getWorkLog().replaceAll("('|\")", ""));
        entryReport.put("hours", df.format(entry.getHours()));

        workLogsReport.add(entryReport);
      }
      return workLogsReport;
    }
    return null;
  }

}
