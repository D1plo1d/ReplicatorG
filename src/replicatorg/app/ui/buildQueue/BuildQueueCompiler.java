package replicatorg.app.ui.buildQueue;

import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;

import replicatorg.app.Base;
import replicatorg.app.MachineFactory;
import replicatorg.app.ui.MainWindow;
import replicatorg.app.ui.modeling.EditingModel;
import replicatorg.drivers.Driver;
import replicatorg.model.Build;
import replicatorg.model.BuildModel;
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
	Driver driver;
	
	public BuildQueueCompiler(Driver driver)
	{
		this.driver = driver;
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
				// loading the material
				//TODO: this is a test setup, eventually this will load a specific material.
				HashMap<String, String> material = new HashMap<String, String>();
				//Injecting machine-specific settings into the material
				material = driver.getMaterialProfile(material);

				generator.autoConfigure(material);

				//setting the stl model for gcode generation
				Build build = new Build(mainWindow, buildItem.gcode.getAbsolutePath());

				BuildModel buildModel = build.getModel();
				
				EditingModel editingModel = new EditingModel(buildModel,mainWindow);

				//centering each file on the build platform
				editingModel.getGroup();
				editingModel.center();
				buildModel.save();

				generator.setModel(build.getModel());
				
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
