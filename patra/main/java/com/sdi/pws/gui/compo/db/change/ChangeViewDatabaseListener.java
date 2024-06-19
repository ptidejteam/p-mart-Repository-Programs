/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.gui.compo.db.change;

/**
 * All events contain references to the *internal* PWS structures. The reason is because of identity.
 * If you want the ChangeView counterparts, you have to look them up yourself (or create them yourself).
 * This is a very important design decision.
 */
public interface ChangeViewDatabaseListener
{
    void fieldChange(ChangeViewFieldEvent aEvt);
    void recordChange(ChangeViewRecordEvent aEvt);
    void dbChange(ChangeViewDatabaseEvent aEvt);
}