/*
 * Ryan Matthews
 *
 * Simulation
 */

package train_model;

// Physics object with thermal energy
//
// Has a "heat inertia".
//
// Maintains temperature.

public class PointHeat
{
	// Joule per celsius
	private final double heatInertia = 10.0;

	private double temperature = 20.0;

	// Returns the current temperature.
	//
	// Units: Celsius
	public double temperature()
	{
		return temperature;
	}

	// Simulates heating/cooling with a constant power for an amount of
	// time.
	//
	// Updates temperature.
	//
	// Does the math and returns immediately, i.e., does not sleep.
	//
	// Parameter power is Watts transferred in. Can be negative.
	//
	// Parameter time is milliseconds.
	public void conduct(double power, int time)
	{
		// joules
		double energy = power * time / 1000.0;

		temperature += energy / heatInertia;
	}
}
