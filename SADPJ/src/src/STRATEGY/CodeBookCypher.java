package src.STRATEGY;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CodeBookCypher implements EncryptionStrategy {

  HashMap codeContents = new HashMap();

  private void populateCodeEntries() {
    codeContents.put("This","Design");
    codeContents.put("is","Patterns");
    codeContents.put("a","are");
    codeContents.put("true","really");
    codeContents.put("statement","useful");
    //.........
    //........
  }

  public String encrypt(String inputData) {

    populateCodeEntries();

    String outStr = "";

    StringTokenizer st = new StringTokenizer(inputData);
    while (st.hasMoreTokens()) {
      outStr = outStr + " " +
               codeContents.get(st.nextToken());
    }

    return new String(outStr);
  }

}
