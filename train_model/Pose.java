/*
 * Ryan Matthews
 *
 * Geometry
 */

package train_model;

import track_model.GlobalCoordinates;
import track_model.Orientation;

// Position and orientation of a physical object on the surface of a globe
//
// Orientation specifies which direction is forward. It specifies rotation
// along a single axis perpendicular to the surface, e.g., North or East.

public class Pose
{
	public GlobalCoordinates position;
	public Orientation orientation;

	// Constructs a Pose object as a copy of another.
	public Pose(Pose other)
	{
		this.position = other.position;
		this.orientation = other.orientation;
	}

	// Constructs a Pose object from a position and an orientation.
	public Pose(GlobalCoordinates position, Orientation orientation)
	{
		this.position = position;
		this.orientation = orientation;
	}
}
