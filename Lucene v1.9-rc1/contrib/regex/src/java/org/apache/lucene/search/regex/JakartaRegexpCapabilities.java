package org.apache.lucene.search.regex;

import org.apache.regexp.RE;
import org.apache.regexp.RegexpTunnel;

public class JakartaRegexpCapabilities implements RegexCapabilities {
  private RE regexp;

  public void compile(String pattern) {
    regexp = new RE(pattern);
  }

  public boolean match(String string) {
    return regexp.match(string);
  }

  public String prefix() {
    char[] prefix = RegexpTunnel.getPrefix(regexp);
    return prefix == null ? null : new String(prefix);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final JakartaRegexpCapabilities that = (JakartaRegexpCapabilities) o;

    if (regexp != null ? !regexp.equals(that.regexp) : that.regexp != null) return false;

    return true;
  }

  public int hashCode() {
    return (regexp != null ? regexp.hashCode() : 0);
  }
}
