package me.h14r.invoicemaker;

import org.apache.commons.configuration.*;

public class ConfigurationProvider {

  public static String PROPERTY_FILE = "configuration.properties";

  private ConfigurationProvider() {
  }

  private static class ConfigurationHolder {
    private static final Configuration CONFIGURATION = getConfiguration();
  }

  public static Configuration getInstance() {
    return ConfigurationHolder.CONFIGURATION;
  }

  private static Configuration getConfiguration() {
    CompositeConfiguration configuration = new CompositeConfiguration();
    configuration.addConfiguration(new SystemConfiguration());
    try {
      configuration.addConfiguration(new PropertiesConfiguration(PROPERTY_FILE));
    } catch (ConfigurationException e) {
      e.printStackTrace();
      return null;
    }
    return configuration;
  }


}
