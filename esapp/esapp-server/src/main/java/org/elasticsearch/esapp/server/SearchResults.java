package org.elasticsearch.esapp.server;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;

import com.google.common.collect.Maps;

public class SearchResults {

   private Map<String, String> strMap = Maps.newHashMap();
   private Map<String, Integer> intMap = Maps.newHashMap();
   private FacetElementList facetElementList = null;
   private Map<String, SearchResponse> sresMap = Maps.newHashMap();

   public static final String QUERY = "query";
   public static final String REQQ = "setReqQ";
   public static final String PAGE = "page";
   public static final String ROWS = "rows";
   public static final String REQPLEFT = "setReqPLeft";
   public static final String SRES = "sres";
   
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

   public void putSres(String key, SearchResponse value) {
     this.sresMap.put(key, value);
   }

   public SearchResponse getSres(String key) {
     return this.sresMap.get(key);
   }
}
