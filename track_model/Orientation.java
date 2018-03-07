/*
 * Ryan Matthews
 *
 * Geometry
 */

package track_model;

// Direction of an object on the surface of a globe

public class Orientation
{
	private static final double degreesPerRadian = 180.0 / Math.PI;

	private final double direction;

	// Returns a new Orientation object with specified direction in
	// radians.
	//
	// Parameter direction is radians in [0.0, 2.0 * Math.PI), starting
	// from north at 0.0, increasing clockwise until 2.0 * Math.PI,
	// non-inclusive. E.g.,
	//
	//	0.0 * Math.PI - north
	//	0.5 * Math.PI - east
	//	1.0 * Math.PI - south
	//	1.5 * Math.PI - west
	//
	// Throws IllegalArgumentException if direction not in legal bounds.
	public static Orientation radians(double direction)
	{
		return new Orientation(direction);
	}

	// Returns a new Orientation object with specified direction in
	// degrees.
	//
	// Parameter direction is degrees in [0.0, 360.0), starting from north
	// at 0.0, increasing clockwise until 360.0, non-inclusive. E.g.,
	//
	//	  0.0 - north
	//	 90.0 - east
	//	180.0 - south
	//	270.0 - west
	//
	// Throws IllegalArgumentException if direction not in legal bounds.
	public static Orientation degrees(double direction)
	{
		if (direction < 0.0 || direction >= 360.0)
			throw new IllegalArgumentException("direction range");

		direction /= degreesPerRadian;

		// clip to legal range because floating point errors
		if (direction <  0.0          ) direction = 0.0;
		if (direction >= 2.0 * Math.PI) direction = 0.0;

		return new Orientation(direction);
	}

	public Orientation(Orientation other)
	{
		this.direction = other.direction;
	}

	private Orientation(double direction)
		throws IllegalArgumentException
	{
		if (direction < 0.0 || direction >= 2 * Math.PI)
			throw new IllegalArgumentException("direction range");

		this.direction = direction;
	}

	// Returns the direction in radians.
	public double radians()
	{
		return direction;
	}

	// Returns the direction in degrees.
	public double degrees()
	{
		double value = degreesPerRadian * direction;

		// clip to legal range because floating point errors
		if (value <    0.0) value = 0.0;
		if (value >= 360.0) value = 0.0;

		return value;
	}

	// Adds an angle in radians to the direction.
	//
	// Returns the result.
	public Orientation addRadians(double value)
	{
		double direction = this.direction + value;

		direction %= 2 * Math.PI;

		// because of the way modulus of negatives works in this
		// language
		if (direction < 0.0)
			direction += 2 * Math.PI;

		// clip to legal range because floating point errors
		if (direction <  0.0          ) direction = 0.0;
		if (direction >= 2.0 * Math.PI) direction = 0.0;

		return new Orientation(direction);
	}

	// Adds an angle in degrees to the direction.
	//
	// Returns the result.
	public Orientation addDegrees(double value)
	{
		return addRadians(value / degreesPerRadian);
	}

	// Subtracts an angle in radians from the direction.
	//
	// Returns the result.
	public Orientation subtractRadians(double value)
	{
		return addRadians(-value);
	}

	// Subtracts an angle in degrees from the direction.
	//
	// Returns the result.
	public Orientation subtractDegrees(double value)
	{
		return addDegrees(-value);
	}
}
