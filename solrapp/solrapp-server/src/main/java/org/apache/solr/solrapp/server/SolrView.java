package org.apache.solr.solrapp.server;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SolrView implements IView {

  private static final String sep = StandardSystemProperty.FILE_SEPARATOR.value();

  private String publicDir;

  private SearchResults modelResults = new SearchResults();
  private Map<String, Object> viewResult = Maps.newLinkedHashMap();
  private String viewName = null;

  public SolrView(String publicDir) {
    this.publicDir = publicDir;
  }

  @Override
  public void execute(IModel model) {
    Preconditions.checkNotNull(model);

    model.execute();
    this.modelResults = (SearchResults) model.getResult();

    String action = Preconditions.checkNotNull(model.getAction());

    switch(action) {
    case SearchAction.ROOT:
    case SearchAction.SEARCH:
      this.createSearchResult();
      this.viewName = publicDir+sep+"templates"+sep+"searchresults.mustache";
      break;
    default:
      break;
    }
  }

  @Override
  public Object getResult() {
    return this.viewResult;
  }

  public String getViewName() {
    return this.viewName;
  }

  private void createSearchResult() {
    QueryResponse qres = modelResults.getQres(SearchResults.QRES);

    String qtime = String.valueOf(qres.getQTime());
    SolrDocumentList solrDocumentList = qres.getResults();
    long numFound = solrDocumentList.getNumFound();
    String count = String.valueOf(numFound);
    Map<String, Map<String, List<String>>> hlMap = qres.getHighlighting();

    String query = modelResults.getStr(SearchResults.QUERY);
    int page = modelResults.getInt(SearchResults.PAGE);
    String setReqQ = modelResults.getStr(SearchResults.REQQ);
    FacetElementList facetElementList = modelResults.getFacetElementList();
    String setReqFE = facetElementList.getAllRequestParam();
    String setReqPLeft = modelResults.getStr(SearchResults.REQPLEFT);
    int rows = modelResults.getInt(SearchResults.ROWS);

    viewResult.put("query", query);
    viewResult.put("page", page);
    viewResult.put("count", count);
    viewResult.put("qtime", qtime);
    String prevClass = (page <= 1) ? "disabled" : "";
    String prev = (page <= 1) ? "" : setReqQ + setReqFE + setReqPLeft + (page - 1);
    double maxPage = Math.ceil((double)numFound/(double)rows);
    String nextClass = (maxPage < (page + 1)) ? "disabled" : "";
    String next = (maxPage < (page + 1)) ? "" : setReqQ + setReqFE + setReqPLeft + (page + 1);
    viewResult.put("prevclass", prevClass);
    viewResult.put("prev", prev);
    viewResult.put("nextclass", nextClass);
    viewResult.put("next", next);
    for (FacetElement fe : facetElementList) {
      viewResult.put(fe.getItemsKey(), fe.getItems(qres, setReqQ, facetElementList));
      if (!fe.isItemsEmpty()) {
        String fname = fe.getFieldName();
        viewResult.put(fname + "category", fname);
        viewResult.put(fname + "show", true);
      }
    }
    List<Map<String, Object>> docList = Lists.newArrayList();
    for (SolrDocument doc : solrDocumentList) {
      Map<String, Object> docMap = Maps.newLinkedHashMap();
      docMap.put("titlehref", doc.getFieldValue("url"));
      Map<String, List<String>> hlSnippetsMap = hlMap.get(doc.get("id"));
      for (String s : hlSnippetsMap.get("title")) {
        docMap.put("title", s);
      }
      StringBuilder sb = new StringBuilder();
      for (String s : hlSnippetsMap.get("content")) {
        sb.append(s).append(" ... ");
      }
      //sb.setLength(sb.length() - 5);
      docMap.put("content", sb.toString());
      List<String> tagArrList =Lists.newArrayList();
      for (Object o : doc.getFieldValues("tags")) {
        tagArrList.add(o.toString());
      }
      docMap.put("tags", tagArrList);
      docList.add(docMap);
    }
    viewResult.put("docItems", docList);
  }

}
