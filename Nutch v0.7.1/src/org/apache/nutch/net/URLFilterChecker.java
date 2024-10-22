/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.net;

import org.apache.nutch.plugin.Extension;
import org.apache.nutch.plugin.ExtensionPoint;
import org.apache.nutch.plugin.PluginRepository;

import org.apache.nutch.util.LogFormatter;

import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Checke one given filter or all filters.
 * 
 * @author John Xing
 */

public class URLFilterChecker {

  public static final Logger LOG =
    LogFormatter.getLogger(URLFilterChecker.class.getName());

  public URLFilterChecker() {}

  private void checkOne(String filterName) throws Exception {
    URLFilter filter = null;

    ExtensionPoint point =
      PluginRepository.getInstance().getExtensionPoint(URLFilter.X_POINT_ID);

    if (point == null)
      throw new RuntimeException(URLFilter.X_POINT_ID+" not found.");

    Extension[] extensions = point.getExtensions();

    for (int i = 0; i < extensions.length; i++) {
      Extension extension = extensions[i];
      filter = (URLFilter)extension.getExtensionInstance();
      //LOG.info(filter.getClass().getName());
      if (filter.getClass().getName().equals(filterName)) {
        break;
      } else {
        filter = null;
      }
    }

    if (filter == null)
      throw new RuntimeException("Filter "+filterName+" not found.");

    if (LogFormatter.hasLoggedSevere())
      throw new RuntimeException("Severe error encountered.");

    LOG.info("Checking URLFilter "+filterName);

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while((line=in.readLine())!=null) {
      String out=filter.filter(line);
      if(out!=null) {
        System.out.print("+");
        System.out.println(out);
      } else {
        System.out.print("-");
        System.out.println(line);
      }
    }
  }

  private void checkAll() throws Exception {
    LOG.info("Checking combination of all URLFilters available");

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while((line=in.readLine())!=null) {
      String out=URLFilters.filter(line);
      if(out!=null) {
        System.out.print("+");
        System.out.println(out);
      } else {
        System.out.print("-");
        System.out.println(line);
      }
    }
  }

  public static void main(String[] args) throws Exception {

    String usage = "Usage: URLFilterChecker (-filterName filterName | -allCombined)";

    if (args.length == 0) {
      System.err.println(usage);
      System.exit(-1);
    }

    String filterName = null;
    if (args[0].equals("-filterName")) {
      if (args.length != 2) {
        System.err.println(usage);
        System.exit(-1);
      }
      filterName = args[1];
    }

    URLFilterChecker checker = new URLFilterChecker();
    if (filterName != null) {
      checker.checkOne(filterName);
    } else {
      checker.checkAll();
    }

    System.exit(0);
  }
}
