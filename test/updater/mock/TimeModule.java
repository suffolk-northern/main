/*
 * Ryan Matthews
 *
 * Updater timing robustness testing
 */

package test.updater.mock;

import java.util.Date;

import updater.Clock;
import updater.Updateable;

// Mock module which prints simulation time during each update

public class TimeModule
	implements Updateable
{
	Clock clock = new Clock();

	// Constructs a TimeModule whose initial time is the given Date.
	public TimeModule(Date date)
	{
		clock = new Clock(date);
	}

	// Updates this object.
	public void update(int time)
	{
		clock.advance(time);

		System.out.printf("time is %s\n", clock.time());
	}
}
