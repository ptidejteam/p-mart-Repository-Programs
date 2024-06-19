package gombos.webbrowser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created on Feb 22, 2006
 * 
 *  Author: Andrew Gombos
 *
 *  Apply regexes to the HTML, and create a list of opening and closing tags
 *  
 *  These are sorted by starting index (so an unbalanced tag set will be obvious)
 */

public class RegexGatherer extends ArrayList {
	/**
	 * Collect all the matches for the tag regexes 
	 */
	public void gatherMatches(StringBuffer data) {
		Pattern openingTag =
			Pattern.compile(
				"<(b|u|i|a|font)([ \\w]*=.*?)?>",
				Pattern.CASE_INSENSITIVE);
		Pattern closingTag =
			Pattern.compile("<(/(b|u|i|a|font))>", Pattern.CASE_INSENSITIVE);

		Matcher openM = openingTag.matcher(data);
		Matcher closeM = closingTag.matcher(data);

		while (openM.find()) {
			add(
				new Match(
					openM.start(),
					openM.end(),
					openM.group(1),
					openM.group(2)));
		}

		while (closeM.find()) {
			add(new Match(closeM.start(), closeM.end(), closeM.group(1), ""));
		}

		Collections.sort(this, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return ((Match) arg0).start - ((Match) arg1).start;
			}
		});
	}

	/**
	 * Represent a Regex match. Basically the same as MatchResult in J2SE 1.5
	 */
	public class Match {
		/**
		 * Create a match record.  Also normalize tags to remove leading and trailing spaces,
		 * and make them all lowercase 
		 */
		public Match(int start, int end, String tag, String attributes) {
			this.start = start;
			this.end = end;

			if (tag != null)
				this.tag = tag.trim().toLowerCase();
			else
				this.tag = tag;

			if (attributes != null)
				this.attrs = attributes.trim().toLowerCase();
			else
				this.attrs = attributes;
		}

		public String toString() {
			return start + "->" + end + ": " + tag + "\n";
		}

		public int start, end;
		public String tag, attrs;
	}
}
