package net.suberic.pooka.resource;

import net.suberic.util.*;
import net.suberic.pooka.ssl.*;
import net.suberic.pooka.*;
import javax.activation.*;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A ResourceManager which uses files.
 */
public class FileResourceManager extends ResourceManager {

  //static Pattern sVariablePattern = Pattern.compile("\\$\\{[^\\\\$]}");
  static Pattern sRootDirPattern = Pattern.compile("\\$\\{pooka\\.root\\}");

  /**
   * Creates a VariableBundle to be used.
   */
  public VariableBundle createVariableBundle(String fileName, VariableBundle defaults) {
    try {
      java.io.File f = new java.io.File(fileName);
      if (! f.exists())
        f.createNewFile();
      return new net.suberic.util.FileVariableBundle(f, defaults);
    } catch (java.io.IOException ioe) {
      //new net.suberic.util.VariableBundle(url.openStream(), "net.suberic.pooka.Pooka");
      return defaults;
    }

  }

  /**
   * Creates a MailcapCommandMap to be used.
   */
  public MailcapCommandMap createMailcap(String fileName) throws java.io.IOException {
    return new FullMailcapCommandMap(fileName);
  }

  /**
   * Creates a PookaTrustManager.
   */
  public PookaTrustManager createPookaTrustManager(javax.net.ssl.TrustManager[] pTrustManagers, String fileName) {
    return new PookaTrustManager(pTrustManagers, fileName);
  }

  public java.io.InputStream getInputStream(String pFileName)
    throws java.io.IOException {
    String translatedFile = translateName(pFileName);
    try {
      URL url = new URL(translatedFile);
      return url.openStream();
    } catch (MalformedURLException mue) {
      return new FileInputStream(new File(translatedFile));
    }
  }


  public java.io.OutputStream getOutputStream(String pFileName)
    throws java.io.IOException {
    String translatedFile = translateName(pFileName);
    return new FileOutputStream(new File(translatedFile));
  }

  /**
   * Creates an appropriate FolderInfo for the given StoreInfo.
   */
  public FolderInfo createFolderInfo(StoreInfo pStore, String pName) {
    String storeProperty = pStore.getStoreProperty();
    if (pStore.isPopStore() && pName.equalsIgnoreCase("INBOX")) {
      return new PopInboxFolderInfo(pStore, pName);
    } else if (Pooka.getProperty(storeProperty + ".protocol", "mbox").equalsIgnoreCase("imap")) {
      // check to see if we have a cacheMode value set.
      String cacheMode = Pooka.getProperty(storeProperty + ".cacheMode", "");
      if (cacheMode.equals("")) {
        if (Pooka.getProperty(storeProperty + ".cachingEnabled", Pooka.getProperty(storeProperty + "." + pName + ".cachingEnabled", "false")).equalsIgnoreCase("true") || Pooka.getProperty(storeProperty + ".cacheHeadersOnly", Pooka.getProperty(storeProperty + "." + pName + ".cacheHeadersOnly", "false")).equalsIgnoreCase("true")) {
          return new net.suberic.pooka.cache.CachingFolderInfo(pStore, pName);
        }
      } else {
        if (cacheMode.equals("enabled") || cacheMode.equals("headersOnly")) {
          return new net.suberic.pooka.cache.CachingFolderInfo(pStore, pName);
        }
      }
      // otherwise....
      return  new UIDFolderInfo(pStore, pName);
    } else {
      return new FolderInfo(pStore, pName);
    }
  }

  /**
   * Translates the given file path.
   */
  public String translateName(String pFileName) {
    String firstTranslate = super.translateName(pFileName);
    Matcher matcher = sRootDirPattern.matcher(firstTranslate);
    String returnValue = matcher.replaceAll(Matcher.quoteReplacement(Pooka.getPookaManager().getPookaRoot().getAbsolutePath()));
    return returnValue;
  }

  /**
   * Encodes the file path to use the pooka.root setting if the file
   * path is below the pooka.root.
   */
  public String encodeFileName(String pFileName) {
    File f = new File(pFileName);
    String filePath = null;
    String rootPath = null;
    try {
      filePath = f.getCanonicalPath();
      rootPath = Pooka.getPookaManager().getPookaRoot().getCanonicalPath();
    } catch (IOException ioe) {
      // just use the absolute paths.
      filePath = f.getAbsolutePath();
      rootPath = Pooka.getPookaManager().getPookaRoot().getAbsolutePath();
    }

    if (filePath.startsWith(rootPath)) {
      return "${pooka.root}" + filePath.substring(rootPath.length());
    } else {
      return pFileName;
    }
  }

}


