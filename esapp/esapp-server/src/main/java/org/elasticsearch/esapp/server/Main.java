package org.elasticsearch.esapp.server;

import com.google.common.base.Preconditions;

public class Main {

  private Server server;

  public static void main(String[] args) {
    new Main().execute();
  }

  public Main() {
    String homeDir = Preconditions.checkNotNull(System.getProperty("esapp.server.home"));
    ServerConfig config = new ServerConfig(homeDir);
    try {
      server = new Server(config);
    } catch (Exception e) {
      System.exit(1);
    }
  }

  public void execute() {
    server.start();
  }

}
