package net.suberic.pooka;

import javax.activation.*;
import java.io.*;
import java.util.StringTokenizer;

/**
 * This class is a generic class which will allow an external program
 * to start up and access a file.
 */
public class ExternalLauncher implements CommandObject, Runnable {

  private String verb;
  private DataHandler dh;

  // for, err, showing progress in downloading the temporary file.
  private net.suberic.util.swing.ProgressDialog mDialog;

  private File mTmpFile = null;

  public ExternalLauncher() {
  };

  /**
   * This sets the CommandContext to the given command and DataHandler.
   * Note that for this implementation, the verb is expected to bet the
   * external command which is run, with %s representing the name of
   * the temporary file.
   *
   * As specified in javax.activation.CommandObject.
   */
  public void setCommandContext(java.lang.String newVerb,
                                DataHandler newDh)
    throws java.io.IOException {
    verb = newVerb;
    dh = newDh;
  }

  /**
   * Sets a ProgressDialog for watching the downloading of the temporary
   * file.
   */
  public void setProgressDialog(net.suberic.util.swing.ProgressDialog pDialog) {
    mDialog = pDialog;
  }

  /**
   * Gets the ProgressDialog for watching the downloading of the temporary
   * file.
   */
  public net.suberic.util.swing.ProgressDialog getProgressDialog() {
    return mDialog;
  }

  /**
   * This starts the run() method in a separate Thread.  It is implemented
   * this way so as to present the same interface as a CommandObject which
   * extends Window.
   */
  public void show() {
    Thread t = new Thread(this, "External Viewer");
    t.start();
  }

