package gombos.webbrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/*
 * Created on Feb 27, 2006
 * 
 * Author: Andrew Gombos
 *
 * A simple dialog to allow input of URLs
 */

public class OpenDialog extends Dialog implements SelectionListener {

	public OpenDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Display the dialog
	 * 
	 * @return The URL string, or null if canceled
	 */
	public String open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		FormLayout layout = new FormLayout();
		shell.setLayout(layout);

		Label prompt = new Label(shell, SWT.CENTER);
		prompt.setText(
			"Enter a URL in the space below (http:// prefix included)");
		FormData promptLayout = new FormData();
		promptLayout.left = new FormAttachment(25);
		promptLayout.right = new FormAttachment(75);
		prompt.setLayoutData(promptLayout);

		urlEntry = new Text(shell, SWT.SINGLE | SWT.BORDER);
		urlEntry.setText("");

		FormData urlEntryLayout = new FormData();
		urlEntryLayout.top = new FormAttachment(prompt, 7);
		urlEntryLayout.right = new FormAttachment(100);
		urlEntryLayout.left = new FormAttachment(0);
		urlEntry.setLayoutData(urlEntryLayout);

		ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		FormData okLayout = new FormData();
		okLayout.top = new FormAttachment(urlEntry, 7);
		okLayout.left = new FormAttachment(40);
		ok.setLayoutData(okLayout);
		ok.addSelectionListener(this);

		cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		FormData cancelLayout = new FormData();
		cancelLayout.top = new FormAttachment(urlEntry, 7);
		cancelLayout.left = new FormAttachment(ok, 5);
		cancel.setLayoutData(cancelLayout);
		cancel.addSelectionListener(this);

		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return result;
	}

	/**
	 *  Monitor OK or cancel press to set return result
	 */
	public void widgetSelected(SelectionEvent e) {
		if (e.widget.equals(ok)) {
			result = urlEntry.getText();
		}
		else
			result = null;

		shell.dispose();
	}

	//Not implemented
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private String result;
	private Text urlEntry;
	private Shell shell;
	private Button ok, cancel;

}
