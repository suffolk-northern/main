/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

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

	// Distance allowed to travel before stopping
	//
	// Units: yard
	public int authority;

	// Constructs a TrackMovementCommand as a copy of another.
	public TrackMovementCommand(TrackMovementCommand other)
	{
		this.speed = other.speed;
		this.authority = other.authority;
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member initializer is
	// invalid.
	public TrackMovementCommand(int speed, int authority)
		throws IllegalArgumentException
	{
		if (speed < 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("speed bounds");

		if (speed % SPEED_MULTIPLE != 0)
			throw new IllegalArgumentException("speed multiple");

		if (authority < 0)
			throw new IllegalArgumentException("authority range");

		this.speed = speed;
		this.authority = authority;
	}
}
