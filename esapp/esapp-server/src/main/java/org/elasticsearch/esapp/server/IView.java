package org.elasticsearch.esapp.server;

public interface IView {
  public void execute(IModel model);
  public Object getResult();
}
