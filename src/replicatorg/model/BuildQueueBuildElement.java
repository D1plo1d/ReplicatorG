package replicatorg.model;

import java.io.OutputStream;

import replicatorg.app.ui.BuildQueuePanel;

public class BuildQueueBuildElement extends BuildElement {
	
	public final BuildQueuePanel panel;

	public BuildQueueBuildElement(BuildQueuePanel panel)
	{
		this.panel = panel;
	}
	
	@Override
	public Type getType() {
		return Type.QUEUE;
	}

	@Override
	void writeToStream(OutputStream ostream) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
