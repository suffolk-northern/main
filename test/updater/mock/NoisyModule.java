/*
 * Ryan Matthews
 *
 * Updater timing robustness testing
 */

package test.updater.mock;

import updater.Updateable;

// Mock module which prints during each update

public class NoisyModule
	implements Updateable
{
	int counter = 0;

	// Updates this object.
	public void update(int time)
	{
		System.out.printf("iteration %d\n", counter++);
	}
}
