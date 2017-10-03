package net.conjur.config;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment; 
import org.springframework.core.env.PropertySource; 
import org.springframework.beans.factory.annotation.Autowired;

public class ConjurPropertySourceLocator implements PropertySourceLocator, PriorityOrdered {
  @Autowired
  private ConjurPropertySource cps;

  @Override
  public PropertySource<?> locate(Environment environment) {
    return cps;
  }

  @Override
  public int getOrder() {
    // The Conjur property source will run last,
    // overwriting any existing variables
    return Ordered.HIGHEST_PRECEDENCE;
  }
}