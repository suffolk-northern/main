/*
 * Ryan Matthews
 *
 * Simulation
 */

package train_model;

import track_model.TrackModel;
import track_model.TrackBlock;
import track_model.Orientation;
import track_model.GlobalCoordinates;
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

	// for moving along blocks
	private final String LINE = "green";
	private final TrackModel track;
	private TrackBlock block = null;
	private double displacement = 0.0;
	private int lastBlockId = 0;
	private boolean forward = true;
	private boolean fromCommon = false;

	private int startCounter = 0;
	private static final int startCounterMod = 1000;

	// Constructs a PointMass as a copy of another object.
	public PointMass(PointMass other)
	{
		this.mass  = other.mass;
		this.pose  = other.pose;
		this.speed = other.speed;
		this.track = other.track;
		this.displacement = other.displacement;
		this.lastBlockId = other.lastBlockId;
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
	public PointMass(double mass, Pose pose, TrackModel track)
	{
		this.mass = mass;
		this.pose = new Pose(pose);
		this.track = track;
	}

	// Returns the current pose.
	public Pose pose()
	{
		return new Pose(pose);
	}

	// Returns the current block location as block ID.
	public int block()
	{
		return block != null ? block.getBlock() : 0;
	}

	// Sets a new pose instantaneously.
	//
	// Sets the speed to zero.
	public void slew(Pose pose)
	{
		this.pose = new Pose(pose);
		this.speed = 0.0;

		block = null;

		// FIXME: this is a hack, because the track model slews us
		// repeatedly for some reason
		startCounter = startCounterMod;

		push(0.0, 10);
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

		double displacementChange = speedAverage * timeSeconds;

		// meters
		double displacementXChange = displacementChange
				* Math.sin(pose.orientation.radians());
		double displacementYChange = displacementChange
				* Math.cos(pose.orientation.radians());

		// yards
		double displacementXChangeYards =
			yardsPerMeter * displacementXChange;
		double displacementYChangeYards =
			yardsPerMeter * displacementYChange;

		speed = speedFinal;

		if (speed < 0.0) speed = 0.0;

		// Steer trains.

		if (block == null) {
			startCounter += time;
			if (startCounter >= startCounterMod)
				startCounter = 0;

			if (startCounter != 0)
				return;

			block = track.getClosestBlock(pose.position, LINE);

			forward = track.getSide(pose.position,
			                 LINE, block.getBlock());
			displacement = forward ? 0.0 : block.getLength();
			pose.position = track.getPositionAlongBlock(
			                 LINE, block.getBlock(), displacement);
		}

		double length = block.getLength();

		if (block.getBlock() == 0) {
			block = null;
			return;
		}

		if (displacementChange < 0.00001)
			return;

		if (forward)
			displacement += displacementChange;
		else
			displacement -= displacementChange;

		if (displacement < 0.0 || displacement > length)
			advanceBlock();

		GlobalCoordinates oldPosition = pose.position;

		pose.position = track.getPositionAlongBlock(LINE,
		                                            block.getBlock(),
		                                            displacement);

		pose.orientation = oldPosition.directionTo(pose.position);

		if (block.getBlock() == 0) {
			block = null;
			displacement = 0.0;
			lastBlockId = 0;
			forward = true;
			fromCommon = false;
		}
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

	// Configures state for the next block.
	private void advanceBlock()
	{
		int id = block.getBlock();

		int nextId = forward ?
			block.getNextBlockId() :
			block.getPrevBlockId();

		TrackBlock next = track.getBlock(LINE, nextId);

		boolean isSwitch = block.isIsSwitch();
		boolean nextIsSwitch = next.isIsSwitch();

		if (isSwitch && nextIsSwitch)
		{
			throw new IllegalStateException("algo can't do tat");
		}

		if (!isSwitch && !nextIsSwitch)
		{
			// no switch involved

			block = next;
		}
		else if (nextIsSwitch
		         && next.getSwitchPosition() == block.getBlock())
		{
			// move onto switch from left or right branch
			//
			// If switch was facing the wrong way, we miss this
			// case and go the wrong way.

			block = next;
			fromCommon = false;
		}
		else if (nextIsSwitch)
		{
			// move onto switch from common branch

			block = next;
			fromCommon = true;
		}
		else if (isSwitch && fromCommon)
		{
			// move onto left or right branch

			block = track.getBlock(LINE, block.getSwitchPosition());
		}
		else if (isSwitch && !fromCommon)
		{
			// move onto common branch

			if (block.getSwitchDirection() > 0)
				block = track.getBlock(LINE,
				                       block.getPrevBlockId());
			else
				block = track.getBlock(LINE,
				                       block.getNextBlockId());
		}
		else
		{
			throw new IllegalStateException("algo can't do that");
		}

		forward = track.getSide(pose.position, LINE, block.getBlock());
		displacement = forward ? 0.0 : block.getLength();
	}

	// How much this mass (in kilograms) weighs in pounds force
	//
	// Returns the weight in pounds.
	private static double kilogramsToPounds(double kilograms)
	{
		return kilograms / kilogramsPerPound;
	}
}
