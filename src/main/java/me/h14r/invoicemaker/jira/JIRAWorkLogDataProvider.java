package me.h14r.invoicemaker.jira;

import me.h14r.invoicemaker.ConfigurationProvider;
import me.h14r.invoicemaker.api.IWorkLogDataProvider;
import me.h14r.invoicemaker.api.WorkLogEntry;
import me.h14r.invoicemaker.xlsparser.XLSWorkLogDataProvider;
import org.apache.commons.configuration.Configuration;
import org.apache.poi.util.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author andrey.tsvetkov
 *
 */
public class JIRAWorkLogDataProvider implements IWorkLogDataProvider {

	private static final String FILTER_DATE_FORMAT = "dd/MMM/yy";
	private static final String SET_COOKIE_HEADER = "Set-Cookie";
	private Configuration configuration;
	private Date fromDate;
	private Date toDate;

	public JIRAWorkLogDataProvider(Configuration configuration, Date fromDate, Date toDate) {
		this.configuration = configuration;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public List<WorkLogEntry> getWorkLogs() {
		InputStream xlsInputStream = getXlsData();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("c:\\temp\\testResp.dat");
			IOUtils.copy(xlsInputStream, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		XLSWorkLogDataProvider workLogProvider = new XLSWorkLogDataProvider(configuration, xlsInputStream);

		return workLogProvider.getWorkLogs();
	}

	private InputStream getXlsData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(FILTER_DATE_FORMAT, Locale.US);
		String from = dateFormat.format(fromDate);
		String to = dateFormat.format(toDate);
		from = from.replaceAll("/", "%2F");
		to = to.replaceAll("/", "%2F");

		String authenticationUrl = ConfigurationProvider.getInstance().getString("worklog.source.jira.login.url", "");
		String userName = ConfigurationProvider.getInstance().getString("worklog.source.jira.login.param.username", "");
		String password = ConfigurationProvider.getInstance().getString("worklog.source.jira.login.param.password", "");
		String getXlsUrl = ConfigurationProvider.getInstance().getString("worklog.source.jira.report.url", "");

		getXlsUrl = MessageFormat.format(getXlsUrl, from, to);
		InputStream xlsInputStream = null;

		try {

			List<String> cookies = sendAuthenticationRequest(authenticationUrl, userName, password);

			xlsInputStream = getXlsInputStream(getXlsUrl, xlsInputStream, cookies);
		} catch (IOException e) {
			System.out.println("Exception occured: " + e.getMessage());
		}
		return xlsInputStream;
	}

	private InputStream getXlsInputStream(String getXlsUrl, InputStream xlsInputStream, List<String> cookies)
			throws MalformedURLException, IOException {
		int responseCode;
		URL getXslUrlObject = new URL(getXlsUrl);
		HttpsURLConnection getXlsConnection = (HttpsURLConnection) getXslUrlObject.openConnection();

		setCookiesToConnection(getXlsConnection, cookies);

		getXlsConnection.setRequestProperty("Accept", "application/vnd.ms-excel,application/xml;");
		responseCode = getXlsConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			xlsInputStream = getXlsConnection.getInputStream();
		} else {
			System.out.println("Get xls file request failed");
		}
		return xlsInputStream;
	}

	private static List<String> sendAuthenticationRequest(String authenticationUrl, String userName, String password)
			throws MalformedURLException, IOException, ProtocolException {
		URL authenticationUrlObj = new URL(authenticationUrl);
		HttpsURLConnection authenticationConnection = (HttpsURLConnection) authenticationUrlObj.openConnection();
		String postParams = MessageFormat.format("os_username={0}&os_password={1}", userName, password);

		authenticationConnection.setRequestMethod("POST");
		authenticationConnection.setDoOutput(true);

		setParams(authenticationConnection, postParams);
		int responseCode = authenticationConnection.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			System.out.println("User is not authorized in Jira");
			return null;
		}
		List<String> cookies = authenticationConnection.getHeaderFields().get(SET_COOKIE_HEADER);
		return cookies;
	}

	private static void setParams(HttpsURLConnection authenticationConnection, String postParams) throws IOException {
		DataOutputStream requestOutputStream = new DataOutputStream(authenticationConnection.getOutputStream());
		requestOutputStream.writeBytes(postParams);
		requestOutputStream.flush();
		requestOutputStream.close();
	}

	private static void setCookiesToConnection(HttpsURLConnection getXlsConnection, List<String> cookies) {
		if (cookies != null) {
			for (String cookie : cookies) {
				getXlsConnection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
	}	
	
}
