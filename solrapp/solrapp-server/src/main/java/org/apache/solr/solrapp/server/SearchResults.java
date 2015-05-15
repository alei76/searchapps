package org.apache.solr.solrapp.server;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

import com.google.common.collect.Maps;

public class SearchResults {

   private Map<String, String> strMap = Maps.newHashMap();
   private Map<String, Integer> intMap = Maps.newHashMap();
   private FacetElementList facetElementList = null;
   private Map<String, QueryResponse> qresMap = Maps.newHashMap();

   public static final String QUERY = "query";
   public static final String REQQ = "setReqQ";
   public static final String PAGE = "page";
   public static final String ROWS = "rows";
   public static final String REQPLEFT = "setReqPLeft";
   public static final String QRES = "qres";
   
   public SearchResults() {  }

   public void putStr(String key, String value) {
     this.strMap.put(key, value);
   }

   public String getStr(String key) {
     return this.strMap.get(key);
   }

   public void putInt(String key, int value) {
     this.intMap.put(key, value);
   }

   public int getInt(String key) {
     return this.intMap.get(key);
   }

   public void setFacetElementList(FacetElementList value) {
     this.facetElementList = value;
   }

   public FacetElementList getFacetElementList() {
     return this.facetElementList;
   }

   public void putQres(String key, QueryResponse value) {
     this.qresMap.put(key, value);
   }

   public QueryResponse getQres(String key) {
     return this.qresMap.get(key);
   }
}
