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
import java.io.FilenameFilter;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.util.PropertyFactory;

/**
 * <code>PortraitChooser</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

final class PortraitChooser extends JPanel
{
	private static final String default_portrait_name = Constants.s_NONE;

	/** a filename filter for jpg, gif (and for java 1.4+ png) files */
	private static final FilenameFilter IMAGE_FILTER = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			String s = name.toLowerCase();
			boolean rc = s.endsWith(".gif") || s.endsWith(".jpg") || s.endsWith(".jpeg");
			if (Globals.javaVersionMajor >= 1 && Globals.javaVersionMinor >= 4)
			{
				rc |= s.endsWith(".png");
			}
			return rc;
		}
	};

	private static final JFileChooser fileChooser;

	private static Image default_portrait = null;

	private File directory;
	private String title;

	private GridBagConstraints gbc;
	private GridBagLayout gbl;

	private JButton dirButton;
	private JButton refreshButton;
	private JComboBox portraitBox;
	private JLabel portraitLabel;
	private PortraitModel portraitModel;

	/** store for all image filenames */
	private String[] portraits;

	/** cache for already appropriately scaled images */
	private ImageIcon[] cache;


	/**
	 * <br>author: Thomas Behr 18-04-02
	 */
	static
	{
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				return f.isDirectory();
			}

			public String getDescription()
			{
				return "Directories only";
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
		this.directory = new File(Globals.getDefaultPath());

		if (default_portrait == null)
		{
			default_portrait = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/pcgen/gui/resource/DefaultPortrait.gif"));
		}

		initComponents();

		initPortrait();
	}

	/**
	 *
	 *
	 * <br>author: Thomas Behr 20-04-02
	 */
	public void refresh()
	{
		initPortrait();
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


		// portrait refresh button
		refreshButton = Utility.createButton(null, null, PropertyFactory.getString("in_refreshTipString"), "Refresh16.gif", true);
		refreshButton.setMargin(new Insets(2, 2, 2, 2));
		refreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		this.add(refreshButton, 1, 0, 1, 1);


		// portrait chooser
		populatePortraits();
		portraitBox = new JComboBox(portraitModel = new PortraitModel(portraits));
		portraitBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changePortrait();
			}
		});
		gbc.weightx = 10;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(portraitBox, 1, 1, 1, 1);


		// directory chooser button
		dirButton = new JButton("...");
		if (SettingsHandler.isToolTipTextShown())
		{
			Utility.setDescription(dirButton, PropertyFactory.getString("in_chooseDirectoryTipString"));
		}
		dirButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileChooser.setCurrentDirectory(directory);
				if (fileChooser.showOpenDialog(PortraitChooser.this) == JFileChooser.APPROVE_OPTION)
				{
					directory = fileChooser.getSelectedFile();
					populatePortraits();
					portraitModel.setData(portraits);
					portraitBox.setSelectedIndex(0);
				}
			}
		});
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		this.add(dirButton, 1, 2, 1, 1);
	}

	/**
	 * initialize the file stuff
	 *
	 * <br>author: Thomas Behr 19-04-02
	 */
	private void initPortrait()
	{
		final String portraitPath = Globals.getCurrentPC().getPortraitPath();
		if (portraitPath.length() > 0)
		{
			File portraitFile = new File(portraitPath);
			if (portraitFile.isDirectory())
			{
				directory = portraitFile;

				populatePortraits();
				portraitModel.setData(portraits);

				File parentFile = directory.getParentFile();
				if ((parentFile != null) && parentFile.exists())
				{
					fileChooser.setCurrentDirectory(parentFile);
				}
			}
			else
			{
				File parentFile = portraitFile.getParentFile();
				if ((parentFile != null) && parentFile.exists())
				{
					directory = parentFile;

					populatePortraits();
					portraitModel.setData(portraits);

					if (portraitFile.exists())
					{
						portraitBox.setSelectedItem(portraitModel.extractName(portraitPath));
					}

					parentFile = directory.getParentFile();
					if ((parentFile != null) && parentFile.exists())
					{
						fileChooser.setCurrentDirectory(parentFile);
					}
				}
			}
		}
		else
		{
			portraitBox.setSelectedIndex(0);
		}
	}

	/**
	 * populate the combobox with the names of jpg,gif files
	 * in the chosen directory
	 *
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void populatePortraits()
	{
		File[] files = directory.listFiles(IMAGE_FILTER);
		portraits = new String[files.length + 1];
		portraits[0] = default_portrait_name;
		for (int i = 0; i < files.length; i++)
		{
			portraits[i + 1] = files[i].getAbsolutePath();
		}

		cache = new ImageIcon[portraits.length];
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
		int index = portraitBox.getSelectedIndex();

		cache = new ImageIcon[cache.length];

		if (index == 0)
		{
			image = default_portrait;
		}
		else
		{
			image = Toolkit.getDefaultToolkit().getImage(portraits[index]);
		}

		cache[index] = createScaledImageIcon(image);
		portraitLabel.setIcon(cache[index]);
	}

	/**
	 * change the displayed portrait;
	 * automatically resize the image to fit the
	 * current display size
	 *
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void changePortrait()
	{
		Image image;
		int index = portraitBox.getSelectedIndex();

		if (cache[index] == null)
		{
			if (index == 0)
			{
				image = default_portrait;
			}
			else
			{
				image = Toolkit.getDefaultToolkit().getImage(portraits[index]);
			}

			cache[index] = createScaledImageIcon(image);
		}

		portraitLabel.setIcon(cache[index]);

		final String newPortraitPath = (index > 0) ? portraits[index] : "";
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
		;

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

	/**
	 * ComboBox model for arrays of filenames
	 *
	 * <br>author: Thomas Behr 18-04-02
	 */
	private class PortraitModel extends AbstractListModel implements ComboBoxModel
	{
		private Object selectedObject;
		private String[] data;

		/**
		 * Constructor
		 */
		public PortraitModel(String[] newData)
		{
			setData(newData);
			selectedObject = data[0];
		}

		/**
		 * set the displayed data;
		 * this method takes an array of absolute paths
		 * and extracts the actual filenames as data
		 *
		 * <br>author: Thomas Behr 18-04-02
		 *
		 * @param newData   an array of absolute paths
		 */
		public void setData(String[] newData)
		{
			data = new String[newData.length];
			/*
			 * data[0] should always equal Globals.s_NONE,
			 * no need to extract a name for that.
			 *
			 * author: Thomas Behr 19-04-02
			 */
			data[0] = newData[0];
			for (int i = 1; i < data.length; i++)
			{
				data[i] = extractName(newData[i]);
			}
			fireContentsChanged(this, -1, -1);
		}

		/**
		 * extract the filename (without file extension) from
		 * a specified absolute path
		 *
		 * <br>author: Thomas Behr 18-04-02
		 *
		 * @param s   the absolute path from which the filenname (without
		 *            file extension) should be extracted
		 *
		 * @return the filename (without file extension)
		 */
		public String extractName(String s)
		{
			return s.substring(s.lastIndexOf(File.separator) + 1, s.lastIndexOf("."));
		}

		// implements javax.swing.ComboBoxModel
		public void setSelectedItem(Object anObject)
		{
			if ((selectedObject != null && !selectedObject.equals(anObject)) || (selectedObject == null && anObject != null))
			{
				selectedObject = anObject;
				fireContentsChanged(this, -1, -1);
			}
		}

		// implements javax.swing.ComboBoxModel
		public Object getSelectedItem()
		{
			return selectedObject;
		}

		// implements javax.swing.ListModel
		public int getSize()
		{
			return data.length;
		}

		// implements javax.swing.ListModel
		public Object getElementAt(int index)
		{
			if ((index > -1) && (index < getSize()))
			{
				return data[index];
			}
			else
			{
				return null;
			}
		}
	}
}
