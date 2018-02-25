/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package track_controller.communication;

import track_controller.TrackController;

// Communication link between two track controllers

public class ControllerLink
{
	private TrackController controller;
	private Branch branch;

	// Constructs a ControllerLink that communicates with a track
	// controller concerning one of its branches.
	public ControllerLink(TrackController controller, Branch branch)
	{
		this.controller = controller;
		this.branch = branch;
	}

	// Returns the authority of the connected branch.
	public Authority authority()
	{
		return controller.authority(branch);
	}
}
