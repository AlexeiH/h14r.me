package me.h14r.invoicemaker.util;

import me.h14r.invoicemaker.api.IWorkLogTotalHrsAdjuster;
import me.h14r.invoicemaker.api.WorkLogEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultWorkLogTotalHrsAdjuster implements IWorkLogTotalHrsAdjuster {

  private BigDecimal[] hours = new BigDecimal[]{new BigDecimal(1), new BigDecimal(2), new BigDecimal(2), new
      BigDecimal(2), new BigDecimal(3), new BigDecimal(4), new BigDecimal(4), new BigDecimal(4), new BigDecimal(5),
      new BigDecimal(6)};

  private BigDecimal maxPerDay = new BigDecimal("12");


  public List<WorkLogEntry> adjustTotals(List<WorkLogEntry> workLogs, BigDecimal expectedHrs) {
    List<WorkLogEntry> result = new ArrayList<WorkLogEntry>();
    result.addAll(workLogs);


    int[] indexes = new int[result.size()];
    for (int i = 0; i < indexes.length; i++) {
      indexes[i] = i;
    }
    CommonUtils.shuffleArray(indexes);

    BigDecimal totalHours = CommonUtils.total(workLogs);

    Random rnd = ThreadLocalRandom.current();
    if (totalHours.compareTo(expectedHrs) == 1) {
      // substract
      int idx = 0;
      while (totalHours.doubleValue() != expectedHrs.doubleValue()) {
        BigDecimal toSubstract = hours[rnd.nextInt(hours.length)];
        // calculate diff
        BigDecimal diff = totalHours.subtract(expectedHrs);
        if (diff.doubleValue() < toSubstract.doubleValue()) {
          toSubstract = diff;
        }
        // randomly picks an item
        int currentIdx = idx;
        while (true) {
          WorkLogEntry entry = result.get(idx++);
          if (idx >= indexes.length) {
            idx = 0;
            CommonUtils.shuffleArray(indexes);
          }
          if (currentIdx == idx) {
            break;
          }
          if (entry.getHours() != null) {
            if (entry.getHours().doubleValue() > toSubstract.doubleValue()) {
              entry.setHours(entry.getHours().subtract(toSubstract));
              totalHours = totalHours.subtract(toSubstract);
              break;
            }
          }
        }
      }
    } else if (totalHours.compareTo(expectedHrs) == -1 && maxPerDay.multiply(new BigDecimal(result.size()))
        .doubleValue() >= expectedHrs.doubleValue()) {
      // add
      int idx = 0;
      while (totalHours.doubleValue() != expectedHrs.doubleValue()) {
        BigDecimal toAdd = hours[rnd.nextInt(hours.length)];
        // calculate diff
        BigDecimal diff = expectedHrs.subtract(totalHours);
        if (toAdd.doubleValue() > diff.doubleValue()) {
          toAdd = diff;
        }
        // randomly picks an item
        int currentIdx = idx;
        while (true) {
          WorkLogEntry entry = result.get(idx++);
          if (idx >= indexes.length) {
            idx = 0;
            CommonUtils.shuffleArray(indexes);
          }
          if (currentIdx == idx) {
            break;
          }
          if (entry.getHours() != null) {
            if (entry.getHours().add(toAdd).doubleValue() <= maxPerDay.doubleValue()) {
              entry.setHours(entry.getHours().add(toAdd));
              totalHours = totalHours.add(toAdd);
              break;
            }
          }
        }
      }
    }
    return result;
  }


}
