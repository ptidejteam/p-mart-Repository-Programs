package net.suberic.pooka.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.activation.*;

import net.suberic.pooka.*;
import net.suberic.util.swing.*;
import net.suberic.util.thread.*;

/**
 * Handles opening, saving, etc. attachments.
 */
public class AttachmentHandler {

  // i'm hardcoding these, but i doubt that will be too much of a problem.

  static int minTextWidth = 600;
  static int maxTextWidth = 800;
  static int minTextHeight = 600;
  static int maxTextHeight = 800;

  MessageProxy mProxy;

  /**
   * Creates a new AttachmentHandler instance.
   */
  public AttachmentHandler(MessageProxy pProxy) {
    mProxy = pProxy;
  }

  /**
   * Returns the associated MessageProxy.
   */
  public MessageProxy getMessageProxy() {
    return mProxy;
  }

  /**
   * Returns the associated MessageUI.
   */
  public MessageUI getMessageUI() {
    return mProxy.getMessageUI();
  }

  /**
   * Shows an error message, either on the MessageUI if there is one, or
   * if not, on the main Pooka frame.
   */
  public void showError(String message, Exception ioe) {
    MessageUI mui = getMessageUI();
    if (mui != null) {
      mui.showError(message,ioe);
    } else {
      Pooka.getUIFactory().showError(message,ioe);
    }
  }

  /**
   * Shows an error message, either on the MessageUI if there is one, or
   * if not, on the main Pooka frame.
   */
  public void showError(String message, String title, Exception ioe) {
    MessageUI mui = getMessageUI();
    if (mui != null) {
      mui.showError(message, title, ioe);
    } else {
      Pooka.getUIFactory().showError(message, title, ioe);
    }
  }

  /**
   * This opens up the selected Attachment using the default handler
   * for the Attachment's Mime type.
   */
  public void openAttachment(Attachment pAttachment) {
    // called on the folder thread.

    if (pAttachment != null) {
      DataHandler dh = null;
      dh = pAttachment.getDataHandler();

      if (dh != null) {
        dh.setCommandMap(Pooka.getMailcap());

        if (Pooka.isDebug()) {
          CommandInfo[] cis = dh.getAllCommands();
          if (cis != null && cis.length > 0) {
            for (int i = 0; i < cis.length; i++) {
              System.out.println(cis[i].getCommandName() + ", " + cis[i].getCommandClass());
            }
          } else {
            System.out.println("No commands for mimetype.");
          }
        } // end debug

        CommandInfo[] cmds = dh.getPreferredCommands();
        if (cmds != null && cmds[0] != null) {
          Object beanViewer = dh.getBean(cmds[0]);
          if (beanViewer instanceof Frame) {
            Frame frameViewer = (Frame)beanViewer;
            try {
              frameViewer.setTitle(pAttachment.getName());
              frameViewer.setSize(frameViewer.getPreferredSize());
            } catch (Exception e) {
            }
            frameViewer.setVisible(true);
          } else if (beanViewer instanceof Component) {
            String title = pAttachment.getName();
            openAttachmentWindow((Component)beanViewer, title, false);
          } else if (beanViewer instanceof ExternalLauncher) {
            ((ExternalLauncher)beanViewer).show();
          } else if (beanViewer instanceof com.sun.mail.handlers.text_plain || beanViewer instanceof com.sun.mail.handlers.text_html) {
            // sigh
            JTextPane jtp = new JTextPane();
            try {
              String content = (String) pAttachment.getContent();
              if (pAttachment.isHtml()) {
                jtp.setContentType("text/html");
              }
              jtp.setText(content);
              jtp.setEditable(false);
              openAttachmentWindow(new JScrollPane(jtp), pAttachment.getName(), true);
            } catch (IOException ioe) {
              showError("Error showing attachment:  ", ioe);
            }
          } else if (cmds[0].getCommandClass().equals("net.suberic.pooka.ExternalLauncher")) {
            try {
              ExternalLauncher el = new ExternalLauncher();

              // create a progress dialog for the external launcher
              int attachmentSize = pAttachment.getSize();
              if (pAttachment.getEncoding() != null && pAttachment.getEncoding().equalsIgnoreCase("base64"))
                attachmentSize = (int) (attachmentSize * .73);

              ProgressDialog dlg;
              if (getMessageUI() != null) {
                dlg = getMessageUI().createProgressDialog(0, attachmentSize, 0, "Fetching attachment...","Fetching attachment");
              } else {
                dlg = Pooka.getUIFactory().createProgressDialog(0, attachmentSize, 0, "Fetching attachment","Fetching attachment");
              }

              final ExternalLauncher fLauncher = el;
              dlg.addCancelListener(new ProgressDialogListener() {
                  public void dialogCancelled() {
                    fLauncher.cancelSave();
                  }
                });

              el.setProgressDialog(dlg);

              el.setCommandContext(cmds[0].getCommandName(), null);

              el.show();
            } catch (IOException ioe) {
              //
            }
          } else {
            openWith(pAttachment);
          }
        } else if (isWindows()) {
          try {
            String mimeType = pAttachment.getMimeType().toString();
            if (mimeType.indexOf(';') != -1)
              mimeType = mimeType.substring(0, mimeType.indexOf(';'));

            String cmd = "rundll32 url.dll,FileProtocolHandler %s";

            ExternalLauncher el = new ExternalLauncher();

            el.setCommandContext(cmd, dh);

            // create a progress dialog for the external launcher
            int attachmentSize = pAttachment.getSize();
            if (pAttachment.getEncoding() != null && pAttachment.getEncoding().equalsIgnoreCase("base64"))
              attachmentSize = (int) (attachmentSize * .73);

            ProgressDialog dlg;
            if (getMessageUI() != null) {
              dlg = getMessageUI().createProgressDialog(0, attachmentSize, 0, "Fetching attachment","Fetching attachment");
            } else {
              dlg = Pooka.getUIFactory().createProgressDialog(0, attachmentSize, 0, "Fetching attachment","Fetching attachment");
            }

            final ExternalLauncher fLauncher = el;
            dlg.addCancelListener(new ProgressDialogListener() {
                public void dialogCancelled() {
                  fLauncher.cancelSave();
                }
              });

            el.setProgressDialog(dlg);

            if (Pooka.isDebug())
              System.out.println("opening external launcher with ");
            el.show();
          } catch (Exception elException) {
            getMessageUI().showError("Error opening attachment", elException);
          }

        } else {
          openWith(pAttachment);
        }
      }
    }
  }

