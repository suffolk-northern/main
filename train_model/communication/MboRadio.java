/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

import track_model.GlobalCoordinates;
import train_model.communication.MovementCommand;
import train_model.communication.Relay;

// Train-MBO wireless communication simulation
//
// From the POV of the MBO
//
// Is associated with a Train.
//
// Sending from multiple instances is well-defined (but not thread-safe).
//
// Receiving from multiple instances has undefined behavior.

public class MboRadio
{
	private final Relay relay;

	// Constructs an MboRadio that interacts with a Relay.
	public MboRadio(Relay relay)
	{
		this.relay = relay;
	}

	// Sends a message to the associated train.
	public void send(MovementCommand message)
	{
		relay.onRXMbo(message);
	}

	// Receives a message from the associated train.
	//
	// Returns 100ms old information because of simulated delay.
	//
	// Returns null if no information is available.
	public GlobalCoordinates receive()
	{
		return relay.location();
	}
}
