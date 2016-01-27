package me.h14r.invoicemaker.api;

import java.io.OutputStream;

public interface ITemplateProcessor {

  void generate(InvoiceValueHolder valueHolder, OutputStream outputStream);

}
