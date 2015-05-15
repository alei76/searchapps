package org.apache.solr.solrapp.server;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.solrapp.client.SClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class SolrModel implements IModel {

  final static Logger logger = LoggerFactory.getLogger(SolrModel.class);

  private String action = null;
  private spark.Request req = null;
  private SearchResults results = new SearchResults();

  public SolrModel(spark.Request request) {
    req = request;
  }

  @Override
  public void setAction(String action) {
    this.action = Preconditions.checkNotNull(action);
  }

  @Override
  public String getAction() {
    return this.action;
  }

  @Override
  public void execute() {
    Preconditions.checkNotNull(this.action);

    switch(this.action) {
    case SearchAction.ROOT:
    case SearchAction.SEARCH:
      this.executeSearch();
      break;
    default:
      break;
    }
  }

  @Override
  public Object getResult() {
    return this.results;
  }

  private void executeSearch() {
    ModifiableSolrParams params = new ModifiableSolrParams();

    String query = MoreObjects.firstNonNull(req.queryParams(SearchParams.Q), "");
    results.putStr(SearchResults.QUERY, query);
    String setReqQ = SearchAction.SEARCH+"?"+SearchParams.Q+"="+query;
    results.putStr(SearchResults.REQQ, setReqQ);
    String clientquery = (query.equals("")) ? "*:*" : query;
    params.add("q", clientquery);

    FacetElementList facetElementList = new FacetElementList();

    FacetElement tagsFE = new FacetElement(SearchParams.TAGS, "tags");
    tagsFE.process(req, params);
    facetElementList.add(tagsFE);
    FacetElement locationFE = new FacetElement(SearchParams.NEE_LOCATION, "locations");
    locationFE.process(req, params);
    facetElementList.add(locationFE);
    FacetElement personFE = new FacetElement(SearchParams.NEE_PERSON, "persons");
    personFE.process(req, params);
    facetElementList.add(personFE);
    FacetElement foodFE = new FacetElement(SearchParams.NEE_FOOD, "foods");
    foodFE.process(req, params);
    facetElementList.add(foodFE);
    FacetElement eventFE = new FacetElement(SearchParams.NEE_EVENT, "events");
    eventFE.process(req, params);
    facetElementList.add(eventFE);

    results.setFacetElementList(facetElementList);

    String pageParam = MoreObjects.firstNonNull(req.queryParams(SearchParams.PAGE), "1");
    int page = Integer.parseInt(pageParam);
    results.putInt(SearchResults.PAGE, page);
    int rows = 10; // TODO into properties file
    results.putInt(SearchResults.ROWS, rows);
    String setReqPLeft = "&"+SearchParams.PAGE+"=";
    results.putStr(SearchResults.REQPLEFT, setReqPLeft);
    int startOffset = (page - 1) * rows;
    params.add("start", String.valueOf(startOffset));

    QueryResponse qres = null;
    try {
      qres = SClient.getInstance().query(params);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      Spark.halt(500, e.getMessage());
    }
    results.putQres(SearchResults.QRES, qres);
  }
}
