package org.apache.solr.solrapp.server;

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

  private String path;
  private String port;

  private String zkHost;
  private String collection;

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
    setZkHost(props.get("zkHost"));
    setCollection(props.get("collection"));
  }

  public void setPort(String port) {
    this.port = Preconditions.checkNotNull(port);
    logger.info("port:" + port);
  }
  public String getPort() { return this.port; }

  public String getPublic() {
    return homePath + sep + "public";
  }

  public void setZkHost(String zkHost) {
    this.zkHost = Preconditions.checkNotNull(zkHost);
    logger.info("zkHost:" + this.zkHost);
  }
  public String getZkHost() { return this.zkHost; }

  public void setCollection(String collection) {
    this.collection = Preconditions.checkNotNull(collection);
    logger.info("collection:" + this.collection);
  }
  public String getCollection() { return this.collection; }

}
