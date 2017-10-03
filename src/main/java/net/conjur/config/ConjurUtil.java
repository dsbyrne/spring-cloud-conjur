package net.conjur.config;

import java.io.File;

public class ConjurUtil {
  public static String expandHome(String path) {
    if (path != null && path.startsWith("~" + File.separator)) {
      path = System.getProperty("user.home") + path.substring(1);
    }

    return path;
  }
}
