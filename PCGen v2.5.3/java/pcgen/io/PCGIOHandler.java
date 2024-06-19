/*
 * PCGIOHandler.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 11, 2002, 8:30 PM
 */

package pcgen.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.Spell;
import pcgen.core.Utility;

/**
 * <code>PCGIOHandler</code><br>
 * Reading and Writing PlayerCharacters in PCGen's own format (PCG).
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision: 1.1 $
 */

public class PCGIOHandler extends IOHandler
{
        public static final String s_CHECKLOADEDCAMPAIGNS = "Check loaded campaigns.";

        private List errors = new ArrayList();
        private List warnings = new ArrayList();
	private PlayerCharacter aPC;

        /**
         * Selector
         *
         * <br>author: Thomas Behr 18-03-02
         *
         * @return a list of error messages
         */
        public List getErrors()
        {
                return errors;
        }

        /**
         * Convenience Method
         *
         * <br>author: Thomas Behr 18-03-02
         *
         * @return a list of messages
         */
        public List getMessages()
        {
                List messages = new ArrayList();

                for (Iterator it = errors.iterator(); it.hasNext();) {
                        messages.add("Error: " + (String)it.next());
                }
                for (Iterator it = warnings.iterator(); it.hasNext();) {
                        messages.add("Warning: " + (String)it.next());
                }

                return messages;
        }

        /**
         * Selector
         *
         * <br>author: Thomas Behr 15-03-02
         *
         * @return a list of warning messages
         */
        public List getWarnings()
        {
                return warnings;
        }

