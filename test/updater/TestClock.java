/*
 * Ryan Matthews
 *
 * Basic Clock demonstration
 */

package test.updater;

import java.util.Date;

import test.updater.mock.NiceModule;
import test.updater.mock.TimeModule;
import updater.Updater;
import updater.Updateable;

public class TestClock
{
	public static void main(String[] args)
	{
		final int simulationUpdatePeriod = 1000;
		final int       wallUpdatePeriod = 1000;

		Date now = new Date();
		Date initialTime = now;

		Updateable modules[] = {
			new NiceModule(),
			new TimeModule(initialTime)
		};

		Updater updater = new Updater(simulationUpdatePeriod, modules);

		updater.scheduleAtFixedRate(wallUpdatePeriod);
	}
}
