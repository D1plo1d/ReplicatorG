/**
 * 
 */
package replicatorg.app.ui.buildQueue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import replicatorg.plugin.toolpath.ToolpathGenerator.GeneratorListener;

/**
 * @author rob
 *
 */
public class BuildQueueCompilerDialog extends JDialog implements GeneratorListener {
	private final JProgressBar queueProgress;
	private final JLabel statusText = new JLabel("Starting up..");
	private final int size;

	public BuildQueueCompilerDialog(JFrame parent, int size)
	{
		super(parent);
		this.size = size;
		queueProgress = new JProgressBar(0, size);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				init();
			}
		});
	}
	
	private void init()
	{
		this.setTitle("Compiling..");
				
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add( new JLabel("Compiling Queue to GCode.."), BorderLayout.NORTH );
		center.add( queueProgress, BorderLayout.CENTER );
		center.add( statusText, BorderLayout.SOUTH );
		center.setVisible(true);
		
		queueProgress.setStringPainted(true);
		queueProgress.setString("0/"+size);
		queueProgress.setPreferredSize(new Dimension(300, 50));

		this.setContentPane(center);

		this.setLocationRelativeTo(null); // center on screen
		this.toFront(); // raise above other java windows

		this.pack();
		this.setVisible(true);
	}

	@Override
	public void updateGenerator(String message) {
		statusText.setText(message);
	}
	@Override
	public void generationComplete(Completion completion, Object details) {
		queueProgress.setValue(queueProgress.getValue()+1);
		queueProgress.setString(queueProgress.getValue()+"/"+size);
		if (queueProgress.getValue() == size) this.dispose();
	}
}
