/*
 * Created on 8 juil. 2003
 *
 */
package org.gudy.azureus2.ui.swt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

/**
 * @author Olivier
 * 
 */
public class StartServer extends Thread {

  public static final String ACCESS_STRING = "Azureus Start Server Access";
  private ServerSocket socket;
  private int state;
  private Main main;

  private boolean bContinue;
  public static final int STATE_FAULTY = 0;
  public static final int STATE_LISTENING = 1;

  public StartServer(Main main) {
    super("Start Server");
    try {
      this.main = main;
      //socket = new ServerSocket(6880);
	  socket = new ServerSocket(6880, 50, InetAddress.getByName("127.0.0.1")); //NOLAR: only bind to localhost
      state = STATE_LISTENING;
    }
    catch (Exception e) {
      state = STATE_FAULTY;
    }
  }

  public void run() {
    bContinue = true;
    while (bContinue) {
      BufferedReader br = null;
      try {
        Socket sck = socket.accept();
        String address = sck.getInetAddress().getHostAddress();
        if (address.equals("localhost") || address.equals("127.0.0.1")) {
          br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
          String line = br.readLine();
          //System.out.println("received : " + line);
          if (line != null) {
            main.showMainWindow();
            StringTokenizer st = new StringTokenizer(line, ";");           
            int i = 0;
            if(st.countTokens() > 1) {
              String args[] = new String[st.countTokens() - 1];
              String checker = st.nextToken();
                if(checker.equals(ACCESS_STRING)) {
                  while (st.hasMoreElements()) {              
                    args[i++] = st.nextToken().replaceAll("&;", ";").replaceAll("&&", "&");
                  }
                  main.useParam(args);
              }
            }
          }
        }
        sck.close();

      }
      catch (Exception e) {
        if(!(e instanceof SocketException))
          e.printStackTrace();        
        //bContinue = false;
      } finally {
        try {
          if (br != null)
            br.close();
        } catch (Exception e) { /*ignore */
        }
      }
    }
  }

  public void stopIt() {
    bContinue = false;
    try {
      socket.close();
    }
    catch (Exception e) {/*ignore */}
  }
  /**
   * @return
   */
  public int getState() {
    return state;
  }

}
