package replicatorg.app.ui.buildQueue;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Iterator;

import replicatorg.app.Base;

/**
 * Iterator for listing each build item in a build queue in the order they are to be printed.
 * BuildItems will be reiterated to match their quantity field values. To make things 
 * easier repeated build items are iterated sequentially.
 * 
 * The buffered readers contain the gcode of each object to be printed.
 * @author rob
 */
public class BuildQueueIterator implements Iterator<BufferedReader> {
	private BuildItem currentItem;
	private int currentIterationOfItem = 1;
	public final BuildQueuePanel buildQueue;

	public BuildQueueIterator(BuildQueuePanel buildQueue)
	{
		this.buildQueue = buildQueue;
	}
	
	@Override
	public boolean hasNext() {
		return getNextItem(false)!=null;
	}

	/**
	 *  Private method to locate the next build item
	 *  updateIteration is used to specify if we should increment the iterator to the next item.
	 */
	private BuildItem getNextItem(boolean updateIteration) {
		Component[] items = buildQueue.buildItems.getComponents();

		if (items.length == 0) return null;

		//if the current item is null set it to the first item
		if (currentItem == null)
		{
			BuildItem nextItem = (BuildItem) items[0];
			if (updateIteration == true)
			{
				currentIterationOfItem = 1;
				currentItem = nextItem;
			}
			return nextItem;
		}

		for (int i = 0; i < items.length; i++)
		{
			if (items[i] == currentItem)
			{
				// Reload this item for it's next iteration if it has more iterations left
				if (currentIterationOfItem < currentItem.quantity.get())
				{
					if (updateIteration == true) currentIterationOfItem ++;
					return currentItem;
				}
				// Load the next item if this one is done and there are more items remaining.
				else if (i+1 < items.length)
				{
					BuildItem nextItem = (BuildItem)items[i+1];

					if (updateIteration==true)
					{
						currentIterationOfItem = 1;
						currentItem = nextItem;
					}

					return nextItem;
				}
				// If the queue has reached it's end return null
				else
				{
					return null;
				}
			}
		}

		//if the current item is not in the list set it to the first item
		currentItem = null;
		return getNextItem(updateIteration);
	}

	@Override
	public BufferedReader next() {
		//TODO: get this working for compiling STLs as well as gcode
		currentItem = getNextItem(true);
		//buildQueue.buildItems.remove(currentItem);
		System.out.println("opening: "+currentItem.name);
		try {
			return currentItem.getGCodeBufferedReader();
		} catch (FileNotFoundException e) {
			Base.logger.severe("build queue file missing, skipping "+e.getMessage());
			if (hasNext())
			{
				return next();
			}
			else
			{
				return null;
			}
		}
	}

	@Override
	public void remove() {
		buildQueue.buildItems.remove(currentItem);
	}
}
