/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

// Instructions for a train's motion
//
// Sent by CTC or MBO

public class MovementCommand
{
	// Number of yards in a mile
	private static final int YARDS_PER_MILE = 1760;

	// Member bounds
	public static final int MAX_SPEED = 35;
	public static final int MAX_AUTHORITY = YARDS_PER_MILE;

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

	// Constructs a MovementCommand as a copy of another.
	public MovementCommand(MovementCommand other)
	{
		this.speed     = other.speed;
		this.authority = other.authority;
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member initializer is
	// invalid.
	public MovementCommand(int speed, int authority)
		throws IllegalArgumentException
	{
		if (speed < 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("speed bounds");

		if (speed % SPEED_MULTIPLE != 0)
			throw new IllegalArgumentException("speed multiple");

		if (authority < 0 || authority > MAX_AUTHORITY)
			throw new IllegalArgumentException("authority bounds");

		this.speed = speed;
		this.authority = authority;
	}
}
