/*
 * Ryan Matthews
 *
 * Updater timing robustness testing
 */

package test.updater.mock;

import updater.Updateable;

// Mock module which takes minimal execution time

public class NiceModule
	implements Updateable
{
	// Updates this object.
	public void update(int time)
	{
		// do nothing
	}
}
