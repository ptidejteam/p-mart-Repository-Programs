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

/**
 * Interface used to limit which URLs enter Nutch.
 * Used by the injector and the db updater.
 */

public interface URLFilter {
  /** The name of the extension point. */
  public final static String X_POINT_ID = URLFilter.class.getName();

  /* Interface for a filter that transforms a URL: it can pass the
     original URL through or "delete" the URL by returning null */
  public String filter(String urlString);
}
