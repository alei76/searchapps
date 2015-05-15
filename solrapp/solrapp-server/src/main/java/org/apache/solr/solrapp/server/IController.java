package org.apache.solr.solrapp.server;

public interface IController {
  public void setModelAndView(IModel model, IView view);
  public Object getResult();
  public String getLogMsg(String method, String queryString);
}
