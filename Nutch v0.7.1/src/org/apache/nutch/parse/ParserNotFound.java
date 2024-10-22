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

package org.apache.nutch.parse;

import java.io.IOException;

public class ParserNotFound extends ParseException {
  private String url;
  private String contentType;

  public ParserNotFound(String url, String contentType) {
    this(url, contentType,
         "parser not found for contentType="+contentType+" url="+url);
  }

  public ParserNotFound(String url, String contentType, String message) {
    super(message);
    this.url = url;
    this.contentType = contentType;
  }

  public String getUrl() { return url; }
  public String getContentType() { return contentType; }
}
