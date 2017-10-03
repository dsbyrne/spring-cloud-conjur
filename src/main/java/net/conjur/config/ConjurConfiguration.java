package net.conjur.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConjurConfiguration {
  @Bean
  public ConjurPropertySource getConjurPropertySource() {
    return new ConjurPropertySource();
  }
}