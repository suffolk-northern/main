/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

import train_model.DoorLocation;
import train_model.Train;
import train_model.communication.MovementCommand;

// Train model-controller communication simulation
//
// From the POV of the controller
//
// Is associated with a Train.
//
// Operating on multiple instances is well-defined (but not thread-safe).
//
// Receiving from multiple instances has undefined behavior.

public class ControllerLink
{
	private final Relay relay;
	private final Train train;

	// Constructs a TrackCircuit that interacts with a Relay and its Train.
	public ControllerLink(Relay relay, Train train)
	{
		this.relay = relay;
		this.train = train;
	}

	// Receives a message from any beacon.
	//
	// If no new message is available since last call, returns null.
	//
	// A message is never received from more than one beacon at once. If
	// multiple messages are received between calls, the latest overwrites
	// previous messages.
	public BeaconMessage receiveFromBeacons()
	{
		return relay.beaconMessage();
	}

	// Receives a message from the MBO.
	//
	// If no new message is available since last call, returns null.
	public MovementCommand receiveFromMbo()
	{
		return relay.mboMessage();
	}

	// Receives a message from the track circuit.
	//
	// If no new message is available since last call, returns null.
	//
	// A message is never received from more than one track circuit at
	// once. If multiple messages are received between calls, the latest
	// overwrites previous messages.
	public MovementCommand receiveFromTrack()
	{
		return relay.trackMessage();
	}

	// Returns the current speedometer reading.
	//
	// Units: meters per second
	public double speed()
	{
		return train.speed();
	}

	// Sets the engine output power.
	//
	// Units: Proportion of max output power, e.g.,
	//
	//   0.5 = 50% output power
	//
	// Failure mode: Engine output is zero regardless of input.
	public void power(double value)
	{
		train.power(value);
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
	{
		train.serviceBrake(value);
	}

	// Engages the emergency brake.
	//
	// Irreversible. Damages the vehicle.
	//
	// Failure mode: Emergency brake applies no force regardless of input.
	public void applyEmergencyBrake()
	{
		train.applyEmergencyBrake();
	}

	// Opens specified door(s).
	//
	// No failure modes. Always succeeds.
	public void openDoor(DoorLocation location)
	{
		train.openDoor(location);
	}

	// Closes specified door(s).
	//
	// No failure modes. Always succeeds.
	public void closeDoor(DoorLocation location)
	{
		train.closeDoor(location);
	}

	// Returns the current temperature.
	//
	// Units: celsius
	public double temperature()
	{
		return train.temperature();
	}

	// Returns true if the heater is on.
	public boolean heater()
	{
		return train.heater();
	}

	// Turns the heater on.
	public void heaterOn()
	{
		train.heaterOn();
	}

	// Turns the heater off.
	public void heaterOff()
	{
		train.heaterOff();
	}
}
