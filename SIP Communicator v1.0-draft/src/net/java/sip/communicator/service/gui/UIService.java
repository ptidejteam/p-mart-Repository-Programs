/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.contactlist.*;

/**
 * The <tt>UIService</tt> offers generic access to the graphical user interface
 * for all modules that would like to interact with the user.
 * <p>
 * Through the <tt>UIService</tt> all modules can add their own components in
 * different menus, toolbars, etc. within the ui. Each <tt>UIService</tt>
 * implementation should export its supported "plugable" containers - a set of
 * <tt>Container</tt>s corresponding to different "places" in the application,
 * where a module can add a component.
 * <p>
 * The <tt>UIService</tt> provides also methods that would allow to other
 * modules to control the visibility, size and position of the main application
 * window. Some of these methods are: setVisible, minimize, maximize, resize,
 * move, etc.
 * <p>
 * A way to show different types of simple windows is provided to allow other
 * modules to show different simple messages, like warning or error messages. In
 * order to show a simple warning message, a module should invoke the
 * getPopupDialog method and then one of the showXXX methods, which corresponds
 * best to the required dialog.
 * <p>
 * Certain components within the GUI, like "AddContact" window for example,
 * could be also shown from outside the UI bundle. To make one of these
 * component exportable, the <tt>UIService</tt> implementation should attach to
 * it an <tt>WindowID</tt>. A window then could be shown, by invoking
 * <code>getExportedWindow(WindowID)</code> and then <code>show</code>. The
 * <tt>WindowID</tt> above should be obtained from
 * <code>getSupportedExportedWindows</code>.
 * 
 * @author Yana Stamcheva
 */
public interface UIService
{
    /**
     * Returns TRUE if the application is visible and FALSE otherwise. This
     * method is meant to be used by the systray service in order to detect the
     * visibility of the application.
     * 
     * @return <code>true</code> if the application is visible and
     *         <code>false</code> otherwise.
     * 
     * @see #setVisible(boolean)
     */
    public boolean isVisible();

    /**
     * Shows or hides the main application window depending on the value of
     * parameter <code>visible</code>. Meant to be used by the systray when it
     * needs to show or hide the application.
     * 
     * @param visible if <code>true</code>, shows the main application window;
     *            otherwise, hides the main application window.
     * 
     * @see #isVisible()
     */
    public void setVisible(boolean visible);

    /**
     * Returns the current location of the main application window. The returned
     * point is the top left corner of the window.
     * 
     * @return The top left corner coordinates of the main application window.
     */
    public Point getLocation();

    /**
     * Locates the main application window to the new x and y coordinates.
     * 
     * @param x The new x coordinate.
     * @param y The new y coordinate.
     */
    public void setLocation(int x, int y);

    /**
     * Returns the size of the main application window.
     * 
     * @return the size of the main application window.
     */
    public Dimension getSize();

    /**
     * Sets the size of the main application window.
     * 
     * @param width The width of the window.
     * @param height The height of the window.
     */
    public void setSize(int width, int height);

    /**
     * Minimizes the main application window.
     */
    public void minimize();

    /**
     * Maximizes the main application window.
     */
    public void maximize();

    /**
     * Restores the main application window.
     */
    public void restore();

    /**
     * Resizes the main application window with the given width and height.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void resize(int width, int height);

    /**
     * Moves the main application window to the given coordinates.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public void move(int x, int y);

    /**
     * Brings the focus to the main application window.
     */
    public void bringToFront();

    /**
     * Sets the exitOnClose property. When TRUE, the user could exit the
     * application by simply closing the main application window (by clicking
     * the X button or pressing Alt-F4). When set to FALSE the main application
     * window will be only hidden.
     * 
     * @param exitOnClose When TRUE, the user could exit the application by
     *            simply closing the main application window (by clicking the X
     *            button or pressing Alt-F4). When set to FALSE the main
     *            application window will be only hidden.
     */
    public void setExitOnMainWindowClose(boolean exitOnClose);

    /**
     * Returns TRUE if the application could be exited by closing the main
     * application window, otherwise returns FALSE.
     * 
     * @return Returns TRUE if the application could be exited by closing the
     *         main application window, otherwise returns FALSE
     */
    public boolean getExitOnMainWindowClose();

    /**
     * Returns an exported window given by the <tt>WindowID</tt>. This could be
     * for example the "Add contact" window or any other window within the
     * application. The <tt>windowID</tt> should be one of the WINDOW_XXX
     * obtained by the <tt>getSupportedExportedWindows</tt> method.
     * 
     * @param windowID One of the WINDOW_XXX WindowID-s.
     * @throws IllegalArgumentException if the specified <tt>windowID</tt> is
     *             not recognized by the implementation (note that
     *             implementations MUST properly handle all WINDOW_XXX ID-s.
     * @return the window to be shown.
     */
    public ExportedWindow getExportedWindow(WindowID windowID)
        throws IllegalArgumentException;

