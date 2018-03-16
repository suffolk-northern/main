/*
 * Ryan Matthews
 *
 * Updater timing robustness testing
 */

package test.updater;

import test.updater.mock.NaughtyModule;
import test.updater.mock.NiceModule;
import test.updater.mock.NoisyModule;
import updater.Updater;
import updater.Updateable;

public class TestTimingRobustness
{
	public static void main(String[] args)
	{
		final int simulationUpdatePeriod = 1000;
		final int       wallUpdatePeriod = 1000;

		Updateable modules[] = {
			new NaughtyModule(simulationUpdatePeriod),
			new NiceModule(),
			new NoisyModule()
		};

		Updater updater = new Updater(simulationUpdatePeriod, modules);

		updater.scheduleAtFixedRate(wallUpdatePeriod);
	}
}
