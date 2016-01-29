package me.h14r.invoicemaker.template;

import com.haulmont.yarg.formatters.factory.DefaultFormatterFactory;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.GroovyDataLoader;
import com.haulmont.yarg.reporting.Reporting;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import com.haulmont.yarg.structure.ReportOutputType;
import com.haulmont.yarg.structure.impl.BandBuilder;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportTemplateBuilder;
import com.haulmont.yarg.util.groovy.DefaultScriptingImpl;
import me.h14r.invoicemaker.api.ITemplateProcessor;
import me.h14r.invoicemaker.api.InvoiceValueHolder;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class YARGTemplateProcessor implements ITemplateProcessor {

	private String templatePath;
	private String fileName;
	private ReportOutputType type;

	public YARGTemplateProcessor(String templatePath, String fileName, ReportOutputType type) {
		this.templatePath = templatePath;
		this.fileName = fileName;
		this.type = type;
	}

	public void generate(InvoiceValueHolder valueHolder, OutputStream outputStream) {
		try {
			ReportBuilder reportBuilder = new ReportBuilder();

			ReportTemplateBuilder reportTemplateBuilder = new ReportTemplateBuilder().documentPath(templatePath)
					.documentName(fileName).outputType(type).readFileFromPath();

			reportBuilder.template(reportTemplateBuilder.build());

			Map<String, Object> params = DataConverter.convertFirstLevelValueHolder(valueHolder);
			String paramsStr = JSONObject.toJSONString(params).replace("{", "[").replace("}", "]").replaceAll("\"", "\'");

			List<Map<String, Object>> tableData = DataConverter.convertInnerLevelValueHolder(valueHolder);
			String tableDataStr = JSONArray.toJSONString(tableData).replace("{", "[").replace("}", "]").replaceAll("\"",
					"\'");


			BandBuilder bandBuilder = new BandBuilder();
			ReportBand main = bandBuilder.name("Main").query("Main", "return [" + paramsStr + "]", "groovy").build();

			bandBuilder = new BandBuilder();
			ReportBand items = bandBuilder.name("Items").query("Items", "return " + tableDataStr + "", "groovy").build();

			reportBuilder.band(main);
			reportBuilder.band(items);

			Report report = reportBuilder.build();

			Reporting reporting = new Reporting();
			reporting.setFormatterFactory(new DefaultFormatterFactory());
			reporting.setLoaderFactory(new DefaultLoaderFactory().setGroovyDataLoader(new GroovyDataLoader(new
					DefaultScriptingImpl())));

			reporting.runReport(new RunParams(report), outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public static void main(String[] args) throws Exception {
		String template = "c:\\Work\\HackDay\\templates\\invoice.docx";
		OutputStream out = new FileOutputStream("c:\\Work\\HackDay\\templates\\invoice_result.docx");
		
		List<WorkLogEntry> workLogs = new ArrayList<WorkLogEntry>();
	    for (int i = 0; i < 2; i++) {
	      workLogs.add(new WorkLogEntry("2015-12-01", "Desc1", new BigDecimal(8)));
	      workLogs.add(new WorkLogEntry("2015-12-02", "Desc2", new BigDecimal(6)));
	      workLogs.add(new WorkLogEntry("2015-12-03", "Desc3", new BigDecimal(4)));
	    }
	    InvoiceValueHolder holder = new InvoiceValueHolder();
	    holder.setMonth(new Date());
	    holder.setNumber("1");
	    holder.setTotalAmount(new BigDecimal(1000.111));
	    holder.setTotalHours(new BigDecimal(97.956));
	    holder.setWorkLogs(workLogs);
		
		YARGTemplateProcessor processor = new YARGTemplateProcessor(template, "invoice_result.docx", ReportOutputType
		.docx);
		processor.generate(holder, out);
	}*/
}
