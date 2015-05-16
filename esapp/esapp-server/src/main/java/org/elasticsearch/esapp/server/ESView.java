package org.elasticsearch.esapp.server;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ESView implements IView {

  private static final String sep = StandardSystemProperty.FILE_SEPARATOR.value();

  private String publicDir;

  private SearchResults modelResults = new SearchResults();
  private Map<String, Object> viewResult = Maps.newLinkedHashMap();
  private String viewName = null;

  public ESView(String publicDir) {
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
    SearchResponse sres = modelResults.getSres(SearchResults.SRES);

    String qtime = String.valueOf(sres.getTookInMillis());
    SearchHits esDocumentList = sres.getHits();
    long numFound = esDocumentList.getTotalHits();
    String count = String.valueOf(numFound);

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
      viewResult.put(fe.getItemsKey(), fe.getItems(sres, setReqQ, facetElementList));
      if (!fe.isItemsEmpty()) {
        String fname = fe.getFieldName();
        viewResult.put(fname + "category", fname);
        viewResult.put(fname + "show", true);
      }
    }
    List<Map<String, Object>> docList = Lists.newArrayList();
    for (SearchHit doc : esDocumentList) {
      Map<String, Object> docMap = Maps.newLinkedHashMap();
      Optional<SearchHitField> urlField = Optional.ofNullable(doc.getFields().get("url"));
      String urlValue = (urlField.isPresent()) ? urlField.get().getValue() : null;
      docMap.put("titlehref", urlValue);
      HighlightField hlSnippets1 = doc.highlightFields().get("title");
      for (Text s : hlSnippets1.fragments()) {
        docMap.put("title", s.string());
      }
      HighlightField hlSnippets2 = doc.highlightFields().get("content");
      String content = Joiner.on(" ... ").skipNulls().join(hlSnippets2.fragments());
      docMap.put("content", content);
      List<String> tagArrList =Lists.newArrayList();
      Optional<SearchHitField> tags = Optional.ofNullable(doc.getFields().get("tags"));
      List<Object> tagsValues = (tags.isPresent()) ? tags.get().getValues() : Lists.newArrayList();
      for (Object o : tagsValues) {
        tagArrList.add(o.toString());
      }
      docMap.put("tags", tagArrList);
      docList.add(docMap);
    }
    viewResult.put("docItems", docList);
  }

}
