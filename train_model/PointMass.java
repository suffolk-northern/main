/*
 * Ryan Matthews
 *
 * Simulation
 */

package train_model;

import track_model.Orientation;
import train_model.Pose;

// Physics point mass
//
// Has a mass.
//
// Maintains displacement and velocity vectors.

public class PointMass
{
	// how much mass (in kilograms) weighs one pound on Earth
	private static final double kilogramsPerPound = 0.453592;

	// how many yards in a meter
	private static final double yardsPerMeter = 1.093613;

	// in kilograms
	private final double mass;

	// combination of these two represents displacement/velocity vectors
	//
	// speed is meters per second
	private Pose pose;
	private double speed = 0.0;

	// Constructs a PointMass as a copy of another object.
	public PointMass(PointMass other)
	{
		this.mass  = other.mass;
		this.pose  = other.pose;
		this.speed = other.speed;
	}

	// Constructs a PointMass with given intrinsic properties and initial
	// conditions.
	//
	// Intrinsic properties:
	//
	//   Parameter mass is kilograms.
	//
	// Initial conditions:
	//
	//   Parameter pose is the initial position and direction.
	//
	//   Velocity starts at zero.
	public PointMass(double mass, Pose pose)
	{
		this.mass = mass;
		this.pose = new Pose(pose);
	}

	// Returns the current pose.
	public Pose pose()
	{
		return new Pose(pose);
	}

	// Sets a new pose instantaneously.
	//
	// Sets the speed to zero.
	public void slew(Pose pose)
	{
		this.pose = new Pose(pose);
		this.speed = 0.0;
	}

	// Returns the current speed.
	//
	// Units: meters per second
	public double speed()
	{
		return speed;
	}

	// Returns the current orientation.
	public Orientation orientation()
	{
		return new Orientation(pose.orientation);
	}

	// Sets the orientation.
	//
	// Causes an instantaneous change in the direction of the velocity
	// vector, i.e., "turns" the object without changing its speed.
	public void orientation(Orientation value)
	{
		pose.orientation = new Orientation(value);
	}

	// Simulates pushing with a constant force for an amount of time.
	//
	// Updates position and velocity. Speed in the forward direction is
	// clippped to [0, infinity).
	//
	// Does the math and returns immediately, i.e., does not sleep.
	//
	// Parameter force is newtons in the forward direction. Can be
	// negative.
	//
	// Parameter time is milliseconds.
	public void push(double force, int time)
	{
		// meters per second per second
		double acceleration = force / mass;

		// seconds
		double timeSeconds = time / 1000.0;

		// meters per second
		double speedChange  = acceleration * timeSeconds;
		double speedInitial = speed;
		double speedFinal   = speed + speedChange;
		double speedAverage = speedInitial + 0.5 * speedChange;

		// meters
		double displacementXChange =
			speedAverage * Math.sin(pose.orientation.radians())
			* timeSeconds;
		double displacementYChange =
			speedAverage * Math.cos(pose.orientation.radians())
			* timeSeconds;

		// yards
		double displacementXChangeYards =
			yardsPerMeter * displacementXChange;
		double displacementYChangeYards =
			yardsPerMeter * displacementYChange;

		pose.position = pose.position.addYards(
			displacementYChangeYards,
			displacementXChangeYards
		);

		speed = speedFinal;

		if (speed < 0.0) speed = 0.0;
	}

	// How much mass (in kilograms) weighs this many pounds
	//
	// Returns the mass in kilograms.
	public static double poundsToKilograms(double pounds)
	{
		return kilogramsPerPound * pounds;
	}

	// Returns the amount of force which, applied at the current velocity,
	// would exert the given amount of power.
	//
	// If current velocity is zero, returns zero.
	//
	// Parameter power is Watts.
	//
	// Returns newtons.
	public double forceForPower(double power)
	{
		if (speed == 0.0)
			return 0.0;

		// power is kilogram * meter^2 / second^3
		//
		// speed is meter / second
		//
		// force is kilogram * meter / second^2 = newton

		double absValue = power / Math.abs(speed);

		return power >= 0.0 ? absValue : -absValue;
	}

	// How much this mass (in kilograms) weighs in pounds force
	//
	// Returns the weight in pounds.
	private static double kilogramsToPounds(double kilograms)
	{
		return kilograms / kilogramsPerPound;
	}
}
