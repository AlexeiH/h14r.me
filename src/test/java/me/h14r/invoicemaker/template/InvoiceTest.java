package me.h14r.invoicemaker.template;


import com.haulmont.yarg.formatters.factory.DefaultFormatterFactory;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.GroovyDataLoader;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import com.haulmont.yarg.reporting.Reporting;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.ReportBand;
import com.haulmont.yarg.structure.ReportOutputType;
import com.haulmont.yarg.structure.impl.BandBuilder;
import com.haulmont.yarg.structure.impl.ReportBuilder;
import com.haulmont.yarg.structure.impl.ReportFieldFormatImpl;
import com.haulmont.yarg.structure.impl.ReportTemplateBuilder;
import com.haulmont.yarg.structure.xml.impl.DefaultXmlReader;
import com.haulmont.yarg.util.groovy.DefaultScriptingImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;

public class InvoiceTest {
  @Test
  @Ignore
  public void testInvoiceReport() throws Exception {
    InputStream inputStream = getClass().getResourceAsStream("/template/invoice.xml");
    Report report = new DefaultXmlReader().parseXml(IOUtils.toString(inputStream, "UTF-8"));

    Reporting reporting = new Reporting();
    DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory();
    reporting.setFormatterFactory(formatterFactory);
    reporting.setLoaderFactory(new DefaultLoaderFactory().setGroovyDataLoader(new GroovyDataLoader(new
        DefaultScriptingImpl())));

    ReportOutputDocument reportOutputDocument = reporting.runReport(new RunParams(report), new FileOutputStream
        ("invoice.pdf"));
  }


  @Test
  @Ignore
  public void testInvoiceReportRaw() throws Exception {
    ReportBuilder reportBuilder = new ReportBuilder();
    ReportTemplateBuilder reportTemplateBuilder = new ReportTemplateBuilder().documentPath("c:\\sandbox\\h14r" +
        ".Me\\src\\test\\resources\\template\\invoice.docx").documentName("invoice.docx").outputType(ReportOutputType
        .docx).readFileFromPath();
    reportBuilder.template(reportTemplateBuilder.build());
    BandBuilder bandBuilder = new BandBuilder();
    ReportBand main = bandBuilder.name("Main").query("Main", "return [\n" +
        "                              [\n" +
        "                               'invoiceNumber':99987,\n" +
        "                               'client' : 'Google Inc.',\n" +
        "                               'date' : new Date(),\n" +
        "                               'addLine1': '1600 Amphitheatre Pkwy',\n" +
        "                               'addLine2': 'Mountain View, USA',\n" +
        "                               'addLine3':'CA 94043',\n" +
        "                               'signature': '<html><body><span style=\"color:red\">Mr. " +
        "Yarg</span></body></html>',\n" +
        "                               'footer' : '<html><body><b><span style=\"color:green;font-weight:bold;\">The " +
        "invoice footer</span></b></body></html>' \n" +
        "                            ]]", "groovy").build();


    bandBuilder = new BandBuilder();
    //    ReportBand items = bandBuilder.name("Items").query("Items", "return [\n" +
    //        "                                ['name':'Java Concurrency in practice', 'price' : 15000],\n" +
    //        "                                ['name':'Clear code', 'price' : 13000],\n" +
    //        "                                ['name':'Scala in action', 'price' : 12000]\n" +
    //        "                            ]", "groovy").build();

    ReportBand items = bandBuilder.name("Items").query("Items", "return [\n" +
        "                                ['name':'Java Concurrency in practice', 'price' : 15000],\n" +
        "                                ['name':'Clear code', 'price' : 13000],\n" +
        "                                ['name':'Scala in action', 'price' : 12000]\n" +
        "                            ]", "groovy").build();

    reportBuilder.band(main);
    reportBuilder.band(items);
    reportBuilder.format(new ReportFieldFormatImpl("Main.signature", "${html}"));
    reportBuilder.format(new ReportFieldFormatImpl("Main.footer", "${html}"));

    Report report = reportBuilder.build();

    Reporting reporting = new Reporting();
    reporting.setFormatterFactory(new DefaultFormatterFactory());
    reporting.setLoaderFactory(new DefaultLoaderFactory().setGroovyDataLoader(new GroovyDataLoader(new
        DefaultScriptingImpl())));

    ReportOutputDocument reportOutputDocument = reporting.runReport(new RunParams(report), new FileOutputStream
        ("invoice.docx"));
  }


}