# spring-cloud-conjur
Allows `@Value` population from an external Conjur service using machine identity.
## Installation
This project is built with Maven. You can install it by running `mvn install` from the project's root directory.

## Usage
Install `spring-cloud-conjur` using Maven and add the following dependency to your `pom.xml`

```
<dependency>
  <groupId>net.conjur.config</groupId>
  <artifactId>spring-cloud-conjur</artifactId>
  <version>0.0.1</version>
</dependency>
```

Your application will now try to resolve `@Value` annotations by using the local machine's identity to retrieve secrets from Conjur. Currently, Conjur configuration files in `/etc/conjur.conf` or `~/.conjurrc` are supported.

## Examples
_DemoApplication.yml_  
[See more here](https://github.com/dsbyrne/conjur-spring-demo)

```
package com.example.demo;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class DemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Value("${demo-app/api-key}")
  String password;

  @PostConstruct
  private void postConstruct() {
    System.out.println("My password is: " + password);
  }
}

```

The above application will look to Conjur to populate the value from a variable named `demo-app/api-key`.