/*
 *  RPGeneration - A role playing utility generate interesting things
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * Class.java
 *
 * Created on November 1, 2002, 2:15 PM
 */

package plugin.doomsdaybook.util;

/**
 *
 * @author  devon
 */
public class Operation implements Comparable {
	String type = "";
	String key = "";
	String value = "";
	String name = "";

	public Operation(String type, String key, String value, String name) {
		this.type = type;
		this.key = key;
		this.value = value;
		this.name = name;
	}

	public Operation(String type, String key, String value) {
		this(type, key, value, "");
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return name;
	}

	public int compareTo(Object obj) {
		String title = this.toString();
		String compared = obj.toString();
		return title.compareTo(compared);
	}
}

