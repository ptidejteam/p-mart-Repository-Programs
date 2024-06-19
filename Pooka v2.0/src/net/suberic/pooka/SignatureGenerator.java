package net.suberic.pooka;

/**
 * This is the interface for a class which can produce a signature.
 */
public interface SignatureGenerator {

    /**
     * Produce a signature String which is appropriate for the given
     * UserProfile and message text.
     */
    public String generateSignature(String text);

    public void setProfile(UserProfile p);

    public UserProfile getProfile();
}
