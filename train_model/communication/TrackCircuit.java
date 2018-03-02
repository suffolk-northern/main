/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

import train_model.communication.TrackMovementCommand;
import train_model.communication.Relay;

// Train-Track communication simulation
//
// From the POV of the track
//
// Is associated with a TrainModel.
//
// Is stateless, so any number of instances may be used.

public class TrackCircuit
{
	private final Relay relay;

	// Constructs a TrackCircuit that interacts with a Relay.
	public TrackCircuit(Relay relay)
	{
		this.relay = relay;
	}

	// Sends a message to the associated train.
	public void send(TrackMovementCommand message)
	{
		relay.onRXTrack(message);
	}
}