  /**
   * Returns whether or not we're running on a Windows platform.
   */
  public boolean isWindows() {
    return (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1);
  }

  /**
   * Opens either a JFrame or a JInternalFrame, whichever is appropriate,
   * with the given Component as a content pane and the given title.
   */
  private void openAttachmentWindow(Component pContent, String pTitle, boolean pResize) {
    // threading:  this can be called on any thread, since it calls
    // SwingUtilities.invokeLater().

    final Component content = pContent;
    final String title = pTitle;
    final boolean resize = pResize;

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          MessageUI mui = getMessageUI();
          if (Pooka.isDebug())
            System.out.println("opening attachment window.");

          if ((mui != null && mui instanceof JInternalFrame) || (mui == null && Pooka.getUIFactory() instanceof PookaDesktopPaneUIFactory) ) {
            JDesktopPane desktop = ((PookaDesktopPaneUIFactory) Pooka.getUIFactory()).getMessagePanel();
            JInternalFrame jif = new JInternalFrame(title, true, true, true, true);
            jif.getContentPane().add(content);
            jif.pack();
            if (resize) {
              // let's be reasonable here....
              Dimension frameSize = jif.getSize();
              if (frameSize.width < minTextWidth) {
                frameSize.width = minTextWidth;
              } else if (frameSize.width > maxTextWidth) {
                frameSize.width = maxTextWidth;
              }

              if (frameSize.height < minTextHeight) {
                frameSize.height = minTextHeight;
              } else if (frameSize.height > maxTextHeight) {
                frameSize.height = maxTextHeight;
              }

              jif.setSize(frameSize);
            }

            desktop.add(jif);
            if (desktop instanceof MessagePanel) {
              jif.setLocation(((MessagePanel) desktop).getNewWindowLocation(jif, false));
            }
            jif.setVisible(true);
            try {
              jif.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
            }
          } else {
            JFrame frame = new JFrame(title);
            frame.getContentPane().add(content);
            frame.pack();

            if (resize) {
              // let's be reasonable here....
              Dimension frameSize = frame.getSize();
              if (frameSize.width < minTextWidth) {
                frameSize.width = minTextWidth;
              } else if (frameSize.width > maxTextWidth) {
                frameSize.width = maxTextWidth;
              }

              if (frameSize.height < minTextHeight) {
                frameSize.height = minTextHeight;
              } else if (frameSize.height > maxTextHeight) {
                frameSize.height = maxTextHeight;
              }

              frame.setSize(frameSize);
            }
            frame.setVisible(true);
          }
        }
      });
  }

  /**
   * This opens the Attachment with the program of the user's choice.
   */
  public void openWith(Attachment pAttachment) {
    if (Pooka.isDebug())
      System.out.println("calling AttachmentHandler.openWith()");

    try {
      String mimeType = pAttachment.getMimeType().toString();
      if (mimeType.indexOf(';') != -1)
        mimeType = mimeType.substring(0, mimeType.indexOf(';'));

      final String mType = mimeType;

      final Attachment fAttachment = pAttachment;

      // have to get the ActionThread for later.
      ActionThread actionThread = null;
      Thread currentThread = Thread.currentThread();
      if (currentThread instanceof ActionThread) {
        actionThread = (ActionThread) currentThread;
      }
      final ActionThread fActionThread = actionThread;

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            String inputMessage = Pooka.getProperty("AttchmentPane.openWith.message", "Enter the command with which \r\nto open the attchment.");
            String inputTitle = Pooka.getProperty("AttachmentPane.openWith.title", "Open Attachment With");
            String makeDefaultLabel = Pooka.getProperty("AttachmentPane.openWith.makeDefaultMessage", "Make default command?");

            JLabel toggleMsgLabel = new JLabel(makeDefaultLabel);
            toggleMsgLabel.setForeground(Color.getColor("Black"));
            JRadioButton toggleButton = new JRadioButton();
            JPanel togglePanel = new JPanel();
            togglePanel.add(toggleMsgLabel);
            togglePanel.add(toggleButton);

            Object[] messageArray = new Object[2];
            messageArray[0] = inputMessage;
            messageArray[1] = togglePanel;
            String cmd = null;
            if (getMessageUI() != null)
              cmd = getMessageUI().showInputDialog(messageArray, inputTitle);
            else
              cmd = Pooka.getUIFactory().showInputDialog(messageArray, inputTitle);

            if (cmd != null) {
              if (cmd.indexOf("%s") == -1)
                cmd = cmd.concat(" %s");

              if (toggleButton.isSelected()) {
                String newMailcap = new String(mType.toLowerCase() + ";" + cmd);
                ((FullMailcapCommandMap)Pooka.getMailcap()).addMailcap(newMailcap);
              }


              final DataHandler dh = fAttachment.getDataHandler();
              final String fCmd = cmd;

              if (dh != null) {
                AbstractAction action = new AbstractAction() {
                    public void actionPerformed(java.awt.event.ActionEvent ae) {
                      try {
                        dh.setCommandMap(Pooka.getMailcap());
                        ExternalLauncher el = new ExternalLauncher();

                        el.setCommandContext(fCmd, dh);

                        // create a progress dialog for the external launcher
                        int attachmentSize = fAttachment.getSize();
                        if (fAttachment.getEncoding() != null && fAttachment.getEncoding().equalsIgnoreCase("base64"))
                          attachmentSize = (int) (attachmentSize * .73);

                        ProgressDialog dlg;
                        if (getMessageUI() != null) {
                          dlg = getMessageUI().createProgressDialog(0, attachmentSize, 0, "Fetching attachment","Fetching attachment");
                        } else {
                          dlg = Pooka.getUIFactory().createProgressDialog(0, attachmentSize, 0, "Fetching attachment","Fetching attachment");
                        }

                        final ExternalLauncher fLauncher = el;
                        dlg.addCancelListener(new ProgressDialogListener() {
                            public void dialogCancelled() {
                              fLauncher.cancelSave();
                            }
                          });

                        el.setProgressDialog(dlg);

                        if (Pooka.isDebug())
                          System.out.println("opening external launcher with ");
                        el.show();
                      } catch (Exception elException) {
                        getMessageUI().showError("Error opening attachment", elException);
                      }
                    }
                  };

                if (fActionThread != null) {
                  fActionThread.addToQueue(action, new java.awt.event.ActionEvent(AttachmentHandler.this, 0, "attachment-open"));
                } else {
                  action.actionPerformed( new java.awt.event.ActionEvent(AttachmentHandler.this, 0, "attachment-open"));
                }
              }
            }
          }
        });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * This opens up a JFileChooser to let the user choose under what
   * name and where the selected Attachment should be saved.  It then
   * calls saveFileAs() to save the file.
   */
  public void saveAttachment(Attachment pAttachment, Component pComponent) {
    // usually called on the folder thread.  so we need to throw the
    // filechooser over to the AWTEvent thread.

    if (pAttachment != null) {
      final Attachment fAttachment = pAttachment;
      final Component fComponent = pComponent;
      final String fileName = pAttachment.getName();

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JFileChooser saveChooser;
            String currentDirectoryPath = Pooka.getProperty("Pooka.tmp.currentDirectory", "");
            if (currentDirectoryPath == "")
              saveChooser = new JFileChooser();
            else
              saveChooser = new JFileChooser(currentDirectoryPath);

            if (fileName != null)
              saveChooser.setSelectedFile(new File(fileName));

            int saveConfirm = saveChooser.showSaveDialog(fComponent);
            Pooka.getResources().setProperty("Pooka.tmp.currentDirectory", saveChooser.getCurrentDirectory().getPath(), true);
            if (saveConfirm == JFileChooser.APPROVE_OPTION) {
              try {
                // saveFileAs creates a new thread, so don't bother
                // dispatching this somewhere else.
                saveFileAs(fAttachment, saveChooser.getSelectedFile());
              } catch (IOException exc) {
                showError(Pooka.getProperty("error.SaveFile", "Error saving file") + ":\n", Pooka.getProperty("error.SaveFile", "Error saving file"), exc);
              }
            }
          }
        });
    }
  }

  /**
   * This opens up a JFileChooser to let the user choose the location
   * to which to save the attachments.  Then it calls saveFileAs() to save
   * the attachments with their default filenames.
   */
  public void saveAllAttachments(Component pComponent) {
    // usually called on the folder thread.  so we need to throw the
    // filechooser over to the AWTEvent thread.
    try {
      final Component fComponent = pComponent;
      final java.util.List fAttachmentList = mProxy.getAttachments();

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JFileChooser saveChooser;
            String currentDirectoryPath = Pooka.getProperty("Pooka.tmp.currentDirectory", "");
            if (currentDirectoryPath == "")
              saveChooser = new JFileChooser();
            else
              saveChooser = new JFileChooser(currentDirectoryPath);

            saveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int saveConfirm = saveChooser.showSaveDialog(fComponent);
            File selectedDir = saveChooser.getSelectedFile();
            Pooka.getResources().setProperty("Pooka.tmp.currentDirectory", saveChooser.getCurrentDirectory().getPath(), true);
            if (saveConfirm == JFileChooser.APPROVE_OPTION) {
              for (int i = 0; i < fAttachmentList.size(); i++) {
                Attachment currentAttachment = (Attachment) fAttachmentList.get(i);
                String filename = currentAttachment.getName();
                if (filename == null || filename.equals("")) {
                  filename = "savedFile_" + i;
                }
                File currentFile = new File(selectedDir, filename);
                try {
                  // saveFileAs creates a new thread, so don't bother
                  // dispatching this somewhere else.
                  saveFileAs(currentAttachment, currentFile);
                } catch (IOException exc) {
                  showError(Pooka.getProperty("error.SaveFile", "Error saving file") + ":\n", Pooka.getProperty("error.SaveFile", "Error saving file"), exc);
                }
              }
            }
          }
        });
    } catch (javax.mail.MessagingException me) {
      showError("Error getting attachment list", me);
    }


  }

  /**
   * This actually saves the Attachment as the File saveFile.
   */
  public void saveFileAs(Attachment mbp, File saveFile) throws IOException {
    SaveAttachmentThread thread = new SaveAttachmentThread(mbp, saveFile);
    thread.start();
  }


  class SaveAttachmentThread extends Thread {

    Attachment attachment;
    File saveFile;
    ProgressDialog dialog;
    boolean running = true;

    SaveAttachmentThread(Attachment newAttachment, File newSaveFile) {
      attachment = newAttachment;
      saveFile = newSaveFile;
    }

    public void run() {
      InputStream decodedIS = null;
      BufferedOutputStream outStream = null;

      int attachmentSize = 0;

      try {
        decodedIS = attachment.getInputStream();
        attachmentSize = attachment.getSize();
        if (attachment.getEncoding() != null && attachment.getEncoding().equalsIgnoreCase("base64"))
          attachmentSize = (int) (attachmentSize * .73);

        dialog = createDialog(attachmentSize);
        dialog.show();

        outStream = new BufferedOutputStream(new FileOutputStream(saveFile));
        int b=0;
        byte[] buf = new byte[32768];

        b = decodedIS.read(buf);
        while (b != -1 && running) {
          outStream.write(buf, 0, b);
          dialog.setValue(dialog.getValue() + b);
          if (dialog.isCancelled())
            running=false;

          b = decodedIS.read(buf);
        }

      } catch (IOException ioe) {
        showError("Error saving file", ioe);
        cancelSave();
      } finally {
        if (outStream != null) {
          try {
            outStream.flush();
            outStream.close();
          } catch (IOException ioe) {}
        }
        if (dialog != null)
          dialog.dispose();
      }
    }

    /**
     * Creates a progress dialog to show the downloading of an attachment.
     */
    public ProgressDialog createDialog(int attachmentSize) {
      ProgressDialog dlg;
      if (getMessageUI() != null) {
        dlg = getMessageUI().createProgressDialog(0, attachmentSize, 0, saveFile.getName(), saveFile.getName());
      } else {
        dlg = Pooka.getUIFactory().createProgressDialog(0, attachmentSize, 0, saveFile.getName(), saveFile.getName());
      }

      dlg.addCancelListener(new ProgressDialogListener() {
          public void dialogCancelled() {
            cancelSave();
          }
        });
      return dlg;
    }

    public void cancelSave() {
      try {
        saveFile.delete();
      } catch (Exception e) {}
      dialog.dispose();
    }
  } // SaveAttachmentThread

}

