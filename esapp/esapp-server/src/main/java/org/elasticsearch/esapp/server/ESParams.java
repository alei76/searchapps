package org.elasticsearch.esapp.server;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ESParams {

  private String source = null;

  private List<String> filters = Lists.newArrayList();
  private String matchAllFilter = "\"match_all\" : { }";
  private String boolMustFilter = "\"bool\" : { \"must\" : [ @array ] }";

  public ESParams(String source) {
    this.source = source;
  }

  public void set(String key, String value) {
    if ((key != null) && (value != null)) {
      source = source.replace(key, value);
    }
  }

  public void setFilterElement(String filter) {
    filters.add(filter);
  }

  public String getSource() {
    source = source.replace("@filter", getFilter());
    return source;
  }

  private String getFilter() {
    String filter = "";
    if (filters.size() <= 0) {
      filter = matchAllFilter;
    } else {
      String f1 = Joiner.on(",").skipNulls().join(filters);
      filter = boolMustFilter.replace("@array", f1);
    }
    return filter;
  }

}
