package replicatorg.app.ui.util;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import replicatorg.app.Base;

/**
 * Utility class for selecting GCode and STL files using a file dialog.
 * @author rob
 */
public class FileBrowser {

	private final FileDialog dialog;
	//hacking in the SWT for native file browsing
	private static Display display = new Display();
	private static Shell shell = new Shell(display);
	
	/**
	 * 
	 * @param multi if true allows the selection of multiple files.
	 */
	public FileBrowser(Boolean multi)
	{
		/* The file filter for adding only printable file types to the build queue */
		dialog = new FileDialog(shell, multi?SWT.MULTI:SWT.SINGLE);
		String[] extensions = {"*.stl", "*.gcode", "*.ngc", "*.*"};
		dialog.setFilterExtensions( extensions );
	}

	/**
	 * Returns the files selected in the file browser. If no files were selected returns null.
	 */
	public File[] open()
	{
		/* Open the previous opened directory by default. */
		String loadDir = Base.preferences.get("ui.open_dir", null);
		if (loadDir != null) {
			dialog.setFilterPath( new File(loadDir).getAbsolutePath() );
		}

		// open the file dialog and wait for a response, verify the user didn't hit cancel
		if (dialog.open() == null) return null;

		// Getting the containing folder path
		String path = dialog.getFilterPath();
		char lastPathChar = path.charAt(path.length()-1);
		if (lastPathChar != File.separatorChar) path = path + File.separatorChar;

    	Base.preferences.put("ui.open_dir",dialog.getFilterPath());
		
		String[] filenames = dialog.getFileNames();
		File[] files = new File[filenames.length];

		// Generating absolute file paths
		for (int i = 0; i< filenames.length; i++)
		{
			files[i] = new File(path + filenames[i]);
		}
		
		return files;
	}
}
