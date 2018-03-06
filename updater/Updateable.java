/*
 * Ryan Matthews
 *
 * Module management
 */

package updater;

public interface Updateable
{
	// Updates this object.
	//
	// Parameter time is the amount of time that this update accounts for,
	// i.e., the amount of simulated time that passes during the cycle.
	//
	// Shall not block. Shall be fast.
	public void update(int time);
}
