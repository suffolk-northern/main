/*
 * Ryan Matthews
 *
 * Inter-module communication
 */

package train_model.communication;

// Information that moves from beacon to train antenna

public class BeaconMessage
{
	// max legal length of beacon string
	//
	// 16 ASCII chars is 16 bytes * 8 bits per byte = 128 bits
	public static final int MAX_LENGTH = 16;

	public String string;

	// Constructs a BeaconMessage as a copy of another.
	public BeaconMessage(BeaconMessage other)
	{
		this.string = other.string;
	}

	// Initializes all members.
	//
	// Throws IllegalArgumentException if any member initializer is
	// invalid.
	public BeaconMessage(String string)
		throws IllegalArgumentException
	{
		if (string.length() == 0 || string.length() > MAX_LENGTH)
			throw new IllegalArgumentException("string length");

		this.string = string;
	}
}
