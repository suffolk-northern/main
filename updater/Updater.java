/*
 * Ryan Matthews
 *
 * Module management
 */

package updater;

import java.util.Timer;
import java.util.TimerTask;

// Module iteration scheduler
//
// Updates modules in round-robin-ish fashion.

public class Updater
{
	// milliseconds
	private final int period;

	private Updateable[] objects;

	private Timer timer = new Timer();
	private TimerTask task = new Task(this);

	// Constructs an Updater which updates a list of objects in order.
	//
	// Parameter period is the length of one update in simulation time, in
	// milliseconds.
	//
	// Throws IllegalArgumentException if period is not positive.
	public Updater(int period, Updateable[] objects)
		throws IllegalArgumentException
	{
		if (period < 0)
			throw new IllegalArgumentException("period sign");

		this.period = period;
		this.objects = objects;
	}

	// Runs a single scheduler iteration.
	//
	// NOTE: Not thread-safe if scheduleAtFixedRate() has already been
	// called.
	public void iteration()
	{
		for (Updateable i : objects)
			i.update(period);
	}

	// Schedules iteration() for repeated fixed-rate execution, beginning
	// immediately.
	//
	// Parameter period is the time in milliseconds between iterations.
	public void scheduleAtFixedRate(int period)
	{
		timer.cancel();
		timer.scheduleAtFixedRate(task, 0, period);
	}

	// TimerTask that calls iteration() on an Updater.
	private class Task extends TimerTask
	{
		Updater updater;

		public Task(Updater updater)
		{
			this.updater = updater;
		}

		public void run()
		{
			updater.iteration();
		}
	}
}
