package net.conjur.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

import net.conjur.api.Conjur;

@Configuration
public class ConjurConfiguration {
  @Bean
  public ConjurPropertySource getConjurPropertySource() {
    return new ConjurPropertySource();
  }
}