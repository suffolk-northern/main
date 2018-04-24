/*
 * Ryan Matthews
 *
 * Simulation
 */

package train_model;

import java.util.Observable;

import track_model.GlobalCoordinates;
import track_model.Orientation;
import track_model.TrackModel;
import train_model.Cabin;
import train_model.Failure;
import train_model.PointHeat;
import train_model.PointMass;
import train_model.Pose;
import train_model.communication.BeaconMessage;
import train_model.communication.BeaconRadio;
import train_model.communication.ControllerLink;
import train_model.communication.MboRadio;
import train_model.communication.MboMovementCommand;
import train_model.communication.Relay;
import train_model.communication.TrackCircuit;
import train_model.communication.TrackMovementCommand;
import updater.Updateable;

// Physical simulation of a train vehicle
//
// For inter-module communication, use:
//
//   beaconRadio()
//   controllerLink()
//   mboRadio()
//   trackCircuit()

public class TrainModel
	extends Observable
	implements Updateable
{
	// conversions
	private static final double KILOGRAMS_PER_TON = 907.185;
	private static final double MPS_PER_KPH = 0.277778;

	// datasheet constants
	private static final double DATASHEET_ACCEL_MPSPS   =  0.50;
	private static final double DATASHEET_SDECEL_MPSPS  =  1.20;
	private static final double DATASHEET_EDECEL_MPSPS  =  2.73;
	private static final double DATASHEET_MID_SPEED_KPH = 35.00;
	private static final double DATASHEET_MID_MASS_TON  = 50.00;
	private static final double DATASHEET_POWER_WATT = 120e3;

	// kilograms
	private static final double MASS_EMPTY = 35.0 * KILOGRAMS_PER_TON;

	// derived constants from datasheet constants (SI units)
	private static final double DERIVED_MID_SPEED =
		MPS_PER_KPH * DATASHEET_MID_SPEED_KPH;
	private static final double DERIVED_MID_ENGINE_FORCE =
		MASS_EMPTY * DATASHEET_ACCEL_MPSPS;

	// watts, newtons
	private static final double MAX_ENGINE_POWER = DATASHEET_POWER_WATT;
	private static final double MAX_ENGINE_FORCE =
		2.0 * DERIVED_MID_ENGINE_FORCE;

	// newtons
	private static final double MAX_SERVICE_BRAKE_FORCE =
		MASS_EMPTY * DATASHEET_SDECEL_MPSPS;
	private static final double   EMERGENCY_BRAKE_FORCE =
		MASS_EMPTY * DATASHEET_EDECEL_MPSPS;

	// watts
	private static final double HEATER_POWER  = 1.0;
	private static final double COOLING_POWER = 0.5;

	private static final Pose INITIAL_POSE = new Pose(
		new GlobalCoordinates(0.0, 0.0),
		Orientation.degrees(0.0)
	);

	// notify observers once every this many updates
	private static int NOTIFY_OBSERVERS_MOD = 2;

	// identifier
	private final int id;

	private final PointMass pointMass;

	// watts
	private double enginePower = 0.0;

	// newtons
	private double serviceBrakeForce = 0.0;
	private double emergencyBrakeForce = 0.0;

	private boolean engineFailure = false;
	private boolean serviceBrakeFailure = false;
	private boolean emergencyBrakeFailure = false;

	// true/false for open/closed
	//
	// indices mapped by doorLocationToIndex():
	boolean doors[] = new boolean[2];

	private final PointHeat pointHeat = new PointHeat();
	private double heaterPower = 0.0;

	private boolean lightsAreOn = false;

	private final Relay relay = new Relay(this);

	private final ControllerLink controllerLink =
		new ControllerLink(relay, this);

	private final MboRadio mboRadio = new MboRadio(relay);

	private int notifyObserversCount = 0;

	private final TrackModel track;

	private final Cabin cabin = new Cabin();

	// Constructs a TrainModel with the given identifier.
	public TrainModel(int id, TrackModel track)
	{
		this.id = id;
		this.track = track;
		this.pointMass = new PointMass(MASS_EMPTY, INITIAL_POSE, track);
	}

	// Returns this train's identifier.
	public int id()
	{
		return id;
	}

	// Returns a train_model.communication.BeaconRadio suitable for
	// communicating with this train.
	public BeaconRadio beaconRadio()
	{
		return new BeaconRadio(relay);
	}

	// Returns the train_model.communication.ControllerLink to be used for
	// communicating with this train.
	public ControllerLink controllerLink()
	{
		return controllerLink;
	}

	// Returns the train_model.communication.MboRadio to be used for
	// communicating with this train.
	public MboRadio mboRadio()
	{
		return mboRadio;
	}

	// Returns a train_model.communication.TrackCircuit suitable for
	// communicating with this train.
	public TrackCircuit trackCircuit()
	{
		return new TrackCircuit(relay);
	}

	// Returns the last received beacon message.
	public BeaconMessage lastBeaconMessage()
	{
		return relay.lastBeaconMessage();
	}

	// Returns the last received MBO message.
	public MboMovementCommand lastMboMessage()
	{
		return relay.lastMboMessage();
	}

	// Returns the last received track message.
	public TrackMovementCommand lastTrackMessage()
	{
		return relay.lastTrackMessage();
	}

	// Updates this object.
	public void update(int time)
	{
		double engineForce = pointMass.forceForPower(enginePower);

		// BS force at zero velocity
		if (engineForce == 0.0)
			engineForce = 0.1;

		if (engineForce > MAX_ENGINE_FORCE)
			engineForce = MAX_ENGINE_FORCE;

		double effectiveEngineForce = engineForce;
		double effectiveServiceBrakeForce = serviceBrakeForce;
		double effectiveEmergencyBrakeForce = emergencyBrakeForce;

		if (engineFailure)
			effectiveEngineForce = 0.0;

		if (serviceBrakeFailure)
			effectiveServiceBrakeForce = MAX_SERVICE_BRAKE_FORCE;

		if (emergencyBrakeFailure)
			effectiveEmergencyBrakeForce = 0.0;

		double netForce = effectiveEngineForce
		                  - effectiveServiceBrakeForce
		                  - effectiveEmergencyBrakeForce;

		pointMass.push(netForce, time);

		double netPower = heaterPower - COOLING_POWER;

		pointHeat.conduct(netPower, time);

		notifyActions();
	}

	// Returns the current location.
	//
	// Delayed by 100 ms.
	public GlobalCoordinates location()
	{
		return new GlobalCoordinates(pointMass.pose().position);
	}

	// Returns the current block location as block ID.
	public int block()
	{
		return pointMass.block();
	}

	// Returns the current orientation.
	public Orientation orientation()
	{
		return pointMass.orientation();
	}

	// Instantaneously moves to the given pose on the given line.
	//
	// Sets the speed to zero.
	//
	// Throws IllegalArgumentException if line is not "green" or "red",
	// ignoring case.
	public void slew(String line, Pose pose)
	{
		pointMass.slew(line.toLowerCase(), pose);
	}

	// Sets the orientation.
	//
	// Causes an instantaneous change in the direction of the velocity
	// vector, i.e., "turns" the train without changing its speed.
	public void orientation(Orientation value)
	{
		pointMass.orientation(value);
	}

	// Returns the mass.
	//
	// Units: kilograms
	public double mass()
	{
		return pointMass.mass();
	}

	// Returns the current speed.
	//
	// Units: meters per second
	public double speed()
	{
		return pointMass.speed();
	}

	// Returns the engine output power.
	//
	// Units: Watts
	public double power()
	{
		return enginePower;
	}

	// Sets the engine output power.
	//
	// Units: Proportion of max output power, e.g.,
	//
	//   0.5 = 50% output power
	//
	// Failure mode: Engine output is zero regardless of input.
	//
	// Throws IllegalArgumentException if value not in [0.0, 1.0].
	public void power(double value)
		throws IllegalArgumentException
	{
		if (value < 0.0 || value > 1.0)
			throw new IllegalArgumentException("power range");

		enginePower = MAX_ENGINE_POWER * value;
	}

	// Returns the service brake output.
	//
	// Units: Newtons
	public double serviceBrake()
	{
		return serviceBrakeForce;
	}

	// Sets the service brake output.
	//
	// Units: Proportion of max braking force, e.g.,
	//
	//   0.5 = 50% braking force
	//
	// Failure mode: Service brake applies 100% force regardless of input.
	//
	// Throws IllegalArgumentException if value not in [0.0, 1.0].
	public void serviceBrake(double value)
		throws IllegalArgumentException
	{
		if (value < 0.0 || value > 1.0)
			throw new IllegalArgumentException("force range");

		serviceBrakeForce = MAX_SERVICE_BRAKE_FORCE * value;
	}

	// Returns true if the emergency brake has been applied.
	public boolean emergencyBrake()
	{
		return emergencyBrakeForce != 0.0;
	}

	// Engages the emergency brake.
	//
	// Irreversible. Damages the vehicle.
	//
	// Failure mode: Emergency brake applies no force regardless of input.
	public void applyEmergencyBrake()
	{
		emergencyBrakeForce = EMERGENCY_BRAKE_FORCE;
	}

	// Returns the current grade.
	//
	// Units: TBD.
	public double grade()
	{
		// unimplemented

		return 0.0;
	}

	// Returns true if specified door(s) are open.
	public boolean door(DoorLocation location)
	{
		int index = doorLocationToIndex(location);

		return doors[index];
	}

	// Opens specified door(s).
	//
	// Side effect: Exchanges passengers with the track model for the
	// nearest station.
	//
	// No failure modes. Always succeeds.
	public void openDoor(DoorLocation location)
	{
		int index = doorLocationToIndex(location);

		if (doors[index])
			return;

		doors[index] = true;

		if (!pointMass.atStation())
			return;

		int leaving = cabin.unloadPassengers();
		int free = cabin.free();

		int boarding = track.exchangePassengers(id(), leaving, free);

		cabin.loadPassengers(boarding);

		pointMass.mass(MASS_EMPTY + cabin.mass());
	}

	// Closes specified door(s).
	//
	// No failure modes. Always succeeds.
	public void closeDoor(DoorLocation location)
	{
		int index = doorLocationToIndex(location);

		doors[index] = false;
	}

	// Returns the temperature.
	//
	// Units: celsius
	public double temperature()
	{
		return pointHeat.temperature();
	}

	// Returns true if the heater is on.
	public boolean heater()
	{
		return heaterPower != 0.0;
	}

	// Turns the heater on.
	public void heaterOn()
	{
		heaterPower = HEATER_POWER;
	}

	// Turns the heater off.
	public void heaterOff()
	{
		heaterPower = 0.0;
	}

	// Returns true if the lights are on.
	public boolean lights()
	{
		return lightsAreOn;
	}

	// Turns the lights on.
	public void lightsOn()
	{
		lightsAreOn = true;
	}

	// Turns the lights off.
	public void lightsOff()
	{
		lightsAreOn = false;
	}

	// Returns the number of passengers aboard.
	//
	// Does not include crew.
	public int passengers()
	{
		return cabin.passengers();
	}

	// Returns the number of crew members aboard.
	public int crew()
	{
		return cabin.crew();
	}

	// Returns true if the given failure type has been triggered.
	public boolean failure(Failure failure)
	{
		switch (failure)
		{
			case ENGINE:
				return engineFailure;
			case SERVICE_BRAKE:
				return serviceBrakeFailure;
			case EMERGENCY_BRAKE:
				return emergencyBrakeFailure;
			case TRACK_RX:
				return relay.failure(Failure.TRACK_RX);
			case BEACON_RX:
				return relay.failure(Failure.BEACON_RX);
			case MBO_RX:
				return relay.failure(Failure.MBO_RX);
			case MBO_TX:
				return relay.failure(Failure.MBO_TX);
			default:
				return false;
		}
	}

	// Enables or disables the given failure.
	//
	// Parameter state determines the resulting state. True/false for
	// enabled/disabled.
	public void failure(Failure failure, boolean state)
	{
		switch (failure)
		{
			case ENGINE:
				engineFailure = state;
				break;
			case SERVICE_BRAKE:
				serviceBrakeFailure = state;
				break;
			case EMERGENCY_BRAKE:
				emergencyBrakeFailure = state;
				break;
			case TRACK_RX:
				relay.failure(Failure.TRACK_RX, state);
				break;
			case BEACON_RX:
				relay.failure(Failure.BEACON_RX, state);
				break;
			case MBO_RX:
				relay.failure(Failure.MBO_RX, state);
				break;
			case MBO_TX:
				relay.failure(Failure.MBO_TX, state);
				break;
			default:
				break;
		}
	}

	// Toggles the state of the given failure between enabled/disabled.
	public void toggleFailure(Failure failure)
	{
		failure(failure, !failure(failure));
	}

	// Determines if we should notify observers this update. If so, notifes
	// Observer objects.
	private void notifyActions()
	{
		if (notifyObserversCount == 0)
		{
			setChanged();
			notifyObservers();
		}

		notifyObserversCount =
			(notifyObserversCount + 1) % NOTIFY_OBSERVERS_MOD;
	}

	// Maps door location to index number in doors[].
	private static int doorLocationToIndex(DoorLocation location)
	{
		int value = -1;

		switch (location)
		{
			case  left: value = 0; break;
			case right: value = 1; break;
		}

		return value;
	}
}
