package me.h14r.invoicemaker.jira;

import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import org.apache.commons.configuration.Configuration;

import java.util.List;

public class JIRAWorkLogDataProvider implements IWorkLogDataProvider {

  private Configuration configuration;

  public JIRAWorkLogDataProvider(Configuration configuration) {
    this.configuration = configuration;
  }

  public List<WorkLogEntry> getWorkLogs() {
    //TODO: impl
    return null;
  }
}
