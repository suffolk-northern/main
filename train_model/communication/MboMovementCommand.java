/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

// Instructions for a train's motion
//
// Sent the MBO over a direct radio link.

public class MboMovementCommand
{
	// Member bounds
	public static final int MAX_SPEED = 70;
	public static final int MAX_AUTHORITY = 3000;

	// Target speed
	//
	// Units: kilometer per hour
	public int speed;

	// Distance allowed to travel before stopping
	//
	// Units: meter
	public int authority;

	// Constructs a MboMovementCommand as a copy of another.
	public MboMovementCommand(MboMovementCommand other)
	{
		this.speed     = other.speed;
		this.authority = other.authority;
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member initializer is
	// invalid.
	public MboMovementCommand(int speed, int authority)
		throws IllegalArgumentException
	{
		if (speed < 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("speed bounds");

		if (authority < 0 || authority > MAX_AUTHORITY)
			throw new IllegalArgumentException("authority bounds");

		this.speed = speed;
		this.authority = authority;
	}
}
