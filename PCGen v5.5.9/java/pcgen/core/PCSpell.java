package pcgen.core;

import java.util.List;
import pcgen.core.utils.Utility;

/**
 * This class is a spell granted by a class, template, etc.
 * These objects are typically created by a SPELL tag in an LST
 * file, but may be added from other sources as well.
 *
 * @author sage_sam
 */
public class PCSpell extends PObject {

    // name: inherited
    // number of times/day
    private String timesPerDay = null;

    // spellbook
    private String spellbook = null;


    public PCSpell() {
        super();
//		super.addPreReq(spellbook)
    }

    /**
     * Returns the spellbook.
     * @return String
     */
    public String getSpellbook() {
        return spellbook;
    }

    /**
     * Returns the timesPerDay.
     * @return String
     */
    public String getTimesPerDay() {
        return timesPerDay;
    }

    /**
     * Sets the spellbook.
     * @param spellbook The spellbook to set
     */
    public void setSpellbook(String spellbook) {
        this.spellbook = spellbook;
    }

    /**
     * Sets the timesPerDay.
     * @param timesPerDay The timesPerDay to set
     */
    public void setTimesPerDay(String timesPerDay) {
        this.timesPerDay = timesPerDay;
    }

    /**
     * @see pcgen.core.PObject#getPCCText()
     */
    public String getPCCText() {
        StringBuffer sBuff = new StringBuffer();
        sBuff.append(name);
        sBuff.append('|');
        sBuff.append(timesPerDay);
        sBuff.append('|');
        sBuff.append(spellbook);

        List preReqs = getPreReqList();
        if ((preReqs != null) && (preReqs.size() > 0)) {
            sBuff.append('[');
            sBuff.append(Utility.join(preReqs, '|'));
            sBuff.append(']');
        }
        return sBuff.toString();
    }

}
