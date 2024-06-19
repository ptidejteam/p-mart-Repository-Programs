package net.suberic.pooka;

/**
 * This is a simple SignatureGenerator which just returns a property
 * value as a signature.
 */
public class StringSignatureGenerator extends SignatureGeneratorImpl {

    public StringSignatureGenerator() {
    }

    /**	
     * Returns the UserProfile.username.signature property as a signature.
     */
    
    public String generateSignature(String text) {
	if (profile != null) {
	    String returnValue = Pooka.getProperty("UserProfile." + profile.getName() + ".signature", "");
	    if (!returnValue.equals(""))
		return returnValue;
	}

	return null;
    }

}
