package org.apache.solr.solrapp.server;

import org.apache.solr.solrapp.client.SClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import spark.Spark;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

public class Server {

  final static Logger logger = LoggerFactory.getLogger(Server.class);

  private ServerConfig conf;

  public Server(ServerConfig config) throws Exception {
    this(config, true);
  }

  public Server(ServerConfig config, boolean useConfigFile) throws Exception {
    conf = config;
    if (useConfigFile) {
      conf.load();
    }
  }

  public void start() {
    Spark.port(MoreObjects.firstNonNull(Integer.valueOf(conf.getPort()), Spark.SPARK_DEFAULT_PORT));
    Spark.externalStaticFileLocation(conf.getPublic());

    SClient client = new SClient(conf.getZkHost(), conf.getCollection());
    client.init();

    SolrController contoller = new SolrController();

    Spark.get(SearchAction.SEARCH, (req, res) -> {
      SolrModel model = new SolrModel(req);
      model.setAction(SearchAction.SEARCH);
      SolrView view = new SolrView(conf.getPublic());
      contoller.setModelAndView(model, view);
      Object result = contoller.getResult();
      logger.info(contoller.getLogMsg(req.requestMethod(), req.queryString()));
      return new ModelAndView(result, view.getViewName());
    }, new MustacheTemplateEngine());

    Spark.post(SearchAction.SEARCH, (req, res) -> {
      SolrModel model = new SolrModel(req);
      model.setAction(SearchAction.SEARCH);
      SolrView view = new SolrView(conf.getPublic());
      contoller.setModelAndView(model, view);
      Object result = contoller.getResult();
      logger.info(contoller.getLogMsg(req.requestMethod(), req.queryString()));
      return new ModelAndView(result, view.getViewName());
    }, new MustacheTemplateEngine());

    Spark.get(SearchAction.ROOT, (req, res) -> {
      SolrModel model = new SolrModel(req);
      model.setAction(SearchAction.ROOT);
      SolrView view = new SolrView(conf.getPublic());
      contoller.setModelAndView(model, view);
      Object result = contoller.getResult();
      logger.info(contoller.getLogMsg(req.requestMethod(), req.queryString()));
      return new ModelAndView(result, view.getViewName());
    }, new MustacheTemplateEngine());

    Spark.post(SearchAction.ROOT, (req, res) -> {
      SolrModel model = new SolrModel(req);
      model.setAction(SearchAction.ROOT);
      SolrView view = new SolrView(conf.getPublic());
      contoller.setModelAndView(model, view);
      Object result = contoller.getResult();
      logger.info(contoller.getLogMsg(req.requestMethod(), req.queryString()));
      return new ModelAndView(result, view.getViewName());
    }, new MustacheTemplateEngine());

    Spark.post("/shutdown",
      (req, res) -> {
        logger.info("stopping...");
        SClient.getInstance().close();
        stop();
        return null;
      }
    );
  }

  public void stop() {
    Spark.stop();
  }
}