	/**
	 * Writes the contents of the given PlayerCharacter to a stream
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to write
	 * @param out   the stream to be written to
	 */
	public void write(PlayerCharacter aPC, OutputStream out)
	{
		this.aPC = aPC;

		String pcgString = (new PCGVer0Creator(aPC)).createPCGString();
		try
		{
			// maybe wrap this in a BufferedWriter for performance???
			out.write(pcgString.getBytes(), 0, pcgString.length());
			aPC.setDirty(false);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}


	/**
	 * Reads the contents of the given PlayerCharacter from a stream
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to store the read data
	 * @param in    the stream to be read from
	 */
	public void read(PlayerCharacter aPC, InputStream in)
	{
                this.aPC = aPC;

                warnings.clear();

                StringBuffer buffer = new StringBuffer();

                try
                {
                        BufferedReader br =
                                new BufferedReader(
                                        new InputStreamReader(in));
//                          int c;
//                          while((c = br.read()) != -1) {
//                                  buffer.append((char)c);
//                          }
                        String line;
                        while ((line = br.readLine()) != null) {
                                // fake line
                                //if (line.length() == 0) {
                                //        buffer.append(" ");
                                //}
                                buffer.append(line).append("\n");
                        }
                }
                catch (IOException ioe) {
                        ioe.printStackTrace();
                }

		aPC.setImporting(true);

                PCGVer0Parser parser = new PCGVer0Parser(aPC);
		try {
                        parser.parsePCG(buffer.toString());
                } catch (PCGParseException pcgex) {
                        errors.add(pcgex.getMessage() +
                                   "\nMethod: " + pcgex.getMethod() +
                                   "\nLine: " + pcgex.getLine());
                }
                warnings.addAll(parser.getWarnings());

		try {
                        sanityChecks();
                } catch (Exception ex) {
                        errors.add(ex.getMessage() +
                                   "\nMethod: sanityChecks");
                }

		aPC.setImporting(false);
	}


	/**
	 * Returns the PCG file format string, i.e. "PCG Format"
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @return "PCG Format"
	 */
	public String getFileFormatString()
	{
		return "PCG Format";
	}


	/**
	 * Returns the PCG file name extension, i.e. "pcg"
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @return "pcg"
	 */
	public String getFileNameExtension()
	{
		return "pcg";
	}


	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */

        private void sanityChecks()
        {
                // Hit point sanity check
                boolean bFixMade = false;

                int iSides;
                int iRoll;
                int oldHp = aPC.hitPoints();

                Race aRace = aPC.getRace();
                if (aRace.hitDice() != 0)
                {
                        iSides = aRace.getHitDiceSize();
                        for (int i = 0; i < aRace.hitDice(); i++)
                        {
                                iRoll = aRace.getHitPointList(i).intValue();
                                if (iRoll > iSides)
                                {
                                        aRace.setHitPoint(i, new Integer(iSides));
                                        bFixMade = true;
                                }
                                if (iRoll < 1)
                                {
                                        aRace.setHitPoint(i, new Integer(1));
                                        bFixMade = true;
                                }
                        }
                }

                Feat aFeat;
                for (Iterator it = aPC.getFeatList().iterator(); it.hasNext();)
                {
                        aFeat = (Feat)it.next();
                        if (aFeat.getChoiceString().startsWith("SALIST|"))
                        {
                                ArrayList aAvailable = new ArrayList();
                                ArrayList aBonus = new ArrayList();
                                aFeat.buildSALIST(aFeat.getChoiceString(), aAvailable, aBonus);
                                for (int i = 0; i < aFeat.getAssociatedList().size(); i++)
                                {
                                        String aString = (String)aFeat.getAssociatedList().get(i);
                                        final String prefix = aString + "|";
                                        boolean bLoop = true;
                                        while (true)
                                        {
                                                int x;
                                                for (x = 0; x < aBonus.size(); x++)
                                                {
                                                        final String bString = (String)aBonus.get(x);
                                                        if (bString.startsWith(prefix))
                                                        {
                                                                String tmp = bString.substring(bString.indexOf('|') + 1);
                                                                aFeat.addBonusList(tmp);
                                                                break;
                                                        }
                                                }
                                                if ((x < aBonus.size()) || !bLoop) {
                                                        break;
                                                }

                                                bLoop = false;		// Avoid infinite loops at all costs!

                                                // Do direct replacement if only 1 choice
                                                if (aBonus.size() == 1)
                                                {
                                                        aString = (String)aBonus.get(0);
                                                        aString = aString.substring(0, aString.indexOf('|'));
                                                }
                                                else
                                                {
                                                        /*
                                                         * need to come up with a method that will allow to
                                                         * remove the necessity of swing to be used here
                                                         *
                                                         * author: Thomas Behr 15-03-02
                                                         */
                                                        while (true)
                                                        {
                                                                final String message =
                                                                        aFeat.getName() +
                                                                        " has been modified and PCGen is unable to " +
                                                                        "determine your previous selection(s).\n\n" +
                                                                        "This box will pop up once for each time you " +
                                                                        "have taken the feat.";

                                                                Object selectedValue =
                                                                        JOptionPane.showInputDialog(null,
                                                                                                    message,
                                                                                                    Globals.s_APPNAME,
                                                                                                    JOptionPane.INFORMATION_MESSAGE,
                                                                                                    null,
                                                                                                    aAvailable.toArray(),
                                                                                                    aAvailable.get(0));
                                                                if (selectedValue != null)
                                                                {
                                                                        aString = (String)selectedValue;
                                                                        break;
                                                                }
                                                                JOptionPane.showMessageDialog(null,
                                                                                              "You MUST make a selection",
                                                                                              Globals.s_APPNAME,
                                                                                              JOptionPane.INFORMATION_MESSAGE);
                                                        }
                                                }
                                                aFeat.getAssociatedList().set(i, aString);
                                        }
                                }
                        }
                        else if (aFeat.getChoiceString().startsWith("NONCLASSSKILLLIST|"))
                        {
                                /*
                                 * This is intended to address the problem of the Cosmopolitan feat not re-applying
                                 * classskill status to selected skills when the PC is reloaded
                                 * If this results in odd behavior for the only other feat I've seen
                                 * that uses NONCLASSSKILLLIST (Reincarnated from Ravenloft) I haven't seen it
                                 * Lone Jedi (Feb. 5, 2002)
                                 */
                                Skill aSkill;
                                String skillString;
                                for (Iterator it2 = Globals.getSkillList().iterator(); it2.hasNext();)
                                {
                                        aSkill = (Skill)it2.next();
                                        for (Iterator it3 = aFeat.getAssociatedList().iterator(); it3.hasNext();)
                                        {
                                                skillString = (String)it3.next();
                                                if (aSkill.getRootName().equals(skillString)) {
                                                        aFeat.getCSkillList().add(aSkill.getName());
                                                }
                                        }
                                }
                        }
                }

                PCClass aClass;
                if (aPC.getClassList() != null)
                {
                        for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
                        {
                                aClass = (PCClass)it.next();
                                // Ignore if no levels
                                if (aClass.getLevel().intValue() < 1)
                                {
                                        continue;
                                }

                                // Walk through the levels for this class
                                iSides = aClass.getHitDie();
                                for (int i = 0; i < aClass.getLevel().intValue(); i++)
                                {
                                        iRoll = aClass.getHitPointList(i).intValue();
                                        if (iRoll > iSides)
                                        {
                                                aClass.setHitPoint(i, new Integer(iSides));
                                                bFixMade = true;
                                        }
                                        if (iRoll < 1)
                                        {
                                                aClass.setHitPoint(i, new Integer(1));
                                                bFixMade = true;
                                        }
                                }
                        }
                }

                if (bFixMade)
                {
                        String message =
                                "Fixed illegal value in hit points. " +
                                "Current character hit points: " + aPC.hitPoints() + " not " + oldHp;
                        warnings.add(message);
                }

		// sometimes another class, feat, item, whatever can affect what spells or whatever
		// would have been available for a class, so this simply lets the level advancement routine
		// take into account all the details known about a character now that the import is
		// completed. The level isn't affected.  merton_monk@yahoo.com 2/15/2002
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass)it.next();
			aClass.setLevel(aClass.getLevel());
		}
        }
}