    /**
     * Returns an exported window given by the <tt>WindowID</tt>. This could be
     * for example the "Add contact" window or any other window within the
     * application. The <tt>windowID</tt> should be one of the WINDOW_XXX
     * obtained by the <tt>getSupportedExportedWindows</tt> method.
     * 
     * @param windowID One of the WINDOW_XXX WindowID-s.
     * @param params The parameters to be passed to the returned exported
     *            window.
     * @throws IllegalArgumentException if the specified <tt>windowID</tt> is
     *             not recognized by the implementation (note that
     *             implementations MUST properly handle all WINDOW_XXX ID-s.
     * @return the window to be shown.
     */
    public ExportedWindow getExportedWindow(WindowID windowID, Object[] params)
        throws IllegalArgumentException;

    /**
     * Returns a configurable popup dialog, that could be used to show either a
     * warning message, error message, information message, etc. or to prompt
     * user for simple one field input or to question the user.
     * 
     * @return a <code>PopupDialog</code>.
     * @see PopupDialog
     */
    public PopupDialog getPopupDialog();

    /**
     * Returns the <tt>Chat</tt> corresponding to the given <tt>Contact</tt>.
     * 
     * @param contact the <tt>Contact</tt> for which the searched chat is about.
     * @return the <tt>Chat</tt> corresponding to the given <tt>Contact</tt>.
     */
    public Chat getChat(Contact contact);

    /**
     * Returns the <tt>Chat</tt> corresponding to the given <tt>ChatRoom</tt>.
     * 
     * @param chatRoom the <tt>ChatRoom</tt> for which the searched chat is
     *            about.
     * @return the <tt>Chat</tt> corresponding to the given <tt>ChatRoom</tt>.
     */
    public Chat getChat(ChatRoom chatRoom);

    /**
     * Returns a list of all open Chats
     *
     * @return A list of all open Chats
     */
    public List<Chat> getChats();

    /**
     * Get the MetaContact corresponding to the chat.
     * The chat must correspond to a one on one conversation. If it is a
     * group chat an exception will be thrown.
     *
     * @param chat  The chat to get the MetaContact from
     * @return      The MetaContact corresponding to the chat.
     */
    public MetaContact getChatContact(Chat chat);

    /**
     * Returns the selected <tt>Chat</tt>.
     * 
     * @return the selected <tt>Chat</tt>.
     */
    public Chat getCurrentChat();

    /**
     * Returns the phone number currently entered in the phone number field.
     * This method is meant to be used by plugins that are interested in
     * operations with the currently entered phone number.
     * 
     * @return the phone number currently entered in the phone number field.
     */
    public String getCurrentPhoneNumber();

    /**
     * Sets the phone number in the phone number field. This method is meant to
     * be used by plugins that are interested in operations with the currently
     * entered phone number.
     * 
     * @param phoneNumber the phone number to enter.
     */
    public void setCurrentPhoneNumber(String phoneNumber);

    /**
     * Returns an <tt>ExportableComponent</tt> that corresponds to an
     * authentication window for the given protocol provider and user
     * information. Initially this method is meant to be used by the
     * <tt>SystrayService</tt> in order to show a login window when user tries
     * to connect using the systray menu.
     * 
     * @param protocolProvider the <tt>ProtocolProviderService</tt> for which
     *            the authentication window is about.
     * @param realm the realm
     * @param userCredentials the <tt>UserCredentials</tt>, where the username
     *            and password details are stored
     * @param isUserNameEditable indicates if the user name could be changed by
     *            user.
     * @return an <tt>ExportableComponent</tt> that corresponds to an
     *         authentication window for the given protocol provider and user
     *         information.
     */
    public ExportedWindow getAuthenticationWindow(
        ProtocolProviderService protocolProvider, String realm,
        UserCredentials userCredentials, boolean isUserNameEditable);

    /**
     * Returns a default implementation of the <tt>SecurityAuthority</tt>
     * interface that can be used by non-UI components that would like to launch
     * the registration process for a protocol provider. Initially this method
     * was meant for use by the systray bundle and the protocol URI handlers.
     * 
     * @param protocolProvider the <tt>ProtocolProviderService</tt> for which
     *            the authentication window is about.
     * 
     * @return a default implementation of the <tt>SecurityAuthority</tt>
     *         interface that can be used by non-UI components that would like
     *         to launch the registration process for a protocol provider.
     */
    public SecurityAuthority getDefaultSecurityAuthority(
        ProtocolProviderService protocolProvider);

