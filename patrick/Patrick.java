/*
 * Ryan Matthews
 *
 * Updater demonstration
 */

package patrick;

import spongebob.Spongebob;
import updater.Updateable;

// Patrick Star module

public class Patrick implements Updateable
{
	private final String ANNOUNCEMENT =
		"Patrick: Spongebob is READY TO RUMBLE";

	// Spongebob to watch for readiness to rumble
	private Spongebob spongebob;

	// Updates this object.
	public void update()
	{
		if (spongebob == null)
			return;

		if (spongebob.readyToRumble())
			System.out.println(ANNOUNCEMENT);
	}

	// Sets the Spongebob that this object communicates with.
	public void registerSpongebob(Spongebob spongebob)
	{
		this.spongebob = spongebob;
	}
}
