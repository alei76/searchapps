package org.apache.solr.solrapp.server;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FacetElement {

  private String simpleSearchParams = null;
  private String fieldName = null;

  private String requestParamLeftSide = "";
  private String requestParam = "";
  private List<String> paramList = Lists.newArrayList();
  
  private boolean isItemsEmpty = false;

  public FacetElement(String simpleSearchParams, String fieldName) {
    this.simpleSearchParams = simpleSearchParams;
    this.fieldName = fieldName;
  }

  public void process(spark.Request req, ModifiableSolrParams params) {
    String queryParams = req.queryParams(simpleSearchParams);
    requestParamLeftSide = "&"+simpleSearchParams+"=";
    if (queryParams != null) {
      paramList = Splitter.on(',').trimResults().splitToList(queryParams);
      if (paramList.size() > 0) {
        for (String s : paramList) {
          params.add("fq", "{!term f=" + fieldName + "}" + s);
        }
        requestParam = requestParamLeftSide + queryParams;
      }
    }
  }

  public String getRequestParam() {
    return requestParam;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getItemsKey() {
    return fieldName + "Items";
  }

  public List<Map<String, Object>> getItems(QueryResponse qres, String setReqQ, FacetElementList fEList) {
    List<Map<String, Object>> itemsList = Lists.newArrayList();

    FacetField facetField = qres.getFacetField(fieldName);
    if (facetField == null) {
      isItemsEmpty = true;
      return itemsList;
    }
    List<FacetField.Count> fvals = facetField.getValues();
    if (fvals.isEmpty()) {
      isItemsEmpty = true;
      return itemsList;
    }

    FacetElementList prevFacetElementList = new FacetElementList();
    FacetElementList nextFacetElementList = new FacetElementList();
    boolean next = false;
    for (FacetElement fe : fEList) {
      if (fe.getFieldName().equals(fieldName)) {
        next = true;
        continue;
      }
      if (!next) {
        prevFacetElementList.add(fe);
      } else {
        nextFacetElementList.add(fe);
      }
    }
    String otherPrevRequestParam = prevFacetElementList.getAllRequestParam();
    String otherNextRequestParam = nextFacetElementList.getAllRequestParam();
 
    for (FacetField.Count ffc : fvals) {
      Map<String, Object> itemMap = Maps.newLinkedHashMap();
      String name = ffc.getName();
      String sClass = paramList.contains(name) ? "active" : "";
      itemMap.put(fieldName + "class", sClass);
      String setRequestParam = "";
      StringBuilder setReqRightSide = new StringBuilder().append("");
      List<String> paramListNew = Lists.newArrayList(paramList);
      if (paramListNew.contains(name)) {
        paramListNew.remove(name);
      } else {
        paramListNew.add(name);
      }
      if (paramListNew.size() > 0) {
        for (String s : paramListNew) {
          setReqRightSide.append(s).append(",");
        }
        setReqRightSide.setLength(setReqRightSide.length() - 1);
        setRequestParam = requestParamLeftSide + setReqRightSide.toString();
      }
      String sHref = setReqQ + otherPrevRequestParam + setRequestParam + otherNextRequestParam;
      itemMap.put(fieldName + "href", sHref);
      itemMap.put(fieldName + "name", name);
      itemMap.put(fieldName + "count", ffc.getCount());
      itemsList.add(itemMap);
    }

    return itemsList;
  }

  public boolean isItemsEmpty() {
    return isItemsEmpty;
  }

}
