package me.h14r.invoicemaker.template;

import me.h14r.invoicemaker.api.InvoiceValueHolder;
import me.h14r.invoicemaker.api.WorkLogEntry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataConverter {

  public static Map<String, Object> convertFirstLevelValueHolder(InvoiceValueHolder holder) {
    Map<String, Object> data = new HashMap<String, Object>();

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    DecimalFormat df = new DecimalFormat("#,##0.0#");


    data.put("number", holder.getNumber());
    data.put("month", sdf.format(holder.getMonth()));
    data.put("totalHours", df.format(holder.getTotalHours()));
    data.put("totalAmount", df.format(holder.getTotalAmount()));

    Date month = holder.getMonth();
    Calendar cal = Calendar.getInstance();
    cal.setTime(month);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    data.put("month_start", sdf.format(cal.getTime()));

    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    data.put("month_end", sdf.format(cal.getTime()));

    data.put("currentDate", sdf.format(new Date()));
    return data;
  }


  public static List<Map<String, Object>> convertInnerLevelValueHolder(InvoiceValueHolder holder) {
    List<WorkLogEntry> workLogs = holder.getWorkLogs();
    if (workLogs != null) {
      DecimalFormat df = new DecimalFormat("#,##0.0#");

      List<Map<String, Object>> workLogsReport = new ArrayList<Map<String, Object>>(workLogs.size());
      for (WorkLogEntry entry : workLogs) {
        Map<String, Object> entryReport = new HashMap<String, Object>();

        entryReport.put("key", entry.getKey());
        entryReport.put("workLog", entry.getWorkLog());
        entryReport.put("hours", df.format(entry.getHours()));

        workLogsReport.add(entryReport);
      }
      return workLogsReport;
    }
    return null;
  }

}
