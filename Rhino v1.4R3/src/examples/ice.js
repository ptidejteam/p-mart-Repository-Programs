/*
	ice.js
	
	Uses the ice browser bean from JS. 
	
	by Patrick C. Beard.
 */

/**
 * Creates an ice browser for a specified URL.
 */
function browse(url) {
	// "import" some useful classes from Java.
	var System = java.lang.System;

	var awt = java.awt;
	var Frame = awt.Frame;
	var Panel = awt.Panel;
	var GridLayout = awt.GridLayout;
	var BorderLayout = awt.BorderLayout;
	var List = awt.List;
	var TextArea = awt.TextArea;
	var Button = awt.Button;
	var PopupMenu = awt.PopupMenu;
	var MenuItem = awt.MenuItem;

	var WindowListener = awt.event.WindowListener;
	var ActionListener = awt.event.ActionListener;
	var ItemListener = awt.event.ItemListener;
	var ItemEvent = awt.event.ItemEvent;
	var MouseAdapter = awt.event.MouseAdapter;
	var MouseEvent = awt.event.MouseEvent;
	
	var beans = java.beans;
	var Beans = beans.Beans;
	
	var ice = Packages.ice;

	// create a top-level window with a browser bean in it.
	var frame = new Frame("browser: " + url);
	frame.setLayout(new BorderLayout());

	// put the browser bean in the center.
	// var browserBean = Beans.instantiate(null, "ice.htmlbrowser.Browser");
	var browserBean = new ice.htmlbrowser.Browser();
	frame.add("Center", browserBean);
	
	// add a popup menu to the frame, so that we can perform simple navigation.
	var historyPopup = new PopupMenu();
	var historyListener = new ActionListener() {
		actionPerformed : function(actionEvent) {
			var command = actionEvent.getActionCommand();
			browserBean[command]();
		}
	};
	function createHistoryItem(itemName, itemAction) {
		var item = new MenuItem(itemName);
		item.setActionCommand(itemAction);
		item.addActionListener(historyListener);
		historyPopup.add(item);
	}
	createHistoryItem("Back", "goBack");
	createHistoryItem("Forward", "goForward");
	createHistoryItem("Reload", "reload");
	frame.add(historyPopup);
	
	// attach a mouse listener to the browser to detect popup menu clicks.
	var popupListener = new MouseAdapter() {
		mousePressed : function(e) {
			if (e.isPopupTrigger()) {
				e.consume();
				this.checkHistory();
				historyPopup.show(browserBean, e.getX(), e.getY());
			}
		},
		// when "currentLocation" changes, we inspect the history
		// stack, so the popup menu item states are correct.
		backItem : historyPopup.getItem(0),
		forwardItem : historyPopup.getItem(1),
		checkHistory : function() {
			var backHistory = browserBean.getBackHistory();
			this.backItem.setEnabled(backHistory.size() > 0);
			var forwardHistory = browserBean.getForwardHistory();
			this.forwardItem.setEnabled(forwardHistory.size() > 0);
		}
	};
	browserBean.addMouseListener(popupListener);

	// attach a property change listener, so things like window title will
	// be automatically updated.
	var propertyListener = new beans.PropertyChangeListener() {
		propertyChange : function(/* PropertyChangeEvent */ e) {
			var propertyName = e.getPropertyName();
			var propertyListener = this[propertyName];
			if (propertyListener != undefined)
				propertyListener(e.getNewValue());
		},
		documentTitle : function(newTitle) {
			frame.setTitle("browser: " + newTitle);
		}
	};
	browserBean.addPropertyChangeListener(propertyListener);

	// now, direct the browser to go to the specified URL.
	browserBean.setCurrentLocation(url);
	
	// attach a window listener to the frame to automatically close it.
	var windowListener = new WindowListener() {
		windowClosing: function(event) {
			frame.dispose();
			browserBean.dispose();
		}
	};
	frame.addWindowListener(windowListener);

	frame.pack();
	frame.show();

	return frame;
}

if (arguments != undefined) {
	browse(arguments[0]);
}
