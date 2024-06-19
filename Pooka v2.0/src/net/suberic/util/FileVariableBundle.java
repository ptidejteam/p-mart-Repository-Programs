package net.suberic.util;
import java.util.*;
import java.io.*;

/**
 * VariableBundle is a combination of a Properties object, a ResourceBundle
 * object, and (optionally) a second Properties object to act as the 'parent'
 * properties.  This allows both for a single point of reference for
 * variables, as well as the ability to do hierarchical lookups with the
 * parent (see getProperty() for an example).
 *
 * The order of lookup is as follows:  Local properties are checked first,
 * then parent properties, and then finally (if the value is not found in
 * any properties) the ResourceBundle is checked.
 */

public class FileVariableBundle extends VariableBundle {
  private File mSaveFile;

  public FileVariableBundle(InputStream propertiesFile, String resourceFile, VariableBundle newParentProperties) {
    configure(propertiesFile, resourceFile, newParentProperties);
  }

  public FileVariableBundle(File propertiesFile, VariableBundle newParentProperties) throws java.io.FileNotFoundException {
    FileInputStream fis = new FileInputStream(propertiesFile);
    configure(fis, null, newParentProperties);
    try {
      fis.close();
    } catch (java.io.IOException ioe) {
    }
    mSaveFile = propertiesFile;

  }

  public FileVariableBundle(InputStream propertiesFile, String resourceFile) {
    this(propertiesFile, resourceFile, null);
  }

  public FileVariableBundle(InputStream propertiesFile, VariableBundle newParentProperties) {
    this(propertiesFile, null, newParentProperties);
  }

  public FileVariableBundle(Properties editableProperties, VariableBundle newParentProperties) {
    super(editableProperties, newParentProperties);
  }

  /**
   * Configures the VariableBundle.
   */
  protected void configure(InputStream propertiesFile, String resourceFile, VariableBundle newParentProperties) {

    writableProperties = new Properties();

    if (resourceFile != null)
      try {
        resources = ResourceBundle.getBundle(resourceFile, Locale.getDefault());
      } catch (MissingResourceException mre) {
        System.err.println("Error loading resource " + mre.getClassName() + mre.getKey() + ":  trying default locale.");
        try {
          resources = ResourceBundle.getBundle(resourceFile, Locale.US);
        } catch (MissingResourceException mreTwo){
          System.err.println("Unable to load default (US) resource bundle; exiting.");
          System.exit(1);
        }
      }
    else
      resources=null;

    properties = new Properties();

    if (propertiesFile != null)
      try {
        properties.load(propertiesFile);
      } catch (java.io.IOException ioe) {
        System.err.println(ioe.getMessage() + ":  " + propertiesFile);
      }

    List includeStreams = getPropertyAsList("VariableBundle.include", "");
    if (includeStreams != null && includeStreams.size() > 0) {
      for (int i = 0; i < includeStreams.size(); i++) {
        String current = (String) includeStreams.get(i);
        try {
          if (current != null && ! current.equals("")) {
            java.net.URL url = this.getClass().getResource(current);

            java.io.InputStream is = url.openStream();

            properties.load(is);
          }
        } catch (java.io.IOException ioe) {
          System.err.println("error including file " + current + ":  " + ioe.getMessage());
          ioe.printStackTrace();
        }
      }
    }

    parentProperties = newParentProperties;


  }

  /**
   * Saves the current properties in the VariableBundle to a file.  Note
   * that this only saves the writableProperties of this particular
   * VariableBundle--underlying defaults are not written.
   */
  public void saveProperties() {
    if (mSaveFile != null) {
      saveProperties(mSaveFile);
    }
  }

  /**
   * Saves the current properties in the VariableBundle to a file.  Note
   * that this only saves the writableProperties of this particular
   * VariableBundle--underlying defaults are not written.
   */
  public void saveProperties(File pSaveFile) {
    if (pSaveFile == null)
      return;

    synchronized(this) {
      if (writableProperties.size() > 0) {
        File outputFile;
        String currentLine, key;
        int equalsLoc;

        try {
          if (! pSaveFile.exists())
            pSaveFile.createNewFile();

          outputFile  = pSaveFile.createTempFile(pSaveFile.getName(), ".tmp", pSaveFile.getParentFile());

          BufferedReader readSaveFile = new BufferedReader(new FileReader(pSaveFile));
          BufferedWriter writeSaveFile = new BufferedWriter(new FileWriter(outputFile));
          currentLine = readSaveFile.readLine();
          while (currentLine != null) {
            equalsLoc = currentLine.indexOf('=');
            if (equalsLoc != -1) {
              String rawKey = currentLine.substring(0, equalsLoc);
              key = unEscapeString(rawKey);

              if (!propertyIsRemoved(key)) {
                if (writableProperties.getProperty(key, "").equals("")) {

                  writeSaveFile.write(currentLine);
                  writeSaveFile.newLine();

                } else {
                  writeSaveFile.write(rawKey + "=" + escapeWhiteSpace(writableProperties.getProperty(key, "")));
                  writeSaveFile.newLine();
                  properties.setProperty(key, writableProperties.getProperty(key, ""));
                  writableProperties.remove(key);
                }
              } else {
                properties.remove(key);
              }
            } else {
              writeSaveFile.write(currentLine);
              writeSaveFile.newLine();
            }
            currentLine = readSaveFile.readLine();
          }

          // write out the rest of the writableProperties

          Set<String> propsLeft = writableProperties.stringPropertyNames();
          List<String> propsLeftList = new ArrayList<String>(propsLeft);
          Collections.sort(propsLeftList);
          for (String nextKey: propsLeftList) {
            String nextKeyEscaped = escapeWhiteSpace(nextKey);
            String nextValueEscaped = escapeWhiteSpace(writableProperties.getProperty(nextKey, ""));
            writeSaveFile.write(nextKeyEscaped + "=" + nextValueEscaped);
            writeSaveFile.newLine();

            properties.setProperty(nextKey, writableProperties.getProperty(nextKey, ""));
            writableProperties.remove(nextKey);
          }

          clearRemoveList();

          readSaveFile.close();
          writeSaveFile.flush();
          writeSaveFile.close();

          // if you don't delete the .old file first, then the
          // rename fails under Windows.
          String oldSaveName = pSaveFile.getAbsolutePath() + ".old";
          File oldSave = new File (oldSaveName);
          if (oldSave.exists())
            oldSave.delete();

          String fileName = new String(pSaveFile.getAbsolutePath());
          pSaveFile.renameTo(oldSave);
          outputFile.renameTo(new File(fileName));

        } catch (Exception e) {
          System.out.println(getProperty("VariableBundle.saveError", "Error saving properties file: " + pSaveFile.getName() + ": " + e.getMessage()));
          e.printStackTrace(System.err);
        }
      }
    }
  }

