// Copyright (c) 1999 Brian Wellington (bwelling@xbill.org)
// Portions Copyright (c) 1999 Network Associates, Inc.

package DNS;

import java.io.*;
import java.util.*;
import DNS.utils.*;

public class SOARecord extends Record {

Name host, admin;
int serial, refresh, retry, expire, minimum;

public
SOARecord(Name _name, short _dclass, int _ttl, Name _host, Name _admin,
	  int _serial, int _refresh, int _retry, int _expire, int _minimum)
throws IOException
{
	super(_name, dns.SOA, _dclass, _ttl);
	host = _host;
	admin = _admin;
	serial = _serial;
	refresh = _refresh;
	retry = _retry;
	expire = _expire;
	minimum = _minimum;
}

public
SOARecord(Name _name, short _dclass, int _ttl, int length,
	  CountedDataInputStream in, Compression c) throws IOException
{
	super(_name, dns.SOA, _dclass, _ttl);
	if (in == null)
		return;
	host = new Name(in, c);
	admin = new Name(in, c);
	serial = in.readInt();
	refresh = in.readInt();
	retry = in.readInt();
	expire = in.readInt();
	minimum = in.readInt();
}

public
SOARecord(Name _name, short _dclass, int _ttl, MyStringTokenizer st,
	     Name origin)
throws IOException
{
	super(_name, dns.SOA, _dclass, _ttl);
	host = new Name(st.nextToken(), origin);
	admin = new Name(st.nextToken(), origin);
	serial = Integer.parseInt(st.nextToken());
	refresh = Integer.parseInt(st.nextToken());
	retry = Integer.parseInt(st.nextToken());
	expire = Integer.parseInt(st.nextToken());
	minimum = Integer.parseInt(st.nextToken());
}


public String
toString() {
	StringBuffer sb = toStringNoData();
	if (host != null) {
		sb.append(host);
		sb.append(" ");
		sb.append(admin);
		sb.append(" (\n\t\t\t\t\t");
		sb.append(serial);
		sb.append("\t; serial\n\t\t\t\t\t");
		sb.append(refresh);
		sb.append("\t; refresh\n\t\t\t\t\t");
		sb.append(retry);
		sb.append("\t; retry\n\t\t\t\t\t");
		sb.append(expire);
		sb.append("\t; expire\n\t\t\t\t\t");
		sb.append(minimum);
		sb.append(")\t; minimum");
	}
	return sb.toString();
}

public Name
getHost() {  
	return host;
}       

public Name
getAdmin() {  
	return admin;
}       

public int
getSerial() {  
	return serial;
}       

public int
getRefresh() {  
	return refresh;
}       

public int
getRetry() {  
	return retry;
}       

public int
getExpire() {  
	return expire;
}       

public int
getMinimum() {  
	return minimum;
}       

byte []
rrToWire(Compression c) throws IOException {
	ByteArrayOutputStream bs = new ByteArrayOutputStream();
	CountedDataOutputStream ds = new CountedDataOutputStream(bs);

	host.toWire(ds, c);
	admin.toWire(ds, c);
	ds.writeInt(serial);
	ds.writeInt(refresh);
	ds.writeInt(retry);
	ds.writeInt(expire);
        ds.writeInt(minimum);

	return bs.toByteArray();
}

}
