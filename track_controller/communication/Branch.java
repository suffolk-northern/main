/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package track_controller.communication;

// Specifies which branch in a track controller's track block jurisdiction

public enum Branch
{
	// at the converging end of the switch
	common,

	// at one diverging end of the switch
	left,

	// at the other diverging end of the switch
	right
}
