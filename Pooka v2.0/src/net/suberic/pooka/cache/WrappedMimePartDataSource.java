package net.suberic.pooka.cache;
import javax.mail.internet.*;

public class WrappedMimePartDataSource extends MimePartDataSource {
  MessageCache cache;
  long uid;
  MimePart localPart;

  public WrappedMimePartDataSource(MimePart mm, MessageCache newCache, long newUid) {
    super(mm);
    cache=newCache;
    uid = newUid;
  }
  
  public java.io.InputStream getInputStream() throws java.io.IOException {
    try {
      return super.getInputStream();
    } catch (java.io.IOException ioe) {
      if (localPart instanceof MimeMessage) {
	try {
	  // pretend it's 7bit encoding.
	  java.io.InputStream is = ((MimeMessage)localPart).getRawInputStream();
	  return MimeUtility.decode(is, "7bit");
	} catch (Exception e) {
	  cache.invalidateCache(uid, MessageCache.CONTENT);
	  throw ioe;
	}
      } else {
	cache.invalidateCache(uid, MessageCache.CONTENT);
	throw ioe;
      }
    }
  }

}
