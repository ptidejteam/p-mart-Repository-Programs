package net.suberic.pooka.cache;
import javax.mail.MessagingException;

public class StaleCacheException extends MessagingException {
    long cacheValidity;
    long requestedValidity;

    public StaleCacheException(long validityInCache, long validityRequested) {
	super();
	cacheValidity = validityInCache;
	requestedValidity = validityRequested;
    }

    public long getCacheValidity() { return cacheValidity; }

    public long getRequstedValidity() { return requestedValidity; }
}
