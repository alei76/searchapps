package org.apache.solr.solrapp.client;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;

public class SClient {

  private static SolrClient solrClient;

  private String zkHost;
  private String collection;

  public SClient(String zkHost, String collection) { 
    this.zkHost = zkHost;
    this.collection = collection;
  }

  public void init() {
    solrClient = SConnection.getInstance(zkHost, collection);
  }

  public void close() {
    try {
      solrClient.close();
    } catch (IOException ignore) { }
  }

  public static SolrClient getInstance() {
    return solrClient;
  }

}
