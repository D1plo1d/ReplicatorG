/**
 * 
 */
package replicatorg.app.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileFilter;

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
	private JPanel buildItems = new JPanel(new GridLayout(0,1));

	private JButton addItemButton = new JButton("add build item");

	final JFileChooser addItemFileChooser = new JFileChooser();

	public final Iterator<BufferedReader> iterator = new Iterator<BufferedReader>()
	{
		private BuildItem currentItem;

		@Override
		public boolean hasNext() {
			return getNextItem()!=null;
		}
		
		/* Private method to locate the next build item */
		private BuildItem getNextItem() {
			Component[] items = buildItems.getComponents();

			//if the current item is null set it to the first item
			if (items.length == 0) return null;
			if (currentItem == null) return (BuildItem) items[0];

			for (int i = 0; i < items.length; i++)
			{
				if (items[i] == currentItem)
				{
					if (i+1 < items.length)
					{
						return (BuildItem)items[i++];
					}
					else
					{
						return null;
					}
				}
			}
			
			//if the current item is not in the list set it to the first item
			 currentItem = null;

			return (BuildItem) items[0];
		}

		@Override
		public BufferedReader next() {
			//TODO: get this working for compiling STLs as well as gcode
			currentItem = getNextItem();
			buildItems.remove(currentItem);
			System.out.println("opening: "+currentItem.name);
			return currentItem.bufferedReader;
		}

		@Override
		public void remove() {
			buildItems.remove(currentItem);
		}
		
	};

	private JButton buildQueueButton;
	
	/**
	 * 
	 */
	public BuildQueuePanel(JButton buildQueueButton) {
		super(true);
		
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
		
		addItemButton.addActionListener(this);
		footerButtons.add(addItemButton);
		
		/* The file filter for adding only printable file types to the build queue */
		addItemFileChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "GCode or STL files";
			}
			
			@Override
			public boolean accept(File file) {
				String name = file.getName();
				return name.endsWith(".gcode")
					||name.endsWith(".ngc")
					||name.endsWith(".stl")
					||file.isDirectory();
			}
		});

		this.repaint();
	}
	
	public void addItem(String fileName) throws FileNotFoundException
	{
		addItem(new File(fileName));
	}
	
	public void addItem(File file) throws FileNotFoundException
	{
		buildItems.add(new BuildItem(file));
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
	
	private class BuildItem extends JPanel implements ActionListener
	{
		public final BufferedReader bufferedReader;
		public final String name;
		public BuildItem(File file) throws FileNotFoundException
		{
			super(true);
			this.setLayout(new BorderLayout());
			
			bufferedReader = new BufferedReader(new FileReader(file));

			JLabel label = new JLabel(name = file.getName());
			JButton remove = new JButton("remove");
			remove.addActionListener(this);
			
			this.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
			
			this.add(label, BorderLayout.WEST);
			this.add(remove, BorderLayout.EAST);

			//this.setPreferredSize(new Dimension(400, 50));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			BuildQueuePanel.this.removeItem(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//In response to a button click open the file dialog
		int returnVal = addItemFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
				this.addItem(addItemFileChooser.getSelectedFile());
			} catch (FileNotFoundException ex) {
				System.out.println("file not found exception while trying to queue "+addItemFileChooser.getSelectedFile().getName());
			}
			this.validate();
            this.repaint();
        }
	}

}
