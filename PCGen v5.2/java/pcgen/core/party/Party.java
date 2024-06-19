/**
 * $Id: Party.java,v 1.1 2006/02/21 01:13:41 vauchers Exp $
 */
package pcgen.core.party;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.PCGen_Frame1;
import pcgen.io.PCGIOHandler;
import pcgen.util.FileHelper;
import pcgen.util.GuiFacade;
import pcgen.util.Logging;

/**
 * Class to encapsulate the functionality of loading and saving parties of characters.  Also used
 * to load a single character.  This is my first stab at decoupling some of the party loading and
 * saving logic from the gui.  It's not complete and still a little messy, but at least it gets us
 * on the right track with the decoupling.
 */
public class Party
{
	private File partyFile;
	private List characterFiles = new ArrayList();

	private Party()
	{
	}

	/**
	 * Create a Party which will use the file as the storage location.  This can then be loaded with the load method.
	 * You can also save to the file.
	 * @param partyFile The filename of the pcp file.
	 */
	public static Party makePartyFromFile(File partyFile)
	{
		return new Party(partyFile);
	}

	/**
	 * Create a new Party with a single character in it.  The character can be loaded with the load method.
	 * @param characterFile A file containing a pcgen character.
	 */
	public static Party makeSingleCharacterParty(File characterFile)
	{
		Party party = new Party();
		party.characterFiles.add(characterFile);
		return party;
	}

	private Party(File partyFile)
	{
		this.partyFile = partyFile;
	}

	public void setPartyFile(File partyFile)
	{
		this.partyFile = partyFile;
	}

	private boolean loadCharacterFiles()
	{
		boolean success = true;
		for (int i = 0; i < characterFiles.size(); i++)
		{
			File file = (File) characterFiles.get(i);
			success = loadPCFromFile(file);
		}
		return success;
	}

	/**
	 * todo:figure out how to decouple this from the gui.  I don't like having the gui intruding here!
	 * todo:figure out how to not return a true/false flag.  That's not good OO coding.  Exceptions are a
	 *      better way to handle exceptional conditions, as they contain information about the failure.
	 * @param mainFrame The PCGen gui.  It needs to add tabs and such when characters are loaded.
	 * @return True if the party is sucessfully loaded.  False if there are any problems during the load.
	 */
	public boolean load(PCGen_Frame1 mainFrame)
	{
		if (partyFile == null)
		{
			return loadCharacterFiles();
		}
		try
		{
			//final BufferedReader br = new BufferedReader(new FileReader(partyFile));
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(partyFile), "UTF-8"));
			//load version info
			br.readLine(); //Read and throw away version info. May change to actually use later
			//load character filename data
			final String charFiles = br.readLine();
			br.close();

			//we no longer load campaign/source infor from the party partyFile
			// in this space we could check the VERSION tag of versionInfo for whatever we wanted
			// if the String didn't start with VERSION: then we know it's a really old PCP partyFile

			boolean success = true;
			//parse PC data and load the listed PC's
			final StringTokenizer fileNames = new StringTokenizer(charFiles, ",");
			while (fileNames.hasMoreTokens())
			{
				String fileName = fileNames.nextToken();
				File characterFile = buildCharacterFile(fileName);
				if (!characterFile.exists())
				{
					// try using the global pcg path
					characterFile = new File(SettingsHandler.getPcgPath(), fileName);
				}
				if (characterFile.exists())
				{
					characterFiles.add(characterFile);
					// if called from the GUI, then use the GUI's PC loader so that we get the PC tabs built
					if (mainFrame != null)
					{
						mainFrame.loadPCFromFile(characterFile);
					}
					else
					{
						// otherwise, do it the quick-n-dirty way
						loadPCFromFile(characterFile);
					}
				}
				else
				{
					Logging.errorPrint("Character file does not exist: " + fileName);
					success = false;
				}
			}
			Globals.sortCampaigns();
			return success;
		}
		catch (IOException ex)
		{
			Logging.errorPrint("Error loading party partyFile.", ex);

			if (Globals.getUseGUI())
			{
				//todo:i18n this message
				GuiFacade.showMessageDialog(null, "Could not load party partyFile.", "PCGen", GuiFacade.ERROR_MESSAGE);
			}
		}
		return false;
	}

	private File buildCharacterFile(final String fileName)
	{
		return new File(partyFile.getParentFile().getAbsolutePath() + fileName);
	}

	private static boolean loadPCFromFile(File file)
	{
		final PlayerCharacter newPC = new PlayerCharacter();
		final PCGIOHandler ioHandler = new PCGIOHandler();
		ioHandler.read(newPC, file.getAbsolutePath());
		if (Globals.getUseGUI())
		{
			for (Iterator it = ioHandler.getErrors().iterator(); it.hasNext();)
			{
				GuiFacade.showMessageDialog(null, "Error: " + (String) it.next(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			}
			for (Iterator it = ioHandler.getWarnings().iterator(); it.hasNext();)
			{
				GuiFacade.showMessageDialog(null, "Warning: " + (String) it.next(), Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
			}
		}
		else
		{
			for (Iterator it = ioHandler.getMessages().iterator(); it.hasNext();)
			{
				Logging.errorPrint((String) it.next());
			}
		}
		// if we've had errors, then abort trying to add the new PC, it's most likely "broken"
		//  if it's not broken, then only warnings should have been generated, and we won't count those
		if (ioHandler.getErrors().size() > 0)
		{
			return false;
		}

		// Set the filename so that future checks to see if file already loaded will work
		newPC.setFileName(file.getAbsolutePath());
		Globals.getPCList().add(newPC);
		Globals.setCurrentPC(newPC);
		Globals.sortCampaigns();
		return true;
	}

	public void save() throws IOException
	{
		if (partyFile == null)
		{
			throw new FileNotFoundException("The file to save this party to is null");
		}
		//BufferedWriter writer = new BufferedWriter(new FileWriter(partyFile));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(partyFile), "UTF-8"));
		// Save party partyFile data here
		// Save version info here (we no longer save campaign/source info in the party partyFile)
		ResourceBundle properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
		writer.write("VERSION:");
		writer.write(properties.getString("VersionNumber"));
		writer.newLine();

		for (int i = 0; i < characterFiles.size(); i++)
		{
			File file = (File) characterFiles.get(i);
			writer.write(FileHelper.findRelativePath(partyFile, file) + ",");
		}

		writer.newLine(); // don't write files without terminators.
		writer.close();
		SettingsHandler.setPcgPath(partyFile.getParentFile()); //still set this, we want .pcp and .pcg files in the same place
	}

	public void addAllOpenCharacters()
	{
		Iterator characters = Globals.getPCList().iterator();
		//save PC filenames
		while (characters.hasNext())
		{
			final PlayerCharacter character = (PlayerCharacter) characters.next();
			characterFiles.add(new File(character.getFileName()));

		}
	}

	public String getDisplayName()
	{
		String displayName = partyFile.getName();
		final int lastDot = displayName.lastIndexOf('.');
		if (lastDot >= 0)
		{
			displayName = displayName.substring(0, lastDot);
		}

		return displayName;
	}
}
