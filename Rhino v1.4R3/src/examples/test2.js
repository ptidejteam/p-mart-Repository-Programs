/*
	test2.js
	
	Demonstrates use of JavaScript from JShell, using the AWT to
	create windows and buttons, and binding JavaScript objects to them
	using the JavaAdapter.
	
	by Patrick C. Beard.
 */

// "import" some useful classes from Java.
var awt = java.awt;
var Frame = awt.Frame;
var Button = awt.Button;
var System = java.lang.System;

var ActionListener = awt.event.ActionListener;
var WindowListener = awt.event.WindowListener;

// create a window with a button in it.
var gFrame = new Frame("Demo from JavaScript");

// attach a window listener to the frame.
var windowListener = new WindowListener() {
	window: gFrame,
	windowClosing: function(event) {
		this.window.dispose();
	}
};
gFrame.addWindowListener(windowListener);

var gButton = new Button("OK");
// now, attach some JavaScript behavior to the button.
var buttonListener = new ActionListener() {
	actionPerformed: function(event) {
		System.out.println("actionPerformed here.");
		System.out.flush();
	}
}
gButton.addActionListener(buttonListener);

gFrame.add(gButton);
gFrame.show();
