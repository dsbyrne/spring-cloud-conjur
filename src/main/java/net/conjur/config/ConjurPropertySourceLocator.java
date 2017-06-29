package net.conjur.config;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment; 
import org.springframework.core.env.PropertySource; 
import org.springframework.beans.factory.annotation.Autowired;

public class ConjurPropertySourceLocator implements PropertySourceLocator {
  @Autowired
  private ConjurPropertySource cps;

  @Override
  public PropertySource<?> locate(Environment environment) {
    return cps;
  }
}