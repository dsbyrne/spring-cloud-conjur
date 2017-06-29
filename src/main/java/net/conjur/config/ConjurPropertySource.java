package net.conjur.config;

import org.springframework.core.env.EnumerablePropertySource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConjurPropertySource extends EnumerablePropertySource<ConjurSecretStore> {

  private ConjurSecretStore store;
  private final Map<String, String> properties = new LinkedHashMap<>();

  public ConjurPropertySource() {
    super("conjur");

    store = new ConjurSecretStore();
  }

  @Override
  public Object getProperty(String name) {
    String value = store.getProperty(name);
    if (value == "") {
      value = null;
    }
    return value;
  }

  @Override
  public String[] getPropertyNames() {
    Set<String> strings = this.properties.keySet();
		return strings.toArray(new String[strings.size()]);
  }
}