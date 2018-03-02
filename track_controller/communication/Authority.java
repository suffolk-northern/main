/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package track_controller.communication;

import track_controller.TrackController;

// Blocks into which a train is allowed to move
//
// For one branch

public class Authority
{
	// true/false for allowed/not-allowed
	//
	// Each element maps to a block. Indexed by distance from the inside of
	// the branch, e.g.,
	//
	//   for a track controller with 3, 4, and 5 blocks at common,
	//   left, and right branches:
	//
	//                      = = = =
	//                    /
	//      = = = - switch
	//                    \
	//                      = = = = =
	//
	//   [1, 1, 0] for the common branch maps to:
	//
	//                      = = = =
	//                    /
	//      0 1 1 - switch
	//                    \
	//                      = = = = =
	//
	//   and [0, 1, 0, 1, 1] for the right branch maps to:
	//
	//                      = = = =
	//                    /
	//      = = = - switch
	//                    \
	//                      0 1 0 1 1
	public final boolean blocks[];

	// Constructs an Authority as a copy of another.
	public Authority(Authority other)
	{
		this.blocks = other.blocks.clone();
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member initializer is
	// invalid.
	public Authority(boolean blocks[])
		throws IllegalArgumentException
	{
		if (blocks.length < 1 ||
		    blocks.length > TrackController.MAX_BRANCH_SIZE)
			throw new IllegalArgumentException("length bounds");

		this.blocks = blocks;
	}
}
