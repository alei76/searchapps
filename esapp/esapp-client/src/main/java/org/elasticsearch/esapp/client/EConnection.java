package org.elasticsearch.esapp.client;

import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

public class EConnection {

  final static Logger logger = LoggerFactory.getLogger(EConnection.class);

  private static Client client;

  private EConnection() { }

  @SuppressWarnings("resource")
  public static Client getInstance(List<String> hostPortList, String clusterName) {
    if (client == null) {
      TransportAddress[] transportAddresses = new TransportAddress[hostPortList.size()];
      int i = 0;
      for (String hostPort : hostPortList) {
        List<String> address = Splitter.on(':').trimResults().omitEmptyStrings().splitToList(hostPort);
        TransportAddress inetSocketTransportAddress = new InetSocketTransportAddress(address.get(0), Integer.valueOf(address.get(1)));
        transportAddresses[i] = inetSocketTransportAddress;
        i++;
      }
      Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
      client = new TransportClient(settings).addTransportAddresses(transportAddresses);
    }
    return client;
  }

}
