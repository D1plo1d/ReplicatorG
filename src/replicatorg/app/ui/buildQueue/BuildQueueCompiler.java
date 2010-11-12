package replicatorg.app.ui.buildQueue;

import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;

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
	
	public void compile(final Component[] buildItems, final MainWindow mainWindow)
	{
		int stlCount = 0;
		for (Component component : buildItems)
		{
			BuildItem buildItem = (BuildItem)component;
			if (buildItem.gcode.exists() != true) stlCount ++;
		}

		if (stlCount == 0) return;

		final BuildQueueCompilerDialog compilerDialog = 
			new BuildQueueCompilerDialog (mainWindow, stlCount);

		for (Component component : buildItems)
		{
			BuildItem buildItem = (BuildItem)component;
			if (buildItem.gcode.exists() == true) continue;
			
			ToolpathGenerator generator = ToolpathGeneratorFactory.createSelectedGenerator();
			try {
				Build build = new Build(mainWindow, buildItem.gcode.getAbsolutePath());
				generator.setModel(build.getModel());

				// loading the material
				//TODO: this is a test setup, eventually this will load from xml.
				HashMap<String, String> material = new HashMap<String, String>();
				material.put("profile", "SF31-cupcake-automated-platform-ABS");
				material.put("useRaft", "false");
				generator.autoConfigure(material);
				
				// starting the toolpath generator thread
				ToolpathGeneratorThread thread = new ToolpathGeneratorThread(mainWindow, generator, build);
				
				// setting up the notifier and listener to tell us when the generator is done
				final Object notifier = new Object();
				thread.addListener(compilerDialog);
				thread.addListener(new GeneratorListener() {

					@Override
					public void updateGenerator(String message) {
						Base.logger.info(message);
					}

					@Override
					public void generationComplete(Completion completion,
							Object details) {
						synchronized(notifier)
						{
							notifier.notifyAll();
						}
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
