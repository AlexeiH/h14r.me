package me.h14r.invoicemaker.template;

import me.h14r.invoicemaker.api.ITemplateProcessor;
import me.h14r.invoicemaker.api.InvoiceValueHolder;

import java.io.OutputStream;

public class YARGTemplateProcessor implements ITemplateProcessor {

  private String templatePath;

  public YARGTemplateProcessor(String templatePath) {
    this.templatePath = templatePath;
  }

  public void generate(InvoiceValueHolder valueHolder, OutputStream outputStream) {
    // TODO: impl
  }
}
