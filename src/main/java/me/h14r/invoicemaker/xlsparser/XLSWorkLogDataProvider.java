package me.h14r.invoicemaker.xlsparser;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import me.h14r.invoicemaker.api.WorkLogEntry;

public class XLSWorkLogDataProvider implements IWorkLogDataProvider {

  private InputStream inputStream;
  private Configuration configuration;
  
  public static final String GROUPING_KIND_JIRA = "jira";
  public static final String GROUPING_KIND_DATE = "date";

  public XLSWorkLogDataProvider(Configuration configuration, InputStream inputStream) {
    this.configuration = configuration;
    this.inputStream = inputStream;
  }

  public List<WorkLogEntry> getWorkLogs() {
	  
	String groupingConfig = configuration.getString("worklog.source.xls.groupingKind");
	
	int dateColumnIndex = configuration.getInt("worklog.source.xls.colIdx.date");
	int hoursColumnIndex = configuration.getInt("worklog.source.xls.colIdx.hours");
	int descriptionIndex = configuration.getInt("worklog.source.xls.colIdx.description");
	int idIndex = configuration.getInt("worklog.source.xls.colIdx.id");
	int workLogIndex = configuration.getInt("worklog.source.xls.colIdx.workLog");
	String[] workLogJIRAsArray = configuration.getString("worklog.source.xls.useWorkLogForIds")
			.split(",");
	List<String> workLogJIRAs = Arrays.asList(workLogJIRAsArray);
	String ignorableJIRARegexp = configuration.getString("worklog.source.xls.ignoreWorkLogIds");
	
	String dateFormat = configuration.getString("worklog.source.xls.dateFormat");
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
	
	int skipLines = configuration.getInt("worklog.source.xls.skipFirstLines");
	  
	try {
		HSSFWorkbook workbook = new HSSFWorkbook(this.inputStream);
		HSSFSheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		
		List<WorkLogEntry> workLogs = new ArrayList<WorkLogEntry>();
		
		for (int i = (0 + skipLines); i <= lastRowNum; i++) {
			HSSFRow row = sheet.getRow(i);
			
			HSSFCell issueKeyCell = row.getCell(idIndex);
			String issueKey = issueKeyCell.getStringCellValue();
			if (issueKey.matches(ignorableJIRARegexp)) {
				continue;
			}
			
			HSSFCell workLogCell = row.getCell(workLogIndex);
			String workLog = workLogCell.getStringCellValue();
			
			HSSFCell descriptionCell = row.getCell(descriptionIndex);
			String description = descriptionCell.getStringCellValue();
			
			HSSFCell hoursCell = row.getCell(hoursColumnIndex);
			Double hoursValue = hoursCell.getNumericCellValue();
			BigDecimal hours = BigDecimal.valueOf(hoursValue);
			
			HSSFCell workDateCell = row.getCell(dateColumnIndex);
			Date workDate = workDateCell.getDateCellValue();
			
			WorkLogEntry workLogEntry = null;
			if (GROUPING_KIND_DATE.equals(groupingConfig)) { // use date as the key
				String formattedDate = simpleDateFormat.format(workDate);
				// date, JIRA ID, hours
				workLogEntry = new WorkLogEntry(formattedDate, issueKey, hours);
				workLogs.add(workLogEntry);
			} else if (GROUPING_KIND_JIRA.equals(groupingConfig)) { // use JIRA number as the key
				// JIRA ID, description or worklog, hours
				workLogEntry = new WorkLogEntry(issueKey, workLogJIRAs.contains(issueKey) ? workLog : description, hours);
				workLogs.add(workLogEntry);
			} else {
				String incorrectGroupingMessage = MessageFormat
						.format("Incorrect grouping kind configuration: {0}!", groupingConfig);
				throw new RuntimeException(incorrectGroupingMessage);
			}
		}

		return workLogs;	
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
  }
}
