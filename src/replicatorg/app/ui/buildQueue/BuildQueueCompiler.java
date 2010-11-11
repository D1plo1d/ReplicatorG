package replicatorg.app.ui.buildQueue;

import java.awt.Component;
import java.io.IOException;

import replicatorg.app.Base;
import replicatorg.app.ui.MainWindow;
import replicatorg.model.Build;
import replicatorg.plugin.toolpath.ToolpathGenerator;
import replicatorg.plugin.toolpath.ToolpathGenerator.GeneratorListener;
import replicatorg.plugin.toolpath.ToolpathGeneratorFactory;
import replicatorg.plugin.toolpath.ToolpathGeneratorThread;

/**
 * Compiles all STL files in a build queue and allows user configuration of each 
 * (ok, that second part isn't here yet, but that's the idea).
 * @author rob
 *
 */
public class BuildQueueCompiler {
	
	public BuildQueueCompiler()
	{
	}
	
	public void compile(Component[] buildItems, MainWindow mainWindow)
	{
		
		for (Component component : buildItems)
		{
			BuildItem buildItem = (BuildItem)component;
			if (buildItem.gcode.exists() == true) continue;
			
			ToolpathGenerator generator = ToolpathGeneratorFactory.createSelectedGenerator();
			try {
				// starting the toolpath generator thread
				Build build = new Build(mainWindow, buildItem.gcode.getAbsolutePath());
				ToolpathGeneratorThread thread = new ToolpathGeneratorThread(mainWindow, generator, build);
				
				// setting up the notifier and listener to tell us when the generator is done
				final Object notifier = new Object();
				thread.addListener(new GeneratorListener() {

					@Override
					public void updateGenerator(String message) {
						Base.logger.info(message);
					}

					@Override
					public void generationComplete(Completion completion,
							Object details) {
						notifier.notifyAll();
					}
				});

				// waiting for the toolpath generator to finish generating gcode
				thread.start();
				synchronized(notifier)
				{
					try {
						notifier.wait();
					} catch (InterruptedException e) {/*this is expected*/}
				}
				
			} catch (IOException e) {
				Base.logger.severe("IO Exception while compiling the build queue.\n"+e.getMessage());
			}
		}

	}
}
