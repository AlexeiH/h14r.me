package me.h14r.invoicemaker.xlsparser;

import me.h14r.invoicemaker.ConfigurationProvider;
import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import me.h14r.invoicemaker.api.WorkLogEntry;
import org.apache.commons.configuration.Configuration;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@Ignore
public class XLSWorkLogDataProviderTest {

	private static String TEST_FILENAME = "xlsx/input.xls";
	private static Integer RECORDS_COUNT = 26;
	
	@Test
	public void testXLSParsing() throws FileNotFoundException {
		Configuration configuration = ConfigurationProvider.getInstance();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(TEST_FILENAME);
		assertNotNull(inputStream);
		
		IWorkLogDataProvider xlsDataProvider = new XLSWorkLogDataProvider(configuration, inputStream);
		List<WorkLogEntry> workLogs = xlsDataProvider.getWorkLogs();
		assertNotNull(workLogs);
		
		assertSame(RECORDS_COUNT, workLogs.size());
	}
}
