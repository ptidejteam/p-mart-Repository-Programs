package net.suberic.pooka;

/**
 * A convenience class which implements setProfile and getProfile.
 */
public abstract class SignatureGeneratorImpl implements SignatureGenerator {

    protected UserProfile profile;

    public void setProfile(UserProfile p) {
	profile = p;
    }

    public UserProfile getProfile() {
	return profile;
    }
}
