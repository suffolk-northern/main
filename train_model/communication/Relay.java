/*
 * Ryan Matthews
 *
 * Intra-module communication
 */

package train_model.communication;

import track_model.GlobalCoordinates;
import train_model.Failure;
import train_model.TrainModel;
import train_model.communication.BeaconMessage;
import train_model.communication.MboMovementCommand;
import train_model.communication.TrackMovementCommand;

// Message forwarder between communication interfaces
//
// NOT used outside the train model!
//
// NOT thread-safe!

public class Relay
{
	private final TrainModel train;

	// most recent incoming message from beacon, MBO, track circuit
	private BeaconMessage beaconMessageLast = new BeaconMessage("none");
	private MboMovementCommand mboMessageLast =
		new MboMovementCommand(0, 0);
	private TrackMovementCommand trackMessageLast =
		new TrackMovementCommand(0, 0);

	// like most recent, but set to null after read
	private BeaconMessage beaconMessageCached = null;
	private   MboMovementCommand    mboMessageCached = null;
	private TrackMovementCommand  trackMessageCached = null;

	private boolean failureTrackRx = false;
	private boolean failureBeaconRx = false;
	private boolean failureMboTx = false;
	private boolean failureMboRx = false;

	// Constructs a Relay associated that interacts with a TrainModel.
	public Relay(TrainModel train)
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
	public void onRXMbo(MboMovementCommand message)
	{
		mboMessageCached = new MboMovementCommand(message);
		mboMessageLast   = new MboMovementCommand(message);
	}

	// Triggers actions for a message received from the track circuit.
	public void onRXTrack(TrackMovementCommand message)
	{
		trackMessageCached = new TrackMovementCommand(message);
		trackMessageLast   = new TrackMovementCommand(message);
	}

	// Returns the current location as should be reported to MBO.
	//
	// Returns 100ms old information because of simulated delay.
	//
	// Returns null if no information is available.
	public GlobalCoordinates location()
	{
		if (failureMboTx)
			return null;

		return train.location();
	}

	// If a message was received from a beacon since last call, returns
	// that message. Else returns null.
	public BeaconMessage beaconMessage()
	{
		if (failureBeaconRx)
			return null;

		BeaconMessage value = beaconMessageCached;
		beaconMessageCached = null;
		return value;
	}

	// If a message was received from the MBO since last call, returns that
	// message. Else returns null.
	public MboMovementCommand mboMessage()
	{
		if (failureMboRx)
			return null;

		MboMovementCommand value = mboMessageCached;
		mboMessageCached = null;
		return value;
	}

	// If a message was received from a track circuit since last call,
	// returns that message. Else returns null.
	public TrackMovementCommand trackMessage()
	{
		if (failureTrackRx)
			return null;

		TrackMovementCommand value = trackMessageCached;
		trackMessageCached = null;
		return value;
	}

	// Returns the last received beacon message.
	public BeaconMessage lastBeaconMessage()
	{
		return new BeaconMessage(beaconMessageLast);
	}

	// Returns the last received MBO message.
	public MboMovementCommand lastMboMessage()
	{
		return new MboMovementCommand(mboMessageLast);
	}

	// Returns the last received track message.
	public TrackMovementCommand lastTrackMessage()
	{
		return new TrackMovementCommand(trackMessageLast);
	}

	// Returns true if the given failure type has been triggered.
	public boolean failure(Failure failure)
	{
		switch (failure)
		{
			case TRACK_RX:
				return failureTrackRx;
			case BEACON_RX:
				return failureBeaconRx;
			case MBO_RX:
				return failureMboTx;
			case MBO_TX:
				return failureMboRx;
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
			case TRACK_RX:
				failureTrackRx = state;
				break;
			case BEACON_RX:
				failureBeaconRx = state;
				break;
			case MBO_RX:
				failureMboTx = state;
				break;
			case MBO_TX:
				failureMboRx = state;
				break;
			default:
				break;
		}
	}
}
