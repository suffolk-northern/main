/*
 * Ryan Matthews
 *
 * Module management
 */

package updater;

// Module iteration scheduler
//
// Updates modules in round-robin-ish fashion.

public class Updater
{
	// milliseconds
	private static int period = 100;

	private Updateable[] objects;

	// Constructs an Updater which updates a list of objects in order.
	public Updater(Updateable[] objects)
	{
		this.objects = objects;
	}

	// Runs a single scheduler iteration.
	public void iteration()
	{
		for (Updateable i : objects)
			i.update(period);
	}
}
