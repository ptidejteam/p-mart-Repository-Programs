package net.suberic.pooka;
import java.io.*;

/**
 * This is a simple SignatureGenerator which reads a file and returns its
 * contents as a signature.
 */
public class FileSignatureGenerator extends SignatureGeneratorImpl {

    public FileSignatureGenerator() {
    }

    /**	
     * Returns the contents of the file UserProfile.username.signatureFile
     * as a signature.
     */
    
    public String generateSignature(String text) {
	if (profile != null) {
	    String fileName = Pooka.getProperty("UserProfile." + profile.getName() + ".signatureFile", "");
	    if (!fileName.equals("")) {
		File f = new File(fileName);
		if (f.exists() && f.canRead()) {
		    try {
			StringBuffer returnValue = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(f));
			boolean done = false;
			while (!done) {
			    String nextLine = reader.readLine();
			    if (nextLine != null) {
				returnValue.append(nextLine);
				returnValue.append("\n");
			    } else {
				done = true;
			    }
			}

			return returnValue.toString();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	}

	return null;
    }

}