    /**
     * Returns an iterator over a set of windowID-s. Each <tt>WindowID</tt>
     * points to a window in the current UI implementation. Each
     * <tt>WindowID</tt> in the set is one of the constants in the
     * <tt>ExportedWindow</tt> interface. The method is meant to be used by
     * bundles that would like to have access to some windows in the gui - for
     * example the "Add contact" window, the "Settings" window, the
     * "Chat window", etc.
     * 
     * @return Iterator An iterator to a set containing WindowID-s representing
     *         all exported windows supported by the current UI implementation.
     */
    public Iterator<WindowID> getSupportedExportedWindows();

    /**
     * Checks if a window with the given <tt>WindowID</tt> is contained in the
     * current UI implementation.
     * 
     * @param windowID one of the <tt>WindowID</tt>-s, defined in the
     *            <tt>ExportedWindow</tt> interface.
     * @return <code>true</code> if the component with the given
     *         <tt>WindowID</tt> is contained in the current UI implementation,
     *         <code>false</code> otherwise.
     */
    public boolean isExportedWindowSupported(WindowID windowID);

    /**
     * Returns the <tt>WizardContainer</tt> for the current UIService
     * implementation. The <tt>WizardContainer</tt> is meant to be implemented
     * by the UI service implementation in order to allow other modules to add
     * to the GUI <tt>AccountRegistrationWizard</tt> s. Each of these wizards is
     * made for a given protocol and should provide a sequence of user interface
     * forms through which the user could register a new account.
     * 
     * @return Returns the <tt>AccountRegistrationWizardContainer</tt> for the
     *         current UIService implementation.
     */
    public WizardContainer getAccountRegWizardContainer();

    /**
     * Returns an iterator over a set containing containerID-s pointing to
     * containers supported by the current UI implementation. Each containerID
     * in the set is one of the CONTAINER_XXX constants. The method is meant to
     * be used by plugins or bundles that would like to add components to the
     * user interface. Before adding any component they should use this method
     * to obtain all possible places, which could contain external components,
     * like different menus, toolbars, etc.
     * 
     * @return Iterator An iterator to a set containing containerID-s
     *         representing all containers supported by the current UI
     *         implementation.
     */
    public Iterator<Container> getSupportedContainers();

    /**
     * Checks if the container with the given <tt>Container</tt> is supported
     * from the current UI implementation.
     * 
     * @param containderID One of the CONTAINER_XXX Container-s.
     * @return <code>true</code> if the container with the given
     *         <tt>Container</tt> is supported from the current UI
     *         implementation, <code>false</code> otherwise.
     */
    public boolean isContainerSupported(Container containderID);

    /**
     * Determines whether the Mac OS X screen menu bar is being used by the UI
     * for its main menu instead of the Windows-like menu bars at the top of the
     * windows.
     * <p>
     * A common use of the returned indicator is for the purposes of
     * platform-sensitive UI since Mac OS X employs a single screen menu bar,
     * Windows and Linux/GTK+ use per-window menu bars and it is inconsistent on
     * Mac OS X to have the Window-like menu bars.
     * </p>
     * 
     * @return <tt>true</tt> if the Mac OS X screen menu bar is being used by
     *         the UI for its main menu instead of the Windows-like menu bars at
     *         the top of the windows; otherwise, <tt>false</tt>
     */
    public boolean useMacOSXScreenMenuBar();

    /**
     * Shows or hides the "Tools &gt; Settings" configuration window.
     * <p>
     * The method hides the implementation-specific details of the configuration
     * window from its clients and allows the UI to completely control, for
     * example, how many instances of it are visible at one and the same time.
     * <p>
     *
     * @param visible <tt>true</tt> to show the "Tools &gt; Settings"
     *            configuration window; <tt>false</tt> to hide it
     */
    public void setConfigurationWindowVisible(boolean visible);

    /**
     * Adds the given <tt>WindowListener</tt> listening for events triggered
     * by the main UIService component. This is normally the main application
     * window component, the one containing the contact list. This listener
     * would also receive events when this window is shown or hidden.
     * @param l the <tt>WindowListener</tt> to add
     */
    public void addWindowListener(WindowListener l);

    /**
     * Removes the given <tt>WindowListener</tt> from the list of registered
     * listener. The <tt>WindowListener</tt> is listening for events
     * triggered by the main UIService component. This is normally the main
     * application window component, the one containing the contact list. This
     * listener would also receive events when this window is shown or hidden.
     * @param l the <tt>WindowListener</tt> to remove
     */
    public void removeWindowListener(WindowListener l);
}