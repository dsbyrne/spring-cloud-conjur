package net.conjur.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.conjur.api.Conjur;
import net.conjur.api.Variable;

public class ConjurSecretStore {
  private Conjur conjurApi;

  final static Logger log = LoggerFactory.getLogger(ConjurSecretStore.class);

  public ConjurSecretStore() {
    conjurApi = new ConjurBuilder()
      .mergeEnvironment()
      .mergeConfigurationPath("/etc/conjur.conf", null)
      .mergeConfigurationPath("~/.conjurrc", "~/.netrc")
      .build();
  }

  public String getProperty(String name) {
    String value = "";

    try {
      Variable conjurVar = conjurApi.variables().get(name);
      if(conjurVar.exists()) {
        try {
          value = conjurVar.getValue();
        }
        catch (Exception e) {
          log.error("Conjur variable \"{}\" exists, but is not instantiated with a value",
            name);
        }
      }
    }
    catch(Exception e) { 
      // Variable not found in Conjur.
      // This is okay, it may be elsewhere.
    }

    return value;
  }
}