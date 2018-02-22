/*
 * Ryan Matthews
 *
 * Intra-module communication
 */

package train_model.communication;

import track_model.GlobalCoordinates;
import train_model.Train;
import train_model.communication.BeaconMessage;
import train_model.communication.MovementCommand;

// Message forwarder between communication interfaces
//
// NOT used outside the train model!
//
// NOT thread-safe!

public class Relay
{
	private final Train train;

	// most recent incoming message from beacon, MBO, track circuit
	private   BeaconMessage beaconMessageLast = new BeaconMessage("none");
	private MovementCommand    mboMessageLast = new MovementCommand(0, 0);
	private MovementCommand  trackMessageLast = new MovementCommand(0, 0);

	// like most recent, but set to null after read
	private   BeaconMessage beaconMessageCached = null;
	private MovementCommand    mboMessageCached = null;
	private MovementCommand  trackMessageCached = null;

	// Constructs a Relay associated that interacts with a Train.
	public Relay(Train train)
	{
		this.train = train;
	}

	// Triggers actions for a message received from a beacon.
	public void onRXBeacon(BeaconMessage message)
	{
		beaconMessageCached = new BeaconMessage(message);
		beaconMessageLast   = new BeaconMessage(message);
	}

	// Triggers actions for a message received from the MBO.
	public void onRXMbo(MovementCommand message)
	{
		mboMessageCached = new MovementCommand(message);
		mboMessageLast   = new MovementCommand(message);
	}

	// Triggers actions for a message received from the track circuit.
	public void onRXTrack(MovementCommand message)
	{
		trackMessageCached = new MovementCommand(message);
		trackMessageLast   = new MovementCommand(message);
	}

	// Returns the current location as should be reported to MBO.
	//
	// Returns 100ms old information because of simulated delay.
	//
	// Returns null if no information is available.
	public GlobalCoordinates location()
	{
		return train.location();
	}

	// If a message was received from a beacon since last call, returns
	// that message. Else returns null.
	public BeaconMessage beaconMessage()
	{
		BeaconMessage value = beaconMessageCached;
		beaconMessageCached = null;
		return value;
	}

	// If a message was received from the MBO since last call, returns that
	// message. Else returns null.
	public MovementCommand mboMessage()
	{
		MovementCommand value = mboMessageCached;
		mboMessageCached = null;
		return value;
	}

	// If a message was received from a track circuit since last call,
	// returns that message. Else returns null.
	public MovementCommand trackMessage()
	{
		MovementCommand value = trackMessageCached;
		trackMessageCached = null;
		return value;
	}

	// Returns the last received beacon message.
	public BeaconMessage lastBeaconMessage()
	{
		return new BeaconMessage(beaconMessageLast);
	}

	// Returns the last received MBO message.
	public MovementCommand lastMboMessage()
	{
		return new MovementCommand(mboMessageLast);
	}

	// Returns the last received track message.
	public MovementCommand lastTrackMessage()
	{
		return new MovementCommand(trackMessageLast);
	}
}
