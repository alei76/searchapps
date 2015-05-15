package org.apache.solr.solrapp.server;

public interface IView {
  public void execute(IModel model);
  public Object getResult();
}
