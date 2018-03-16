/*
 * Ryan Matthews
 *
 * Updater timing robustness testing
 */

package test.updater.mock;

import updater.Updateable;

// Mock module which sometimes takes too much execution time

public class NaughtyModule
	implements Updateable
{
	final int delay;

	int counter = 0;

	// Constructs a NaughtyModule that sometimes takes longer than period
	// milliseconds to update.
	public NaughtyModule(int period)
	{
		delay = 2 * period;
	}

	// Updates this object.
	public void update(int time)
	{
		counter = (counter + 1) % 10;

		if (counter == 0)
			delay();
	}

	private void delay()
	{
		try {
			System.out.printf("naughty delay %d ms\n", delay);
			Thread.sleep(delay);
		}
		catch (InterruptedException e) {
			// speaking words of wisdom, let it be
		}
	}
}
