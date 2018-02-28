/*
 * Ryan Matthews
 *
 * Simulation
 */

package train_model;

import java.util.Observable;

import track_model.GlobalCoordinates;
import track_model.Orientation;
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

public class Train
	extends Observable
	implements Updateable
{
	// watts, newtons
	private static double MAX_ENGINE_POWER = 1.0;
	private static double MAX_ENGINE_FORCE = 3.0;

	// newtons
	private static double MAX_SERVICE_BRAKE_FORCE = 1.0;
	private static double   EMERGENCY_BRAKE_FORCE = 1.0;

	// watts
	private static double HEATER_POWER  = 1.0;
	private static double COOLING_POWER = 0.5;

	private static Pose initialPose = new Pose(
		new GlobalCoordinates(0.0, 0.0),
		Orientation.degrees(0.0)
	);

	// kilograms (constant for now)
	private static final double mass = 1.0;

	// notify observers once every this many updates
	private static int NOTIFY_OBSERVERS_MOD = 2;

	private final PointMass pointMass = new PointMass(mass, initialPose);

	// watts
	private double enginePower = 0.0;

	// newtons
	private double serviceBrakeForce = 0.0;
	private double emergencyBrakeForce = 0.0;

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
	public void update()
	{
		double engineForce = pointMass.forceForPower(enginePower);

		// BS force at zero velocity
		if (engineForce == 0.0)
			engineForce = 0.1;

		if (engineForce > MAX_ENGINE_FORCE)
			engineForce = MAX_ENGINE_FORCE;

		double netForce = engineForce
		                  - serviceBrakeForce
		                  - emergencyBrakeForce;

		pointMass.push(netForce, 100);

		double netPower = heaterPower - COOLING_POWER;

		pointHeat.conduct(netPower, 100);

		notifyActions();
	}

	// Returns the current location.
	//
	// Delayed by 100 ms.
	public GlobalCoordinates location()
	{
		return new GlobalCoordinates(pointMass.pose().position);
	}

	// Returns the current orientation.
	public Orientation orientation()
	{
		return pointMass.orientation();
	}

	// Sets the orientation.
	//
	// Causes an instantaneous change in the direction of the velocity
	// vector, i.e., "turns" the train without changing its speed.
	public void orientation(Orientation value)
	{
		pointMass.orientation(value);
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
	public void power(double value)
		throws IllegalArgumentException
	{
		if (value < 0.0 || value > 1.0)
			throw new IllegalArgumentException("power range");

		value *= MAX_ENGINE_POWER;

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

	// Returns true if specified door(s) are open.
	public boolean door(DoorLocation location)
	{
		int index = doorLocationToIndex(location);

		return doors[index];
	}

	// Opens specified door(s).
	//
	// No failure modes. Always succeeds.
	public void openDoor(DoorLocation location)
	{
		int index = doorLocationToIndex(location);

		doors[index] = true;
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
