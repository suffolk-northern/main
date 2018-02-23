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
	// Shall not block. Shall be fast.
	public void update();
}
