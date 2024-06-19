package net.suberic.pooka.resource;

import net.suberic.util.*;
import net.suberic.pooka.ssl.*;
import net.suberic.pooka.*;
import javax.activation.*;
import java.io.File;
import java.util.regex.*;

/**
 * This interface defines a ResourceManager.
 */
public abstract class ResourceManager {

  /**
   * Creates a VariableBundle to be used.
   */
  public abstract VariableBundle createVariableBundle(String fileName, VariableBundle defaults);

  /**
   * Creates a MailcapCommandMap to be used.
   */
  public abstract MailcapCommandMap createMailcap(String fileName) throws java.io.IOException;

  /**
   * Creates a PookaTrustManager.
   */
  public abstract PookaTrustManager createPookaTrustManager(javax.net.ssl.TrustManager[] pTrustManagers, String fileName);

  /**
   * Gets a resource for reading.  pFileName could be a URL or a file name
   * or some similar identifier that the ResourceManager can use.
   */
  public abstract java.io.InputStream getInputStream(String pFileName)
    throws java.io.IOException;

  /**
   * Returns the default Pooka Root.
   */
  public File getDefaultPookaRoot() {
    return new File(System.getProperty("user.home"));
  }

  /**
   * Returns the default localrc.
   */
  public String getDefaultLocalrc(File pPookaRoot) {
    if (pPookaRoot == null) {
      pPookaRoot = getDefaultPookaRoot();
    }
    return new String (pPookaRoot.getAbsolutePath() + System.getProperty("file.separator") + ".pookarc");
  }

  /**
   * Gets a resource for writing.  pFileName could be a URL or a file name
   * or some similar identifier that the ResourceManager can use.
   */
  public abstract java.io.OutputStream getOutputStream(String pFileName)
    throws java.io.IOException;

  /**
   * Creates an appropriate FolderInfo for the given StoreInfo.
   */
  public abstract FolderInfo createFolderInfo(StoreInfo pStore, String pName);

  static Pattern sUserHomePattern = Pattern.compile("\\$\\{user\\.home\\}");
  static Pattern sUserNamePattern = Pattern.compile("\\$\\{user\\.name\\}");
  /**
   * Translates the given file path.
   */
  public String translateName(String pFileName) {
    Matcher nameMatcher = sUserNamePattern.matcher(pFileName);
    String returnValue = nameMatcher.replaceAll(Matcher.quoteReplacement(System.getProperty("user.name")));

    Matcher homeMatcher = sUserHomePattern.matcher(returnValue);
    returnValue = homeMatcher.replaceAll(Matcher.quoteReplacement(System.getProperty("user.home")));

    return returnValue;
  }

  /**
   * Encodes the file path, if needed.  Default implementation just returns
   * the original String.
   */
  public String encodeFileName(String pFileName) {
    return pFileName;
  }
}
