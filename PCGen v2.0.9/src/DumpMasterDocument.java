public class DumpMasterDocument
{
    public static void main (String[] args)
    {
	try {
	    pcgen.util.Boot.SHOW_FILES_AS_FOUND = true;
	    pcgen.xml.Boot.dumpMasterDocument (System.out);
	    System.out.println ( );
	}

	catch (Exception e) {
	    e.printStackTrace ( );
	    System.exit (1);
	}
    }
}
