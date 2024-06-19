/**
 * StringContentParser.java
 * Provides services for parsing a string that has nested tokens
 * defined by more than one set of delimiters.
 *
 * It assumes the the clients using it have implemented the
 * iParsingClient interface.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version  1.0
 */

import java.util.*;

public class StringContentParser
{

  private iParsingClient client;
  private String[] delimiters;
  private String content;

  private int currentDelimiter;
  private int delimiterLimit;


  /**
   * A helper function to avoid doing the same work in more
   * than one place.  Called by the constructor and one of
   * the parse variants, it sets the object's fields.
   *
   * @param content     the string to be parsed
   * @param delimiters  an array of delimiters to using when parsing
   *                    various elements of the string
   * @param client      and object that will receive the last
   *                    parsed item in the tree.
   */
  protected void setParms(String content,
                          String[] delimiters,
                          iParsingClient client)
  {
    this.delimiters = delimiters;
    this.client = client;
    this.content = content;
  }

  /**
   * A constructor that accepts all the details needed to
   * parse a string.  I did it this way mostly out of laziness.
   * I don't want to have to deal with checking to see if any
   * items are missing, and it fits in with my philosphy of
   * not letting an object get into a state where it can't
   * process.  Not that this is bulletproof, but...
   *
   * These parameters are merely passed on the the setParms
   * method.
   *
   * @param content     the string to be parsed
   * @param delimiters  an array of delimiters to using when parsing
   *                    various elements of the string
   * @param client      and object that will receive the last
   *                    parsed item in the tree.
   */
  public StringContentParser(String content,
                           String[] delimiters,
                           iParsingClient client)
  {
    setParms(content, delimiters, client);
  }

  /**
   * The main function visible to clients, this version accepts
   * the same information as the constructor.  This is to allow
   * reusing the object for another string or object without having
   * to endure the overhead of object creation and deletion.
   *
   * These parameters are merely passed on the the setParms
   * method.
   *
   * @param content     the string to be parsed
   * @param delimiters  an array of delimiters to using when parsing
   *                    various elements of the string
   * @param client      and object that will receive the last
   *                    parsed item in the tree.
   */
  public void parse(String content,
                    String[] delimiters,
                    iParsingClient client)
  {
    setParms(content, delimiters, client);
    parseTheContent();
  }

  /**
   * Another version of the main function which allows changing
   * the object that will do the final manipulation.
   *
   * @param client         and object that will receive the last
   *                       parsed item in the tree.
   */
  public void parse(iParsingClient client)
  {
    this.client = client;
    parseTheContent();
  }

  /**
   * Parses the string using the fields in the object.
   *
   */
  public void parse()
  {
    parseTheContent();
  }

  /**
   * Initiates the process of actually parsing the string.
   * This is protected because it's used only internally.
   */
  protected void parseTheContent()
  {
    delimiterLimit = delimiters.length - 1;
    currentDelimiter = 0;

//    System.out.println("In parseTheContent(): \n\tdelimiterLimit: " + delimiterLimit +
//                                          "\n\tcurrentDelimiter: " + currentDelimiter);

    parseItem(content, currentDelimiter);
  }


  /**
   * A recursive function which parses each element as defined
   * by the passed in delimiters.  When it reaches the point past which
   * it can no longer tokenize, it passes the current token back to
   * the client for processing.  This allows virutally unlimited
   * depth in terms of nested tokens, but has the disadvantage of
   * being recursive.  Defining too many levels of tokens in too
   * large a string could prove to be prohibitive in terms of resource
   * usage.
   *
   * The first time this is called, from the parseTheContent method,
   * it will be passed the entire contents of the string and an index
   * pointing to the first delimiter in the array.  It will tokenize
   * that using the first delimiter.
   *
   * If the current delimiter is the last one in the list, instead
   * of further tokenizing, this method will pass each of the
   * tokens obtained here back to the client for processing.
   *
   * If there are more delimiters, it will pass each token to a
   * recursive copy of the method, along with a number that
   * corresponds to the next delimiter in the array.
   *
   * @param itemToParse  the individiual item to parse
   * @param delimiter    a pointer to the delimiter to use in parsing
   *                     this item.
   *
   */
  protected void parseItem(String itemToParse, int delimiter)
  {

    //System.out.println("\nIn parseItem(): \nitemToParse: \n" + itemToParse + " \n\tparseToken: " + parseToken);

    StringTokenizer token =
        new StringTokenizer(itemToParse, delimiters[delimiter], false);

    while (token.hasMoreTokens())
    {
      String item = (String)token.nextToken();
      if (delimiter == delimiterLimit)
      {
        client.parseToken(item);
      }
      else
      {
        parseItem(item, delimiter + 1);
      }
    }
  }
}
