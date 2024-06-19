package gombos.webbrowser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;

/*
 * Created on Feb 25, 2006
 * 
 * Author: Andrew Gombos
 *
 * Control the state of the browser - where forward and back go, which page is currently rendered, 
 * and how to navigate to artibtrary pages.  Represents the behavior/controller portion of the spec
 */

public class StateManager {
	private StateManager() {
		back = new Stack();
		forward = new Stack();
	}

	/**
	 * Singleton pattern 
	 */
	public static StateManager getInstance() {
		if (instance == null)
			instance = new StateManager();

		return instance;
	}

	/**
	 * Store buttons so we can make them (dis|en)abled
	 */
	public void setButtons(ToolItem backButton, ToolItem forwardButton) {
		this.backButton = backButton;
		this.forwardButton = forwardButton;
	}

	/**
	 * Store url entry bar so we can set its text
	 * @param urlBar
	 */
	public void setURLBar(Text urlBar) {
		this.urlBar = urlBar;
	}

	/**
	 * Set the renderer so we can control it      
	 */
	public void setHTMLRenderer(HTMLRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Perform the back action
	 */
	public void back() {
		if (currentPage != null)
			forward.push(currentPage);

		if (back.size() > 0)
			currentPage = (URL) back.pop();

		updateUIState();

		renderer.render(currentPage);
	}

	/**
	 * Perform the forward action
	 */
	public void forward() {
		if (currentPage != null)
			back.push(currentPage);

		if (forward.size() > 0)
			currentPage = (URL) forward.pop();

		updateUIState();

		renderer.render(currentPage);
	}

	/**
	 * Navigate to the home page (samples index page)     
	 */
	public void home() {
		if (currentPage != null)
			back.push(currentPage);

		try {
			currentPage =
				new URL("http://www.cs.uky.edu/~dekhtyar/316-Spring2006/samples/index.html");
		}
		catch (MalformedURLException e) {
		} //Never happens

		if (forward.size() != 0 && !forward.pop().equals(currentPage)) {
			forward.clear();
		}

		updateUIState();

		renderer.render(currentPage);
	}

	/**
	 * Reload the current page
	 */
	public void reload() {
		if (currentPage != null)
			renderer.render(currentPage);
	}

	/**
	 * Open an arbitrary url (open dialog or URL bar or hyperlink)
	 * 
	 *  All have the same behavior
	 */
	public void open(URL url) {
		if (currentPage != null) {
			back.push(currentPage);
		}

		if (forward.size() != 0 && !forward.pop().equals(currentPage)) {
			forward.clear();
		}

		currentPage = url;

		updateUIState();

		renderer.render(url);
	}

	/**
	 *  If a URL is invalid (some sort of error message was displayed instead of the link)
	 *  there is some cleaning up to do
	 *  
	 *  Make sure that our state is correct so back, forward work
	 */
	public void invalidURL() {
		urlBar.setText(currentPage.toString());

		currentPage = null;

		updateUIState();
	}

	/**
	 * Update the enabledness of the back and forward buttons, as well as the URL bar's current text
	 */
	private void updateUIState() {
		if (back.size() == 0)
			backButton.setEnabled(false);
		else
			backButton.setEnabled(true);

		if (forward.size() == 0)
			forwardButton.setEnabled(false);
		else
			forwardButton.setEnabled(true);

		if (currentPage != null)
			urlBar.setText(currentPage.toString());
	}

	private URL currentPage = null;

	private HTMLRenderer renderer;
	private ToolItem backButton, forwardButton;
	private Text urlBar;

	private static StateManager instance;
	private Stack back, forward;

	public URL getCurrentURL() {
		return currentPage;
	}

	/**
	 * Close the current page
	 * 
	 * Does NOT clear the history
	 */
	public void close() {
		if (currentPage != null)
			back.push(currentPage);

		currentPage = null;

		updateUIState();
	}
}
