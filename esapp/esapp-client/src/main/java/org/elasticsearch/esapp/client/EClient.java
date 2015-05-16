package org.elasticsearch.esapp.client;

import java.util.List;

import org.elasticsearch.client.Client;

import com.google.common.base.Splitter;

public class EClient {

  private static Client client;

  private List<String> hostPortList;
  private String clusterName;

  public EClient(String hostPort, String clusterName) {
    this.hostPortList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(hostPort);
    this.clusterName = clusterName;
  }

  public void init() {
    client = EConnection.getInstance(hostPortList, clusterName);
  }

  public static Client getInstance() {
    return client;
  }

  public void close() {
    client.close();
  }

}
