/**
 * 
 */
package replicatorg.app.ui.buildQueue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replicatorg.app.ui.MainWindow;
import replicatorg.app.ui.util.FileBrowser;
import replicatorg.model.BuildElement;
import replicatorg.model.BuildQueueBuildElement;

/**
 * @author rob
 *
 */
public class BuildQueuePanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5957459557841882468L;

	public final BuildElement buildElement = new BuildQueueBuildElement(this);

	/** True if there are no items in the build queue */
	private boolean empty = true;

	private JLabel placeholder = new JLabel(
			"There are no items in the build queue.\n"+
			" To start using the build queue click the 'add build item' button.");
	
	/** sub-jpanel containing listing the build items */
	protected JPanel buildItems = new JPanel(new GridLayout(0,1));

	private JButton addItemButton = new JButton("add build item");

	private final FileBrowser addItemFileBrowser = new FileBrowser(true);

	public final Iterator<BufferedReader> iterator = new BuildQueueIterator(this);
	
	private final JButton buildQueueButton;

	private final MainWindow mainWindow;
	
	/**
	 * 
	 */
	public BuildQueuePanel(JButton buildQueueButton, MainWindow mainWindow) {
		super(true);
		
		this.mainWindow = mainWindow;
		this.buildQueueButton = buildQueueButton;

		this.setLayout(new BorderLayout());
		
		/* Queue */
		this.add(buildItems, BorderLayout.NORTH);

		/* Queue Placeholder */
		this.add(placeholder, BorderLayout.CENTER);
		
		/* Footer */
		JPanel footer = new JPanel(new BorderLayout());
		JPanel footerButtons = new JPanel();
		footer.add(footerButtons, BorderLayout.EAST);
		this.add(footer, BorderLayout.SOUTH);
		
		/* The add button for loading more objects to the queue */
		addItemButton.addActionListener(this);
		footerButtons.add(addItemButton);
		
		this.repaint();
	}

	public void addItems(String[] files) throws FileNotFoundException
	{
		for (String file : files)
		{
			addItem(new File(file));
		}
	}

	public void addItems(File[] files) throws FileNotFoundException
	{
		for (File file : files)
		{
			addItem(file);
		}
	}

	public void addItem(File file) throws FileNotFoundException
	{
		buildItems.add(new BuildItem(file, this));
		if (empty == true)
		{
			empty = false;
			placeholder.setVisible(false);
			buildQueueButton.setEnabled(true);
		}
	}
	
	public void removeItem(Component component)
	{
		this.buildItems.remove(component);
		//if the queue is empty display the placeholder message to help new users.
		if (this.buildItems.getComponentCount() == 0)
		{
			empty = true;
			placeholder.setVisible(true);
			buildQueueButton.setEnabled(false);
		}
		// repaint the build queue to the screen
		this.validate();
        this.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//In response to a button click open the file dialog
		
		// open the file dialog and wait for a response
		File[] files = addItemFileBrowser.open();

		// verify the user didn't hit cancel
		if ( files == null) return;

		//Adding the files to the build queue
        try {
			this.addItems(files);
		} catch (FileNotFoundException ex) {
			System.out.println("file not found exception while trying to add to queue \n"+ex.toString());
		}
		this.validate();
        this.repaint();
	}

	public void compile() {
		Component[] items;
		synchronized(this)
		{
			items = buildItems.getComponents();
		}
		BuildQueueCompiler compiler = new BuildQueueCompiler();
		compiler.compile(items, mainWindow);
	}

}
