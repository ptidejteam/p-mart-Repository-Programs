/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.rolling.helper;

import org.apache.log4j.rolling.RolloverFailure;
import org.apache.log4j.spi.ComponentBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class to help solving problems encontered while renameing files.
 * @author Ceki Gulcu  
 */
public class Util extends ComponentBase {

  
  /**
   * A robust file renaming method which in case of failure falls back to
   * renmaing by copying. In case, the file to be renamed is open by another
   * process, renaming by copying will succed whereas, regular renaming will
   * fail. However, renaming by copying is much slower.
   * 
   * @param from
   * @param to
   * @throws RolloverFailure
   */
  public void rename(String from, String to) throws RolloverFailure {
    File fromFile = new File(from);
    boolean success = false;

    if (fromFile.exists()) {
      File toFile = new File(to);
      getLogger().debug("Renaming file [{} to [{}]", fromFile, toFile);

      boolean result = fromFile.renameTo(toFile);

      if (!result) {
        getLogger().warn("Failed to rename file [{}] to [{}].", fromFile, toFile);
        getLogger().warn("Attemting to rename by copying.");
        renameByCopying(from, to);
      }
    } else {
      throw new RolloverFailure("File [" + from + "] does not exist.");
    }
  }

  public void renameByCopying(String from, String to)
      throws RolloverFailure {
    try {
      FileInputStream fis = new FileInputStream(from);
      FileOutputStream fos = new FileOutputStream(to);
      byte[] inbuf = new byte[8102];
      int n;

      while ((n = fis.read(inbuf)) != -1) {
        fos.write(inbuf, 0, n);
      }

      fis.close();
      fos.close();

      File fromFile = new File(from);

      if (!fromFile.delete()) {
        getLogger().warn("Could not delete [].", from);
      }
    } catch (IOException ioe) {
      getLogger().error("Failed to rename file by copying", ioe);
      throw new RolloverFailure("Failed to rename file by copying");
    }
  }
}