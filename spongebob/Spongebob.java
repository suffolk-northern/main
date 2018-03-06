/*
 * Ryan Matthews
 *
 * Updater demonstration
 */

package spongebob;

import updater.Updateable;

// Spongebob module

public class Spongebob implements Updateable
{
	// true if ready to rumble, else false
	private boolean readyToRumble = false;

	// Returns true if ready to rumble, else false
	public boolean readyToRumble()
	{
		return readyToRumble;
	}

	// Updates this object.
	public void update(int time)
	{
		readyToRumble = !readyToRumble;
	}
}
