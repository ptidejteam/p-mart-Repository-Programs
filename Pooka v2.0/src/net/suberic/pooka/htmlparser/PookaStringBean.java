package net.suberic.pooka.htmlparser;
import org.htmlparser.*;
import org.htmlparser.beans.*;
import org.htmlparser.util.ParserException;

public class PookaStringBean extends StringBean {

  public void setContent(String text, String charSet) {
    mParser = Parser.createParser(text, charSet);
    setStrings();
  }

  public String getURL() {
    return "";
  }
}
