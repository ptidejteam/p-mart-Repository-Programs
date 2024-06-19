package src.FLYWEIGHT.Approach2;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Vector;


public class FileUtil {

  DataOutputStream dos;

  public String OutputToFile(String FileName, String DataLine,
      boolean AppendMode, boolean NewLine) {
    if ((FileExists(FileName)) && (AppendMode == false)) {

      try {
        String tempFile =
          FileName + new Date().getTime();
        File OutFile = new File(tempFile);
        dos = new DataOutputStream(
                new FileOutputStream(OutFile));
        dos.writeBytes(DataLine);
        dos.close();

        DeleteFile(FileName);

        OutFile.renameTo(new File(FileName));
      } catch (FileNotFoundException ex) {
        //
        System.out.println(
          " File Utility OutputToFile ERROR=" +
          ex.getMessage());
        System.exit(1);
      }
      catch (IOException ex) {
        //
        System.out.println(
          " File Utility OutputToFile ERROR=" +
          ex.getMessage());
        System.exit(1);
      }


    } else {
      if (NewLine) {
        DataLine = "\n" + DataLine;
      }

      try {
        File OutFile = new File(FileName);
        if (AppendMode) {
          dos = new DataOutputStream(
                  new FileOutputStream(FileName, true));
        } else {
          dos = new DataOutputStream(
                  new FileOutputStream(OutFile));
        }

        dos.writeBytes(DataLine);
        dos.close();
      } catch (FileNotFoundException ex) {
        //
        System.out.println(
          " File Utility OutputToFile ERROR=" +
          ex.getMessage());
        System.exit(1);
      }
      catch (IOException ex) {
        //
        System.out.println(
          " File Utility OutputToFile ERROR=" +
          ex.getMessage());
        System.exit(1);
      }
    } // If-Else
    return ("Success");

  }

  public String ReadFromFile(String FileName) {
    String DataLine = "";
    try {
      File InFile = new File(FileName);
      BufferedReader dos = new BufferedReader(
            new InputStreamReader(
              new FileInputStream(InFile)));

      DataLine = dos.readLine();
      dos.close();
    } catch (FileNotFoundException ex) {
      //
      System.out.println(
        " File Utility ReadFromFile ERROR=" +
        ex.getMessage());
      System.exit(1);
    }
    catch (IOException ex) {
      //
      System.out.println(
        " File Utility ReadFromFile ERROR=" +
        ex.getMessage());
      System.exit(1);
    }
    return (DataLine);

  }

  public boolean FileExists(String FileName) {
    File InFile = new File(FileName);
    return InFile.exists();
  }

  public boolean DeleteFile(String FileName) {
    File InFile = new File(FileName);
    return InFile.delete();
  }

  public Vector FileToVector(String FileName) {
    Vector v = new Vector();
    String inputLine;
    try {
      File InFile = new File(FileName);
      BufferedReader din = new BufferedReader(
            new InputStreamReader(
              new FileInputStream(InFile)));

      while ((inputLine = din.readLine()) != null) {
        v.addElement(inputLine);
      }
      din.close();
    } // Try
    catch (FileNotFoundException ex) {
      //
      System.out.println(
        " File Utility FiletoVector ERROR=" +
        ex.getMessage());
    }
    catch (IOException ex) {
      //
      System.out.println(
        " File Utility FiletoVector ERROR=" +
        ex.getMessage());
    }
    return (v);
  }

  public void VectorToFile(Vector v, String FileName) {
    for (int i = 0; i < v.size(); i++) {
      OutputToFile(FileName, (String) v.elementAt(i), true,
                   true);
    }
  }

}// end of class

