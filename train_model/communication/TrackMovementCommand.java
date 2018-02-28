/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

import track_controller.communication.Authority;

// Instructions for a train's motion
//
// Sent by CTC via the track.

public class TrackMovementCommand
{
	// Member bounds
	public static final int MAX_SPEED = 35;

	// Speed member must be a multiple of this
	public static final int SPEED_MULTIPLE = 5;

	// Target speed
	//
	// Units: MPH (miles per hour)
	public int speed;

	// Blocks into which the train is allowed to move
	//
	// From the POV of the track controller whose jurisdiction we are in
	//
	// NOTE: The train could be on any of the three branches and could be
	// going in any direction. The train has the responsibility of figuring
	// out where it is.
	//
	// A branch with zero length has null authority.
	public Authority commonAuthority;
	public Authority   leftAuthority;
	public Authority  rightAuthority;

	// Constructs a TrackMovementCommand as a copy of another.
	public TrackMovementCommand(TrackMovementCommand other)
	{
		this.speed = other.speed;

		this.commonAuthority = other.commonAuthority;
		this.  leftAuthority = other.  leftAuthority;
		this. rightAuthority = other. rightAuthority;
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member initializer is
	// invalid.
	public TrackMovementCommand(int speed,
	                            Authority commonAuthority,
	                            Authority   leftAuthority,
	                            Authority  rightAuthority)
		throws IllegalArgumentException
	{
		if (speed < 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("speed bounds");

		if (speed % SPEED_MULTIPLE != 0)
			throw new IllegalArgumentException("speed multiple");

		if (commonAuthority == null)
			throw new IllegalArgumentException(
				"common authority null");

		this.speed = speed;

		this.commonAuthority = commonAuthority;
		this.  leftAuthority =   leftAuthority;
		this. rightAuthority =  rightAuthority;
	}
}
