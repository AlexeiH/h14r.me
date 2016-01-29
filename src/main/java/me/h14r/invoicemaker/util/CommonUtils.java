package me.h14r.invoicemaker.util;

import me.h14r.invoicemaker.api.WorkLogEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtils {

  public static BigDecimal total(List<WorkLogEntry> workLogs) {
    BigDecimal total = new BigDecimal("0.00");
    for (WorkLogEntry workLog : workLogs) {
      if (workLog.getHours() != null) {
        total = total.add(workLog.getHours());
      }
    }
    return total;
  }

  public static void shuffleArray(int[] ar) {
    Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      int a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }
}
