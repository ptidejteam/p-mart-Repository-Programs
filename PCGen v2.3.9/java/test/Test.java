/**
 * Test.java
 * A test excercise for the FileContentParser class
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version  1.0
 */

import java.util.*;

public class Test implements iParsingClient
{
  public static void main(String[] args)
  {
    Test me = new Test();
    me.parseIt();
  }

  public void parseIt()
  {
    String[] tokens = { "\r\n", "\t" };

    FileContentParser fcp =
          new FileContentParser("test.lst", tokens, this);

    fcp.parse();
  }

  public void parseToken(String item)
  {
    System.out.println(item);
  }
}

