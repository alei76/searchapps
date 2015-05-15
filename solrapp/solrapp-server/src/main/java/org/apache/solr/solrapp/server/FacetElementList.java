package org.apache.solr.solrapp.server;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class FacetElementList implements Iterable<FacetElement>{

  private List<FacetElement> list = Lists.newArrayList();

  public FacetElementList() { }

  public void add(FacetElement fe) {
    list.add(fe);
  }

  @Override
  public Iterator<FacetElement> iterator() {
    return list.iterator();
  }

  public String getAllRequestParam() {
    StringBuilder sb = new StringBuilder().append("");
    for(FacetElement fe : list) {
      String reqestParam = fe.getRequestParam();
      if (reqestParam.length() > 0) {
        sb.append(reqestParam);
      }
    }
    return sb.toString();
  }

}
