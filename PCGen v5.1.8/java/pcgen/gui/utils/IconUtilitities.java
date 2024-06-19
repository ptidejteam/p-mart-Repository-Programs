package pcgen.gui.utils;

import java.awt.Frame;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: jonask
 * Date: Apr 1, 2003
 * Time: 5:10:44 PM
 * To change this template use Options | File Templates.
 */
public class IconUtilitities
{
	public static final String RESOURCE_URL = "/pcgen/gui/resource/";

	/**
	 * Fetch an <code>ImageIcon</code> relative to the calling
	 * location.
	 *
	 * @param iconName <code>String</code>, the path to the
	 * <code>IconImage> source
	 *
	 * @return <code>ImageIcon</code>, the icon or
	 * <code>null</code> on failure
	 */
	public static ImageIcon getImageIcon(String iconName)
	{
		final URL iconURL = IconUtilitities.class.getResource(RESOURCE_URL + iconName);
		if (iconURL == null)
		{
			return null;
		}
		return new ImageIcon(iconURL);
	}

	/**
	 * Fetch an <code>ImageIcon</code> relative to the calling
	 * location.
	 *
	 * @param location <code>String</code>, the path to the
	 * <code>IconImage> source
	 * @param description <code>String</code>, the description
	 *
	 * @return <code>ImageIcon</code>, the icon or
	 * <code>null</code> on failure
	 */
	public static ImageIcon getImageIcon(String location, String description)
	{
		final URL iconURL = IconUtilitities.class.getResource(RESOURCE_URL + location);
		if (iconURL == null)
		{
			return null;
		}
		return new ImageIcon(iconURL, description);
	}

	/**
	 * Add an icon to a menu item if the image can be loaded,
	 * otherwise return <code>false</code>.
	 *
	 * @param button AbstractButton the item
	 * @param iconName String the name of the image file (not the path)
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(AbstractButton button, String iconName)
	{
		if (iconName == null)
		{
			return false;
		}
		final ImageIcon iconImage = getImageIcon(iconName);
		if (iconImage == null)
		{
			return false;
		}
		button.setIcon(iconImage);
		return true;
	}

	/**
	 * Add an icon and description to a menu item if the image can
	 * be loaded, otherwise return <code>false</code>.
	 *
	 * @param button AbstractButton the item
	 * @param iconName String the name of the image file (not the path)
	 * @param description String the description of the icon
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(AbstractButton button, String iconName, String description)
	{
		if (iconName == null)
		{
			return false;
		}
		final ImageIcon iconImage = getImageIcon(iconName, description);
		if (iconImage == null)
		{
			return false;
		}
		button.setIcon(iconImage);
		return true;
	}

	/**
	 * Add an icon to a frame if the image can be loaded,
	 * otherwise return <code>false</code>.
	 *
	 * @param frame Frame the frame
	 * @param iconName String the name of the image file (not the path)
	 *
	 * @return boolean was icon set?
	 */
	public static boolean maybeSetIcon(Frame frame, String iconName)
	{
		if (iconName == null)
		{
			return false;
		}
		final ImageIcon iconImage = getImageIcon(iconName);
		if (iconImage == null)
		{
			return false;
		}
		frame.setIconImage(iconImage.getImage());
		return true;
	}
}
