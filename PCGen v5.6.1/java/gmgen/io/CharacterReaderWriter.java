package gmgen.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import pcgen.core.PlayerCharacter;
import pcgen.io.PCGIOHandler;
import pcgen.util.Logging;

/**
 * This class is used to read in <code>PlayerCharacter</code> objects
 * and to save them when needed.<br>
 * Wraps the PCGIOHandler object.
 * @author Expires 2003
 * @version 2.10
 */
public class CharacterReaderWriter{

	/** The player character that will be created from file. */
	private PlayerCharacter aPC;

	/** The PCGen Input Output handler. */
	private PCGIOHandler ioHandler = new PCGIOHandler();

	/**
	 * Creates an instance of this class taking in the empty character
	 * which will be populated.
	 * @param aPC the player character (will be empty).
	 */
	public CharacterReaderWriter(PlayerCharacter aPC)
	{
		setPC(aPC);
	}
	/**
	 * Creates an instance of this class.  It will created a character that
	 * will be sent back.
	 */
	public CharacterReaderWriter(){
		this(new PlayerCharacter());
	}

	/**
	 * Pulls data from the given input stream and uses it to populate a
	 * character.
	 * @param in the InputStream you want to draw data from.
	 */
	public void readCharacterFromStream(InputStream in){
		readCharacterFromStream(getPC(), in);
	}

	/**
	 * Pulls data from the given input stream and uses it to populate the
	 * given PlayerCharacter.
	 * @param aPC the PlayerCharacter you want to populate.
	 * @param in the InputStream you want to draw data from.
	 */
	public void readCharacterFromStream(PlayerCharacter aPC, InputStream in){
		ioHandler.read(aPC,in);
	}

	/**
	 * Pulls data from the given file and uses it to populate a character.
	 * @param file the File you want to draw data from.
	 */
	public void readCharacterFromFile(File file){
		ioHandler.read(this.aPC,file.getAbsolutePath());
	}

	/**
	 * Pulls data from the given file and uses it to populate the given
	 * PlayerCharacter.
	 * @param aPC the character you want to populate.
	 * @param file the File you want to draw data from.
	 */
	public void readCharacterFromFile(PlayerCharacter aPC, File file){
		try{
			ioHandler.read(aPC, file.getAbsolutePath());
		} catch (java.lang.NullPointerException e){
			Logging.debugPrint("Ooops! Someone passed CharacterReaderWriter a null!");
			if (aPC == null) {
				Logging.errorPrint("aPC is null!");
			}
			else if (file == null) {
				Logging.errorPrint("file is null!");
			}
			else if (ioHandler == null) {
				Logging.errorPrint("ioHandler is null!");
			}
			Logging.errorPrint("IO", e);
		}
	}

	/**
	 * Sends a character to the output stream.
	 * @param out the OutputStream you want to send data to.
	 */
	public void writeCharacterToStream(OutputStream out){
		writeCharacterToStream(getPC(), out);
	}

	/**
	 * Sends the given character to the given output stream.
	 * @param aPC the character you want to save.
	 * @param out the OutputStream you want to send data to
	 */
	public void writeCharacterToStream(PlayerCharacter aPC, OutputStream out){
		ioHandler.write(aPC,out);
	}

	/**
	 * Sends the character to the given file.
	 * @param file the File you want to save to.
	 */
	public void writeCharacterToFile(File file){
		ioHandler.write(this.aPC, file.getAbsolutePath());
	}

	/**
	 * Saves the given character to file.
	 * @param aPC the character you want to save.
	 * @param file the File you want to save to.
	 */
	 public void writeCharacterToFile(PlayerCharacter aPC, File file){
		ioHandler.write(aPC,file.getAbsolutePath());
	}

	/**
	 * Gets the character for this class.
	 * @return the character.
	 */
	public PlayerCharacter getPC(){
		return this.aPC;
	}

	/**
	 * Sets the character for this class.
	 * @param aPC the character you want to be the default.
	 */
	public void setPC(PlayerCharacter aPC){
		this.aPC = aPC;
	}
}
