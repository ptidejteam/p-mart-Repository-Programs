/*
 * Created on 01.12.2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.gudy.azureus2.ui.swt;

import java.io.*;
import org.eclipse.swt.dnd.*;

/**
 * URL Transfer type for Drag and Drop of URLs
 * Windows IDs are already functional.
 * 
 * Please use Win32TransferTypes to determine the IDs for other OSes!
 * 
 * @see org.gudy.azureus2.ui.swt.test.Win32TransferTypes
 * @author Rene Leonhardt
 */

public class URLTransfer extends ByteArrayTransfer {

  private static URLTransfer _instance = new URLTransfer();

  // these types work on Windows XP: IE6 link=49367/49362, IE6 bookmark=13,
	// Mozilla link (text)=13, Mozilla bookmark=13
  // Opera 7 LINK DRAG & DROP IMPOSSIBLE (just inside Opera)
  private static final String[] supportedTypes = new String[] { "UniformResourceLocator", "UniformResourceLocator", "CF_UNICODETEXT", "CF_TEXT" };
  private static final int[] supportedTypeIds = new int[] { 49367, 49362, 13, 1 }; // 15="CF_HDROP" (File), 49368="UniformResourceLocator" (File+IE bookmark), 49458="UniformResourceLocatorW" (IE bookmark)

  public static URLTransfer getInstance() {
    return _instance;
  }
  public void javaToNative(Object object, TransferData transferData) {
    if (object == null || !(object instanceof URLType[]))
      return;

    if (isSupportedType(transferData)) {
      URLType[] myTypes = (URLType[]) object;
      try {
        // write data to a byte array and then ask super to convert to pMedium
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream writeOut = new DataOutputStream(out);
        for (int i = 0, length = myTypes.length; i < length; i++) {
          writeOut.writeBytes(myTypes[i].linkURL);
          writeOut.writeBytes("\n");
          writeOut.writeBytes(myTypes[i].linkText);
        }
        byte[] buffer = out.toByteArray();
        writeOut.close();

        super.javaToNative(buffer, transferData);

      } catch (IOException e) {}
    }
  }
  public Object nativeToJava(TransferData transferData) {

    if (isSupportedType(transferData)) {

      byte[] buffer = (byte[]) super.nativeToJava(transferData);
      if (buffer == null)
        return null;

      URLType myData = null;
      try {
        URLType datum = new URLType();
        byte[] text = new byte[buffer.length];
        int j = 0;
        for (int i = 0; i < buffer.length; i++) {
          if (buffer[i] != 0)
            text[j++] = buffer[i];
        }
        String data = new String(text, 0, j);
        int end = data.indexOf("\n");
        if (end >= 0) {
          datum.linkURL = data.substring(0, end++);
          datum.linkText = end == data.length() ? "" : data.substring(end);
        } else {
          datum.linkURL = data;
          datum.linkText = "";
        }
        myData = datum;
      } catch (Exception ex) {
        return null;
      }
      return myData;
    }

    return null;
  }
  protected String[] getTypeNames() {
    return supportedTypes;
  }
  protected int[] getTypeIds() {
    return supportedTypeIds;
  }
  /**
	 * @param transferData
	 * @see org.eclipse.swt.dnd.Transfer#isSupportedType(org.eclipse.swt.dnd.TransferData)
	 * @return
	 */
  public boolean isSupportedType(TransferData transferData) {
    if (transferData != null) {
      for (int i = 0; i < supportedTypeIds.length; i++) {
        if (transferData.type == supportedTypeIds[i])
          return true;
      }
    }
    return super.isSupportedType(transferData);
  }

  public class URLType {
    public String linkURL;
    public String linkText;
    public String toString() {
      return linkURL + "\n" + linkText;
    }
  }

}
