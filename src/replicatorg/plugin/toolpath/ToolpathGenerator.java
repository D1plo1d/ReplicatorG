package replicatorg.plugin.toolpath;

import java.awt.Frame;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import replicatorg.app.Base;
import replicatorg.model.BuildCode;
import replicatorg.model.BuildModel;

/**
 * This is the abstract base class which describes a toolpath plugin.
 * @author phooky
 *
 */
public abstract class ToolpathGenerator {
	public interface GeneratorListener {
		public enum Completion {
			SUCCESS,
			FAILURE
		};
		public void updateGenerator(String message);
		public void generationComplete(Completion completion, Object details);
	}
	
	protected BuildModel model;
	protected LinkedList<GeneratorListener> listeners = new LinkedList<GeneratorListener>();
	
	private AtomicBoolean configured = new AtomicBoolean(false);
	HashMap<String, String> material;
	
	public void addListener(GeneratorListener listener) {
		listeners.add(listener);
	}
	
	public void setModel(BuildModel model) {
		this.model = model;
	}
	
	/**
	 * configures the model to automatically use the specified material.
	 * 
	 * a material will have properties such as density, color and material (ie it's chemistry)
	 * as well as the required info to build these abstract properties to material-specific 
	 * toolpath generator settings.
	 * 
	 * @param material
	 * @return true if successful.
	 */
	public synchronized boolean autoConfigure(HashMap<String, String> material)
	{
		configured.set(true);
		this.material = material;
		return true;
	}

	/**
	 * Configures the model manually using user input.
	 * 
	 * Returns true if configuration successful; false if aborted.
	 */
	public synchronized boolean visualConfigure(Frame parent) {
		assert parent != null;
		assert model != null;
		configured.set(true);
		return true;
	}
	
	public boolean isConfigured()
	{
		return configured.get();
	}
	
	public abstract BuildCode generateToolpath();
	
	public void emitUpdate(String message) {
		for (GeneratorListener listener : listeners) {
			listener.updateGenerator(message);
		}
	}
	
	public void emitCompletion(GeneratorListener.Completion completion, Object details) {
		for (GeneratorListener listener : listeners) {
			listener.generationComplete(completion, details);
		}
	}
}
