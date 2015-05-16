package org.elasticsearch.esapp.server;

import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

public class ESController implements IController {

  private IModel model = null;
  private IView view = null;

  private Long time = 0L;

  public ESController() { }

  @Override
  public void setModelAndView(IModel model, IView view) {
    this.model = Preconditions.checkNotNull(model);
    this.view = Preconditions.checkNotNull(view);	
  }

  @Override
  public Object getResult() {
    Stopwatch stopwatch  = Stopwatch.createStarted();
    time = 0L;

    view.execute(model);
    Object rtnObject = view.getResult();

    time = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    return rtnObject;
  }

  @Override
  public String getLogMsg(String method, String queryString) {
    String qstr = null;
    try {
      qstr = URLDecoder.decode(queryString, Charsets.UTF_8.name());
    } catch (Exception e) {
      qstr = queryString;
    }
    qstr = MoreObjects.firstNonNull(qstr, "");
    String logMsg = "method=" + method + " path=" + model.getAction()
            + " params={" + qstr + "} time=" + this.time;
	return logMsg;
  }
}
