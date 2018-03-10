/*

 * Ryan Matthews

 *

 * Inter-module communication

 */



package track_controller.communication;



import track_controller.TrackController;

import track_controller.communication.Authority;

import track_controller.communication.Branch;



// Communication link between track controller and CTC

//

// From the POV of the CTC



public class CtcLink

{

	private TrackController controller;



	// Constructs a CtcLink that communicates with a track controller.

	public CtcLink(TrackController controller)

	{
            
		this.controller = controller;

	}



	// Sets the authority at the specified branch.

	//

	// Throws IllegalArgumentException if specified branch is length zero,

	// or if authority is null, or if its size does not match the specified

	// branch length.

	public void authority(Branch branch, Authority authority)

		throws IllegalArgumentException

	{

		controller.authority(branch, authority);

	}



	// Sets the speeds at all blocks in the specified branch.

	//

	// Indexed by distance from the inside of the branch in the same way as

	// Authority.

	//

	// Throws IllegalArgumentException if specified branch is length zero,

	// or if speeds array is null, or if its length is not equal to the

	// specified branch length.

	public void speeds(Branch branch, Speed[] speeds)

		throws IllegalArgumentException

	{

		controller.speed(branch, speeds);

	}

}