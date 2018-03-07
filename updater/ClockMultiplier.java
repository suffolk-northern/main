/*
 * Ryan Matthews
 *
 * Module management
 */

package updater;

import updater.Updateable;

// Updateable update() frequency multipler
//
// Useful for increasing the update() frequency of Updateable objects that need
// to be updated by a slower Updater.
//
// Each time an Updater calls update() on a ClockMultiplier, the
// ClockMultiplier in turn calls update() on its Updateable objects in
// round-robin-ish fashion.

public class ClockMultiplier
	implements Updateable
{
	private final Updateable[] objects;
	private final int multiplier;

	// Constructs a ClockMultiplier that updates its objects multiplier
	// times for each of its own updates().
	//
	// Throws IllegalArgumentException if multiplier is negative.
	public ClockMultiplier(int multiplier, Updateable[] objects)
		throws IllegalArgumentException
	{
		this.multiplier = multiplier;
		this.objects = objects.clone();
	}

	// Updates this object.
	public void update(int time)
	{
		// TODO: account for division remainder

		for (int i = 0; i < multiplier;     ++i)
		for (int j = 0; j < objects.length; ++j)
			objects[j].update(time / multiplier);
	}
}
