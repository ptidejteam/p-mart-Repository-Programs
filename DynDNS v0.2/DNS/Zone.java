// Copyright (c) 1999 Brian Wellington (bwelling@xbill.org)
// Portions Copyright (c) 1999 Network Associates, Inc.

package DNS;

import java.util.*;
import java.io.*;

public class Zone {

public static final int CACHE = 1;
public static final int PRIMARY = 2;
public static final int SECONDARY = 3;

private Hashtable data;
private Name origin = null;
private int type;

public Zone(String file, int _type) throws IOException {
	type = _type;
	if (type == CACHE)
		origin = Name.root;

	FileInputStream fis;
	try {
		fis = new FileInputStream(file);
	}
	catch (FileNotFoundException e) {
		throw new IOException(e.toString());
	}
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	String line;
	MyStringTokenizer st;

	Record record = null;

	data = new Hashtable();

	while (true) {
		line = IO.readExtendedLine(br);
		if (line == null)
			break;
		if (line.length() == 0 || line.startsWith(";"))
			continue;

		boolean space = line.startsWith(" ") || line.startsWith("\t");
		st = new MyStringTokenizer(line);

		String s = st.nextToken();
		if (s.equals("$ORIGIN")) {
			origin = parseOrigin(st);
			continue;
		}
		st.putBackToken(s);
		record = parseRR(st, space, record, origin);
		Hashtable nametable = (Hashtable) data.get(record.name);
		if (nametable == null) {
			nametable = new Hashtable();
			data.put(record.name, nametable);
		}
		RRset rrset = (RRset) nametable.get(new Short(record.type));
		if (rrset == null) {
			rrset = new RRset(record.name, record.type);
			nametable.put(new Short(record.type), rrset);
		}
		rrset.addRR(record);
	}
}

public Hashtable
findName(Name name) {
	return (Hashtable) data.get(name);
}

public Name
getOrigin() {
	return origin;
}

Name
parseOrigin(MyStringTokenizer st) throws IOException {
	return new Name(st.nextToken());
}

Record
parseRR(MyStringTokenizer st, boolean useLast, Record last, Name origin)
throws IOException
{
	Name name;
	int ttl;
	short type, dclass;

	if (!useLast)
		name = new Name(st.nextToken(), origin);
	else
		name = last.name;

	String s = st.nextToken();

	try {
		ttl = Integer.parseInt(s);
		s = st.nextToken();
	}
	catch (NumberFormatException e) {
		if (!useLast || last == null)
			ttl = 3600;
		else
			ttl = last.ttl;
	}

	if ((dclass = dns.classValue(s)) > 0)
		s = st.nextToken();
	else
		dclass = dns.IN;
		

	if ((type = dns.typeValue(s)) < 0)
		throw new IOException("Parse error");

	return Record.fromString(name, type, dclass, ttl, st, origin);
}

}
