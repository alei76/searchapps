package org.elasticsearch.esapp.server;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

public class ServerConfig {

  private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

  private static final String sep = StandardSystemProperty.FILE_SEPARATOR.value();

  private String homePath;
  private ImmutableMap<String,String> props;

  private String port;

  private String esAddresses;
  private String esClusterName;
  private String esIndex;
  private String esType;

  private String searchTemplate;

  public ServerConfig() {

  }

  public ServerConfig(String homeDir) {
    homePath = new File(homeDir).getAbsolutePath();
  }

  public void load() throws Exception {
    String configPath = homePath + sep + "conf" + sep + "server.cfg";
    Properties properties = new Properties();
    CharSource charSource= Files.asCharSource(new File(configPath), Charsets.UTF_8);
    try {
      properties.load(charSource.openStream());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw e;
    }
    props = Maps.fromProperties(properties);

    setPort(props.get("port"));
    setEsAddresses(props.get("esAddresses"));
    setEsClusterName(props.get("esClusterName"));
    setEsIndex(props.get("esIndex"));
    setEsType(props.get("esType"));

    String templatePath = homePath + sep + "conf" + sep + "searchtemplate.txt";
    String template = Files.toString(new File(templatePath), Charsets.UTF_8);
    setSearchTemplate(template);
  }

  public void setPort(String port) {
    this.port = Preconditions.checkNotNull(port);
    logger.info("port:" + port);
  }
  public String getPort() { return this.port; }

  public String getPublic() {
    return homePath + sep + "public";
  }

  public void setEsAddresses(String esAddresses) {
    this.esAddresses = Preconditions.checkNotNull(esAddresses);
    logger.info("esAddresses:" + this.esAddresses);
  }
  public String getEsAddresses() { return this.esAddresses; }

  public void setEsClusterName(String esClusterName) {
    this.esClusterName = Preconditions.checkNotNull(esClusterName);
    logger.info("esClusterName:" + this.esClusterName);
  }
  public String getEsClusterName() { return this.esClusterName; }

  public void setEsIndex(String esIndex) {
    this.esIndex = Preconditions.checkNotNull(esIndex);
    logger.info("esIndex:" + this.esIndex);
  }
  public String getEsIndex() { return this.esIndex; }

  public void setEsType(String esType) {
    this.esType = Preconditions.checkNotNull(esType);
    logger.info("esType:" + this.esType);
  }
  public String getEsType() { return this.esType; }

  public void setSearchTemplate(String searchTemplate) {
    this.searchTemplate = Preconditions.checkNotNull(searchTemplate);
    logger.info("searchTemplate:" + this.searchTemplate);
  }
  public String getSearchTemplate() { return this.searchTemplate; }

}
