package org.apache.solr.solrapp.client;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

public class SConnection {

  private static CloudSolrClient solrClient;

  private SConnection() { }

  public static SolrClient getInstance(String zkHost, String collection) {
    if (solrClient == null) {
      solrClient = new CloudSolrClient(zkHost);
      solrClient.setDefaultCollection(collection);
    }
    return solrClient;
  }

}
