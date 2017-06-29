package net.conjur.config;

import org.springframework.beans.factory.annotation.Autowired;
import net.conjur.api.Conjur;

public class ConjurSecretStore {
  private Conjur conjurApi;

  public ConjurSecretStore() {
    conjurApi = new ConjurBuilder()
      .configurationPath("/etc/conjur.conf")
      .build();
  }

  public String getProperty(String name) {
    String value = "";

    try {
      value = conjurApi.variables().get(name).getValue();
    }
    catch(Exception e) { }

    return value;
  }
}