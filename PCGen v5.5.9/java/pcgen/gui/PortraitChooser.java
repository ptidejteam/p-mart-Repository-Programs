/*
 * PortraitChooser.java
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
 * Created on April 18, 2002, 5:00 PM
 */

package pcgen.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.util.PropertyFactory;

/**
 * <code>PortraitChooser</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public final class PortraitChooser extends JPanel
{
	static final long serialVersionUID = -2286034876554542232L;
	private static final boolean ALLOW_PNG =
		(Globals.javaVersionMajor > 1
			|| (Globals.javaVersionMajor == 1 && Globals.javaVersionMinor >= 4));

	private static final JFileChooser fileChooser;

	private static Image default_portrait = null;

	private File directory;
	private File portraitFile;
	private String title;

	private GridBagConstraints gbc;
	private GridBagLayout gbl;

	private JButton dirButton;
	private JButton refreshButton;
	private JButton removeButton;
	private JLabel portraitLabel;

	/** cache for already appropriately scaled images */
	private ImageIcon cachedIcon;


	/**
	 * <br>author: Thomas Behr 18-04-02
	 */
	static
	{
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				if (f.isDirectory())
				{
					return true;
				}

				String fileName = f.getName().toLowerCase();
				boolean isImage = fileName.endsWith(".gif") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
				if (ALLOW_PNG)
				{
					isImage |= fileName.endsWith(".png");
				}
				return isImage;
			}

			public String getDescription()
			{
				if (ALLOW_PNG)
				{
					return "Images (*.gif, *.jpg, *.jpeg, *.png)";
				}
				else
				{
					return "Images (*.gif, *.jpg, *.jpeg)";
				}
			}
		});
	}

	/**
	 * Constructor
	 */
	public PortraitChooser()
	{
		this("Portrait");
	}

	/**
	 * Constructor
	 */
	public PortraitChooser(String title)
	{
		super();

		this.title = title;
		this.directory = SettingsHandler.getPortraitsPath();

		if (default_portrait == null)
		{
			default_portrait = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(IconUtilitities.RESOURCE_URL + "DefaultPortrait.gif"));
		}

		initComponents();

	}

	/**
	 *
	 *
	 * <br>author: Thomas Behr 20-04-02
	 */
	public void refresh()
	{
		refresh(true);
	}

	private void refresh(boolean resetPortraitsPathToDefaultIfCharacterHasNone)
	{
		initPortrait(resetPortraitsPathToDefaultIfCharacterHasNone);
	}

	/**
	 * initialize the gui stuff
	 *
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void initComponents()
	{
		// basic layout
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();

		this.setLayout(gbl);

		if (title != null)
		{
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		}


		// label for displaying the portraits.
		portraitLabel = new JLabel();
		portraitLabel.setHorizontalAlignment(JLabel.CENTER);
		portraitLabel.setVerticalAlignment(JLabel.CENTER);
		portraitLabel.setVerticalTextPosition(JLabel.CENTER);
		portraitLabel.setHorizontalTextPosition(JLabel.CENTER);
		portraitLabel.setIcon(new ImageIcon(default_portrait));
		portraitLabel.setText("");
		portraitLabel.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				resizePortrait();
			}
		});

		JPanel portraitPanel = new JPanel(new GridLayout(1, 1));
		portraitPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		portraitPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0), portraitPanel.getBorder()));
		portraitPanel.add(portraitLabel);

		gbc.weightx = 10;
		gbc.weighty = 10;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(portraitPanel, 0, 0, 3, 1);


		JPanel buttonPanel= new JPanel();

		// portrait refresh button
		refreshButton = Utility.createButton(null, null, PropertyFactory.getString("in_refreshTipString"), "Refresh16.gif", true);
		refreshButton.setMargin(new Insets(2, 2, 2, 2));
		refreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh(false);
			}
		});
		buttonPanel.add(refreshButton);

		// Portrait chooser button
		dirButton = new JButton(PropertyFactory.getString("in_selectPortrait"));
		if (SettingsHandler.isToolTipTextShown())
		{
			Utility.setDescription(dirButton, PropertyFactory.getString("in_selectPortraitTipString"));
		}
		dirButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileChooser.setCurrentDirectory(directory);
				if (fileChooser.showOpenDialog(PortraitChooser.this) == JFileChooser.APPROVE_OPTION)
				{
					directory = fileChooser.getSelectedFile().getParentFile();
					setPortrait(fileChooser.getSelectedFile());
				}
			}
		});
		buttonPanel.add(dirButton);

		// Portrait Remove button
		removeButton = new JButton(PropertyFactory.getString("in_removePortrait"));
		if (SettingsHandler.isToolTipTextShown())
		{
			Utility.setDescription(removeButton, PropertyFactory.getString("in_removePortraitTipString"));
		}
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setPortrait(null);
			}
		});
		buttonPanel.add(removeButton);

		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		this.add(buttonPanel, 1, 2, 1, 1);

		// Now set the initial portrait
		initPortrait(true);

	}

	/**
	 * initialize the file stuff
	 *
	 * <br>author: Thomas Behr 19-04-02
	 */
	private void initPortrait(boolean resetPortraitsPathToDefaultIfCharacterHasNone)
	{
		final String portraitPath = Globals.getCurrentPC().getPortraitPath();
		if (portraitPath.length() > 0)
		{
			File portraitFile = new File(portraitPath);
			setPortrait(portraitFile);
		}
		else
		{
			if (resetPortraitsPathToDefaultIfCharacterHasNone)
			{
				directory = SettingsHandler.getPortraitsPath();
			}
			setPortrait(null);
		}
	}

	/**
	 * resize the currently chosen image to fit the
	 * current display size;
	 * clear the cache since we probably need to resize
	 * previously cached images as well
	 *
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void resizePortrait()
	{
		Image image;
		if (portraitFile != null)
		{
			image = Toolkit.getDefaultToolkit().getImage(portraitFile.getAbsolutePath());
		}
		else
		{
			image = default_portrait;
		}

		cachedIcon = createScaledImageIcon(image);
		portraitLabel.setIcon(cachedIcon);
	}

	/**
	 * Set the displayed portrait.
	 * automatically resize the image to fit the
	 * current display size
	 *
	 * <br>author: James Dempsey  02 Oct 2003
	 *
	 * @param portraitFile The portrait file to be set.
	 */
	private void setPortrait(File portraitFile)
	{
		Image image;
		String newPortraitPath;

		if (portraitFile == null)
		{
			image = default_portrait;
			newPortraitPath = "";
		}
		else
		{
			newPortraitPath = portraitFile.getAbsolutePath();
			image = Toolkit.getDefaultToolkit().getImage(newPortraitPath);
		}

		this.portraitFile = portraitFile;
		cachedIcon = createScaledImageIcon(image);
		portraitLabel.setIcon(cachedIcon);

		if (!newPortraitPath.equals(Globals.getCurrentPC().getPortraitPath()))
		{
			Globals.getCurrentPC().setPortraitPath(newPortraitPath);
			Globals.getCurrentPC().setDirty(true);
		}
	}

	/**
	 * create an ImageIcon instance with the specified image
	 * scaled to fit the current display size
	 *
	 * <br>author: Thomas Behr 18-04-02
	 *
	 * @param image   the Image instance to scale
	 * @return an ImageIcon instance for an appropriately
	 *         scaled instance of the specified Image
	 */
	private ImageIcon createScaledImageIcon(Image image)
	{
		ImageIcon icon = new ImageIcon(image);

		int width = icon.getIconWidth();
		int height = icon.getIconHeight();

		double factorWidth = (width) / portraitLabel.getSize().getWidth();
		double factorHeight = (height) / portraitLabel.getSize().getHeight();

		if (factorWidth > factorHeight)
		{
			width = (int) ((width) / factorWidth);
			height = -1;
		}
		else
		{
			width = -1;
			height = (int) ((height) / factorHeight);
		}

		if ((width != 0) && (height != 0))
		{
			icon = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
		}

		return icon;
	}

	/**
	 * convenience method
	 *
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void add(JComponent comp, int row, int col, int width, int height)
	{
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = width;
		gbc.gridheight = height;

		gbl.setConstraints(comp, gbc);
		super.add(comp);
	}
}
