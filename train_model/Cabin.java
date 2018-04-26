/*
 * Ryan Matthews
 *
 * Simulation
 */

package train_model;

// Container of passengers
//
// Handles passenger accounting and functionality that affects them.

public class Cabin
{
	private static final int KILOGRAMS_PER_PASSENGER = 68;
	private static final int SEATS = 400;

	private int crew = 0;
	private int civilians = 0;

	private int driverId = -1;

	// Returns the total mass of all passengers and crew.
	public int mass()
	{
		return KILOGRAMS_PER_PASSENGER * (crew + civilians);
	}

	// Returns the number of passengers aboard.
	//
	// Does not include crew.
	public int passengers()
	{
		return civilians;
	}

	// Returns the number of crew members aboard.
	public int crew()
	{
		return crew;
	}

	// Returns the number of available seats.
	//
	// Crew members do not use seats.
	public int free()
	{
		return SEATS - civilians;
	}

	// Increases the number of passengers by the given count.
	//
	// Throws IllegalStateException if capacity is exceeded.
	public void loadPassengers(int count)
	{
		if (civilians + count > SEATS)
			throw new IllegalStateException("capacity exceeded");

		civilians += count;
	}

	// Decreases the number of passengers by a random count.
	//
	// Returns the number of passengers unloaded.
	public int unloadPassengers()
	{
		int count = (int) (civilians * Math.random());

		civilians -= count;

		return count;
	}

	// Returns true if a driver is onboard.
	public boolean hasDriver()
	{
		return crew != 0;
	}

	// Returns the ID of the driver onboard.
	//
	// Returns -1 if no driver is onboard.
	public int driverId()
	{
		if (crew == 0)
			return -1;

		return driverId;
	}

	// Loads the driver onto the train.
	public void loadDriver(int id)
	{
		crew = 1;
		driverId = id;
	}

	// Unloads the driver from the train.
	//
	// Returns the ID of the driver that was on train.
	public int unloadDriver()
	{
		crew = 0;
		return driverId;
	}
}
