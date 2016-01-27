package me.h14r.invoicemaker.xlsparser;

import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import org.apache.commons.configuration.Configuration;

import java.io.InputStream;
import java.util.List;

public class XLSWorkLogDataProvider implements IWorkLogDataProvider {

  private InputStream inputStream;
  private Configuration configuration;

  public XLSWorkLogDataProvider(Configuration configuration, InputStream inputStream) {
    this.configuration = configuration;
    this.inputStream = inputStream;
  }

  public List<WorkLogEntry> getWorkLogs() {
    //TODO: impl
    return null;
  }
}
