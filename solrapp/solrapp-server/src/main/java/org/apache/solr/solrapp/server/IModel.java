package org.apache.solr.solrapp.server;

public interface IModel {
  public void setAction(String action);
  public String getAction();
  public void execute();
  public Object getResult();
}