  /*
   * Converts encoded &#92;uxxxx to unicode chars
   * and changes special saved chars to their original forms
   *
   * ripped directly from java.util.Properties; hope they don't mind.
   */
  private String loadConvert (String theString) {
    char aChar;
    int len = theString.length();
    StringBuffer outBuffer = new StringBuffer(len);

    for(int x=0; x<len; ) {
      aChar = theString.charAt(x++);
      if (aChar == '\\') {
        aChar = theString.charAt(x++);
        if(aChar == 'u') {
          // Read the xxxx
          int value=0;
          for (int i=0; i<4; i++) {
            aChar = theString.charAt(x++);
            switch (aChar) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
              value = (value << 4) + aChar - '0';
              break;
            case 'a': case 'b': case 'c':
            case 'd': case 'e': case 'f':
              value = (value << 4) + 10 + aChar - 'a';
              break;
            case 'A': case 'B': case 'C':
            case 'D': case 'E': case 'F':
              value = (value << 4) + 10 + aChar - 'A';
              break;
            default:
              throw new IllegalArgumentException(
                                                 "Malformed \\uxxxx encoding.");
            }
          }
          outBuffer.append((char)value);
        } else {
          if (aChar == 't') aChar = '\t';
          else if (aChar == 'r') aChar = '\r';
          else if (aChar == 'n') aChar = '\n';
          else if (aChar == 'f') aChar = '\f';
          outBuffer.append(aChar);
        }
      } else
        outBuffer.append(aChar);
    }
    return outBuffer.toString();
  }

  /*
   * Converts unicodes to encoded &#92;uxxxx
   * and writes out any of the characters in specialSaveChars
   * with a preceding slash
   *
   * ripped directly from java.util.Properties; hope they don't mind.
   */
  private String saveConvert(String theString, boolean escapeSpace) {
    int len = theString.length();
    StringBuffer outBuffer = new StringBuffer(len*2);

    for(int x=0; x<len; x++) {
      char aChar = theString.charAt(x);
      switch(aChar) {
      case ' ':
        if (x == 0 || escapeSpace)
          outBuffer.append('\\');

        outBuffer.append(' ');
        break;
      case '\\':outBuffer.append('\\'); outBuffer.append('\\');
        break;
      case '\t':outBuffer.append('\\'); outBuffer.append('t');
        break;
      case '\n':outBuffer.append('\\'); outBuffer.append('n');
        break;
      case '\r':outBuffer.append('\\'); outBuffer.append('r');
        break;
      case '\f':outBuffer.append('\\'); outBuffer.append('f');
        break;
      default:
        if ((aChar < 0x0020) || (aChar > 0x007e)) {
          outBuffer.append('\\');
          outBuffer.append('u');
          outBuffer.append(toHex((aChar >> 12) & 0xF));
          outBuffer.append(toHex((aChar >>  8) & 0xF));
          outBuffer.append(toHex((aChar >>  4) & 0xF));
          outBuffer.append(toHex( aChar        & 0xF));
        } else {
          if (specialSaveChars.indexOf(aChar) != -1)
            outBuffer.append('\\');
          outBuffer.append(aChar);
        }
      }
    }
    return outBuffer.toString();
  }

  /**
   * Escapes whitespace in a string by putting a '\' in front of each
   * whitespace character.
   */
  public String escapeWhiteSpace(String sourceString) {
    /*
      char[] origString = sourceString.toCharArray();
      StringBuffer returnString = new StringBuffer();
      for (int i = 0; i < origString.length; i++) {
      char currentChar = origString[i];
      if (Character.isWhitespace(currentChar) || '\\' == currentChar)
      returnString.append('\\');

      returnString.append(currentChar);
      }

      return returnString.toString();
    */
    return saveConvert(sourceString, true);
  }

  /**
   * resolves a whitespace-escaped string.
   */
  public String unEscapeString(String sourceString) {
    return loadConvert(sourceString);
  }

  /**
   * Convert a nibble to a hex character
   * @paramnibblethe nibble to convert.
   */
  private static char toHex(int nibble) {
    return hexDigit[(nibble & 0xF)];
  }

  /** A table of hex digits */
  private static final char[] hexDigit = {
    '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
  };

  //private static final String keyValueSeparators = "=: \t\r\n\f";

  //private static final String strictKeyValueSeparators = "=:";

  private static final String specialSaveChars = "=: \t\r\n\f#!";

  private static final String whiteSpaceChars = " \t\r\n\f";

  /**
   * Returns the current saveFile.
   */
  public File getSaveFile() {
    return mSaveFile;
  }

  /**
   * Sets the save file.
   */
  public void setSaveFile(File newFile) {
    mSaveFile = newFile;
  }
}


