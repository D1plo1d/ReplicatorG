package replicatorg.app.ui.buildQueue;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class BuildItem extends JPanel implements ActionListener
{
	public final File file;
	public final String name;
	private final BuildQueuePanel buildQueue;
	public final AtomicInteger quantity = new AtomicInteger(1);
	public final File gcode;
	
	public BuildItem(File file, BuildQueuePanel buildQueue) throws FileNotFoundException
	{
		super(true);
		// fail fast if the file loaded doesn't exist
		if (!file.isFile()) throw new FileNotFoundException(this.file.getPath());

		this.file = file;
		String filename = this.file.getPath();
		if (filename.endsWith(".gcode")||filename.endsWith(".ngc"))
		{
			this.gcode = this.file;
		}
		else
		{
			this.gcode = new File( filename.substring(0, filename.lastIndexOf('.'))+".gcode" );
		}
		
		this.buildQueue = buildQueue;
		this.setLayout(new BorderLayout());
		
		JLabel label = new JLabel(name = file.getName());
		JButton remove = new JButton("remove");

		// Quantity
		final JTextField quantityValue = new JTextField(""+quantity.get());
		quantityValue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try
				{
					quantity.set( Integer.parseInt( quantityValue.getText().trim() ) );
				}
				catch(NumberFormatException e){/*ignore non-number values*/};
			}
		});
		
		JPanel leftPanel = new JPanel();
		leftPanel.add(label);
		
		JPanel rightPanel = new JPanel();
		rightPanel.add(new JLabel("Quantity: "));
		rightPanel.add(quantityValue);
		rightPanel.add(remove);
		
		
		remove.addActionListener(this);
		
		this.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
		
		this.add(leftPanel, BorderLayout.WEST);
		this.add(rightPanel, BorderLayout.EAST);

		//this.setPreferredSize(new Dimension(400, 50));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.buildQueue.removeItem(this);
	}
	
	public BufferedReader getGCodeBufferedReader() throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(gcode));
	}
}

