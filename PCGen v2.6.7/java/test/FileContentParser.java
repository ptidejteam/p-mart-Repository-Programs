/**
 * FileContentParser.java
 * Provides services for parsing a file that has nested tokens
 * defined by more than one set of delimiters.
 *
 * It assumes the the clients using it have implemented the
 * iParsingClient interface.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version  1.0
 */

import java.io.File;
import java.io.FileInputStream;

public class FileContentParser extends StringContentParser
{

	/**
	 * Returns the complete contents of a file as a String
	 *
	 * @param fileName   the name of the file to read.
	 *
	 * @return  the contents of the file as a String object
	 */
	private static String getFileContents(String fileName)
	{
		try
		{
			File aFile = new File(fileName);
			FileInputStream aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			byte[] inputLine = new byte[length];

			aStream.read(inputLine, 0, length);
			aStream.close();

			return new String(inputLine);
		}
		catch (Exception exception)
		{
			System.out.println("ERROR:" + fileName +
				" Exception type:" +
				exception.getClass().getName() + " Message:" +
				exception.getMessage());
			return "";
		}
	}

	/**
	 * A constructor that accepts all the details needed to
	 * parse a file.  I did it this way mostly out of laziness.
	 * I don't want to have to deal with checking to see if any
	 * items are missing, and it fits in with my philosphy of
	 * not letting an object get into a state where it can't
	 * process.
	 *
	 * These parameters are merely passed on the the setParms
	 * method.
	 *
	 * @param fileName    the name of a file to read
	 * @param delimiters  an array of delimiters to using when parsing
	 *                    various elements of the file
	 * @param client      and object that will receive the last
	 *                    parsed item in the tree.
	 */
	public FileContentParser(String fileName,
		String[] delimiters,
		iParsingClient client)
	{
		super(getFileContents(fileName), delimiters, client);
	}

	/**
	 * The main function visible to clients, this version accepts
	 * the same information as the constructor.  This is to allow
	 * reusing the object for another file or object without having
	 * to endure the overhead of object creation and deletion.
	 *
	 * These parameters are merely passed on the the setParms
	 * method.
	 *
	 * @param fileName    the name of a file to read
	 * @param delimiters  an array of delimiters to using when parsing
	 *                    various elements of the file
	 * @param client      and object that will receive the last
	 *                    parsed item in the tree.
	 */
	public void parse(String fileName,
		String[] delimiters,
		iParsingClient client)
	{
		super.parse(getFileContents(fileName), delimiters, client);
	}

}
