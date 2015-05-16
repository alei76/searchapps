package org.elasticsearch.esapp.server;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.esapp.client.EClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class ESModel implements IModel {

  final static Logger logger = LoggerFactory.getLogger(ESModel.class);

  private String action = null;
  private spark.Request req = null;
  private String template;
  private String index;
  private String type;
  private SearchResults results = new SearchResults();
  
  public ESModel(spark.Request request, String template, String index, String type) {
    req = request;
    this.template = template;
    this.index = index;
    this.type = type;
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
    ESParams params = new ESParams(template);

    String query = MoreObjects.firstNonNull(req.queryParams(SearchParams.Q), "");
    results.putStr(SearchResults.QUERY, query);
    String setReqQ = SearchAction.SEARCH+"?"+SearchParams.Q+"="+query;
    results.putStr(SearchResults.REQQ, setReqQ);
    String clientquery = (query.equals("")) ? "*:*" : query;
    params.set("@query", clientquery);

    FacetElementList facetElementList = new FacetElementList();

    FacetElement tagsFE = new FacetElement(SearchParams.TAGS, "tags");
    tagsFE.process(req, params);
    facetElementList.add(tagsFE);

    results.setFacetElementList(facetElementList);

    String pageParam = MoreObjects.firstNonNull(req.queryParams(SearchParams.PAGE), "1");
    int page = Integer.parseInt(pageParam);
    results.putInt(SearchResults.PAGE, page);
    int rows = 10; // TODO into properties file
    results.putInt(SearchResults.ROWS, rows);
    String setReqPLeft = "&"+SearchParams.PAGE+"=";
    results.putStr(SearchResults.REQPLEFT, setReqPLeft);
    int startOffset = (page - 1) * rows;
    params.set("@from", String.valueOf(startOffset));

    SearchResponse sres = null;
    try {
      sres =  EClient.getInstance()
                .prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setSource(params.getSource())
                .get();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      Spark.halt(500, e.getMessage());
    }

    String shardFailures = Joiner.on(",").skipNulls().join(sres.getShardFailures());
    if (shardFailures.length() > 0) logger.error(shardFailures);

    results.putSres(SearchResults.SRES, sres);
  }
}
