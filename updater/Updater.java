/*
 * Ryan Matthews
 *
 * Module management
 */

package updater;

// Module iteration scheduler
//
// Updates modules in round-robin fashion.

public class Updater
{
	private Updateable[] objects;

	// index of next object to update
	private int next = 0;

	// Constructs an Updater which updates a list of objects in order.
	public Updater(Updateable[] objects)
	{
		this.objects = objects;
	}

	// Runs a single scheduler iteration.
	public void iteration()
	{
		objects[next].update();

		next = (next + 1) % objects.length;
	}
}