  /**
   * This is the main method for the ExternalLaucher.  It creates a
   * temporary file from the DataHandler and then uses the verb command,
   * along with the wrappers specified by ExternalLauncher.fileHandler
   * and ExternalLauncher.cmdWrapper, to access the file itself.
   */
  public void run() {
    try {
      String extension = ".tmp";
      String filename = dh.getName();
      if (filename == null)
        filename = "unavailable";
      int dotLoc = filename.lastIndexOf('.');
      if (dotLoc > 0) {
        extension = filename.substring(dotLoc);
      }
      mTmpFile = File.createTempFile("pooka_", extension);

      boolean cancelled = false;

      if (mDialog == null) {
        FileOutputStream fos = new FileOutputStream(mTmpFile);
        dh.writeTo(fos);
        fos.close();
      } else {
        mDialog.show();
        InputStream decodedIS = dh.getInputStream();
        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(mTmpFile));
        int b=0;
        byte[] buf = new byte[32768];

        b = decodedIS.read(buf);
        while (b != -1 && ! cancelled) {
          outStream.write(buf, 0, b);
          mDialog.setValue(mDialog.getValue() + b);
          if (mDialog.isCancelled())
            cancelled = true;

          b = decodedIS.read(buf);
        }

        outStream.close();
      }

      if (! cancelled) {
        if (mDialog != null)
          mDialog.dispose();

        String fileHandler = Pooka.getProperty("ExternalLauncher.fileHandler." + java.io.File.separator, null);
        String wrapper = Pooka.getProperty("ExternalLauncher.cmdWrapper." + java.io.File.separator, null);
        String fileName = mTmpFile.getAbsolutePath();

        String parsedVerb;
        String[] cmdArray;

        String origParsedCommand = substituteString(verb, "%s", fileName);

        if (fileHandler != null && wrapper != null) {

          parsedVerb = substituteString(fileHandler, "%v", verb);
          parsedVerb = substituteString(parsedVerb, "%s", fileName);

          StringTokenizer tok = new StringTokenizer(wrapper);
          cmdArray = new String[tok.countTokens()];
          for (int i = 0; tok.hasMoreTokens(); i++) {
            String currentString = tok.nextToken();
            if (currentString.equals("%v"))
              cmdArray[i] = parsedVerb;
            else
              cmdArray[i]=currentString;
          }
        } else {
          parsedVerb = substituteString(verb, "%s", fileName);

          cmdArray = parseCommandString(parsedVerb);

          mTmpFile.deleteOnExit();
        }

        try {
          if (Pooka.isDebug()) {
            System.out.println("running external command " + parsedVerb);
          }

          Process p = Runtime.getRuntime().exec(cmdArray);

          long startTime = System.currentTimeMillis();

          try {
            p.waitFor();
          } catch (InterruptedException ie) {
          }

          int exitValue = p.exitValue();

          if (Pooka.isDebug()) {
            System.out.println("finished external command " + parsedVerb);
          }

          if (exitValue != 0) {
            long externalTimeoutMillis = 5000;
            try {
              externalTimeoutMillis = Long.parseLong(Pooka.getProperty("ExternalLauncher.externalTimeoutMillis", "5000"));
            } catch (NumberFormatException nfe) {
              // just use the default.
            }
            if (System.currentTimeMillis() - startTime < externalTimeoutMillis) {
              if (Pooka.isDebug())
                System.out.println("external command " + parsedVerb + ":  exitValue is " + exitValue + " and timeout < externalTimeoutMillis; showing error.");

              showError(origParsedCommand, p);
            }
          }
        } catch (java.io.IOException processIoe) {
          Pooka.getUIFactory().showError("Error running process " + processIoe.getMessage());
          processIoe.printStackTrace();
        }
      } // if ! cancelled
    } catch (java.io.IOException ioe) {
      Pooka.getUIFactory().showError("Error opening temp file " + ioe.getMessage());
      ioe.printStackTrace();
    }
  }

  /**
   * Shows an error.
   */
  public void showError(String command, Process p) {

    // build up the error message.
    try {
      StringWriter errorWriter = new StringWriter();
      BufferedWriter bw = new BufferedWriter(errorWriter);
      bw.write(Pooka.getProperty("ExternalLauncher.error.failedToRun", "Failed executing command:"));
      bw.newLine();
      bw.write(command);
      bw.newLine();
      bw.newLine();
      bw.write(Pooka.getProperty("ExternalLauncher.error.output", "Output:"));
      bw.newLine();

      try {
        InputStream errorStream = p.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(errorStream));
        for (String nextLine = br.readLine(); nextLine != null; nextLine = br.readLine()) {
          bw.write(nextLine);
          bw.newLine();
        }
      } catch (IOException ioe) {
        bw.write("Error not available");
        bw.newLine();
      }
      bw.flush();
      bw.close();

      String errorMessage = errorWriter.toString();
      Pooka.getUIFactory().showError(errorMessage);
    } catch (IOException ioe) {
      // shouldn't happen, but...
      Pooka.getUIFactory().showError(Pooka.getProperty("ExternalLauncher.error.failedToRun", "Failed executing command:"));
    }
  }

  /**
   * This method subsitutes all occurances of key with value in String
   * original.
   */
  public static String substituteString(String original, String key, String value) {
    // you know, i'm already doing this for the replyIntro; maybe i
    // should generalize these both a bit...

    StringBuffer modifiedString = new StringBuffer(original);
    int current = original.lastIndexOf(key, original.length());
    while (current != -1) {
      modifiedString.replace(current, current + key.length(), value);
      current = original.substring(0, current).lastIndexOf(key, current);
    }

    return modifiedString.toString();
  }

  /**
   * This parses a command string into a command array.
   */
  public String[] parseCommandString(String cmdString) {
    StringTokenizer tok = new StringTokenizer(cmdString);
    String[] cmdArray = new String[tok.countTokens()];
    for (int i = 0; tok.hasMoreTokens(); i++) {
      String currentString = tok.nextToken();
      cmdArray[i]=currentString;
    }

    return cmdArray;
  }

  public void cancelSave() {
    try {
      mTmpFile.delete();
    } catch (Exception e) {}
    if (mDialog != null)
      mDialog.dispose();
  }

}
