package gombos.webbrowser;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/*
 * Created on Feb 20, 2006
 * 
 * Author: Andrew Gombos
 *
 * Main class.  Creates/assimilates the different UI components onto one window, and adds listeners to perform the
 * appropriate actions.  
 * 
 * Also configures the state manager which controls the rendering and page history of the browser 
 */

public class Browser implements SelectionListener {
	/**
	 * Constructor - create and initialize all components 
	 */
	public Browser() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("Simple HTML Browser v 0.7");

		shell.setLayout(layout = new FormLayout());

		createButtonToolbar();
		createURLField();
		createHTMLRenderer();
		createMenuBar();

		createStateManager();

		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginBottom = 10;
		layout.marginTop = 10;

		// Start the event loop
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		// Free our resources
		renderer.cleanup();
		display.dispose();
	}

	public static void main(String[] args) {
		new Browser();
	}

	/**
	 * URL entry field enter pressed
	 * 
	 * Validate and load the inputted URL
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		if (e.widget.equals(url)) {
			try {
				URL newURL = new URL(url.getText());

				stateManager.open(newURL);
			}
			catch (MalformedURLException mue) {
				stateManager.invalidURL();

				renderer.display.setText(
					"Malformed URL in link: " + mue.getMessage());
			}
		}
	}

	/**
	 * Perform actions for toolbar buttons and menu items
	 * 
	 * Usually the action is to call a StateManager control method
	 */
	public void widgetSelected(SelectionEvent e) {
		if (e.widget.equals(back)) {
			stateManager.back();
		}
		if (e.widget.equals(home)) {
			stateManager.home();
		}
		if (e.widget.equals(forward)) {
			stateManager.forward();
		}
		if (e.widget.equals(reload)) {
			stateManager.reload();
		}

		//Menu options
		if (e.widget.equals(openItem)) {
			String urlStr = new OpenDialog(shell, SWT.DIALOG_TRIM).open();

			try {
				URL url = new URL(urlStr);

				stateManager.open(url);
			}
			catch (MalformedURLException mue) {
				stateManager.invalidURL();
				renderer.display.setText(
					"Malformed URL in link: " + mue.getMessage());
			}
		}
		if (e.widget.equals(closeItem)) {
			renderer.removeCurrentDocument();

			stateManager.close();
		}
		if (e.widget.equals(exitItem)) {
			shell.dispose();
		}

		if (e.widget.equals(aboutItem)) {
			MessageBox instBox = new MessageBox(shell, SWT.OK);

			instBox.setText("About Simple HTML Browser");
			instBox.setMessage(
				"Simple HTML Browser v 0.7\n\n"
					+ "Written by: Andrew Gombos <andrew.gombos@uky.edu>\n"
					+ "For CS 316; University of Kentucky\n\n"
					+ "February 27, 2006");
			instBox.open();
		}
	}

	/**
	 * Create the control toolbar and set the images associated with
	 * each button
	 */
	private void createButtonToolbar() {
		toolbar = new ToolBar(shell, SWT.HORIZONTAL | SWT.FLAT);

		ImageLoader loader = new ImageLoader();

		// Image backImg, forwardImg, homeImg, reloadImg;

		back = new ToolItem(toolbar, SWT.PUSH);
		// back.setImage(backImg = new Image(display, loader.load("back.png")[0]));
		back.setImage(new Image(display, loader.load("back.png")[0]));
		back.addSelectionListener(this);
		back.setDisabledImage(
			new Image(display, loader.load("backDisabled.png")[0]));

		forward = new ToolItem(toolbar, SWT.PUSH);
		// forward.setImage(
		//	forwardImg = new Image(display, loader.load("forward.png")[0]));
		forward.setImage(new Image(display, loader.load("forward.png")[0]));
		forward.addSelectionListener(this);
		forward.setDisabledImage(
			new Image(display, loader.load("forwardDisabled.png")[0]));

		home = new ToolItem(toolbar, SWT.PUSH);
		// home.setImage(
		//	homeImg = new Image(display, loader.load("home.png")[0]));
		home.setImage(new Image(display, loader.load("home.png")[0]));
		home.addSelectionListener(this);

		reload = new ToolItem(toolbar, SWT.PUSH);
		// reload.setImage(
		//	reloadImg = new Image(display, loader.load("reload.png")[0]));
		reload.setImage(new Image(display, loader.load("reload.png")[0]));
		reload.addSelectionListener(this);

		back.setEnabled(false);
		forward.setEnabled(false);
	}

	/**
	 * Create the HTML rendering area - actual rendering commands are given by the state manager
	 */
	private void createHTMLRenderer() {
		renderer = new HTMLRenderer(shell);

		FormData layoutData = new FormData();
		layoutData.top = new FormAttachment(toolbar, 10);
		layoutData.right = new FormAttachment(100);
		layoutData.left = new FormAttachment(0);
		layoutData.bottom = new FormAttachment(100);

		renderer.display.setLayoutData(layoutData);
	}

	/**
	 * Create the menu bar
	 * 
	 * File and About options
	 */
	private void createMenuBar() {
		// Create the menubar itself
		Menu menuBar = new Menu(shell, SWT.BAR);

		shell.setMenuBar(menuBar);

		// Add the top level menu items
		MenuItem file = new MenuItem(menuBar, SWT.CASCADE);
		file.setText("File");

		MenuItem help = new MenuItem(menuBar, SWT.CASCADE);
		help.setText("Help");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(fileMenu);

		Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpMenu);

		//File menu
		openItem = new MenuItem(fileMenu, SWT.PUSH);
		openItem.setText("Open");
		openItem.addSelectionListener(this);

		closeItem = new MenuItem(fileMenu, SWT.PUSH);
		closeItem.setText("Close");
		closeItem.addSelectionListener(this);

		//Add a separator line before the exit menu item
		new MenuItem(fileMenu, SWT.SEPARATOR);

		exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("Exit");
		exitItem.addSelectionListener(this);

		//Help menu
		aboutItem = new MenuItem(helpMenu, SWT.PUSH);
		aboutItem.setText("About");
		aboutItem.addSelectionListener(this);
	}

	/**
	 * Initialize the state manager with the components that initiate actions.
	 * 
	 * This is so the state manager can update thier enabledness, or what they display
	 */
	private void createStateManager() {
		stateManager = StateManager.getInstance();

		stateManager.setButtons(back, forward);
		stateManager.setHTMLRenderer(renderer);
		stateManager.setURLBar(url);
	}

	/**
	 * Create the URL text field entry area
	 * Change its font because it's really tall
	 */
	private void createURLField() {
		url = new Text(shell, SWT.SINGLE | SWT.BORDER);
		url.addSelectionListener(this);

		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(toolbar, 5);
		layoutData.right = new FormAttachment(100);
		layoutData.top = new FormAttachment(1);
		layoutData.bottom = new FormAttachment(5);

		FontData urlFontData = url.getFont().getFontData()[0];
		urlFontData.setHeight(14);
		urlFont = new Font(display, urlFontData);
		url.setFont(urlFont);

		url.setLayoutData(layoutData);
	}

	/**
	 * Controls, so they can be accessed in the appropriate methods
	 */
	private MenuItem aboutItem;

	private ToolItem back, forward, home, reload;

	private MenuItem closeItem;

	private Display display;

	private MenuItem exitItem;

	private FormLayout layout;

	private MenuItem openItem;

	private HTMLRenderer renderer;
	private Shell shell;
	private StateManager stateManager;
	private ToolBar toolbar;

	private Text url;

	private Font urlFont;
}
