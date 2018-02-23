/*
 * Ryan Matthews
 *
 * Basic demonstration of GlobalCoordinates.
 */

package test;

import track_model.GlobalCoordinates;

public class TestCoordinates
{
	// degrees per arc minute
	private static final double minute = 1.0 / 60.0;

	public static void main(String[] args)
	{
		stretching();
		System.out.println();
		addition();
		System.out.println();
		direction();
	}

	// Demonstrates longitudinal stretching away from equator.
	public static void stretching()
	{
		// near equator
		GlobalCoordinates x00 = new GlobalCoordinates(0, 0);
		GlobalCoordinates y00 = new GlobalCoordinates(0 + minute, 0);
		GlobalCoordinates z00 = new GlobalCoordinates(0, minute);

		// near Pittsburgh's latitude
		GlobalCoordinates x40 = new GlobalCoordinates(40, 0);
		GlobalCoordinates y40 = new GlobalCoordinates(40 + minute, 0);
		GlobalCoordinates z40 = new GlobalCoordinates(40, minute);

		System.out.printf(
			"Near equator:\n" +
			"  1' north = %.0f yards\n" +
			"  1' east  = %.0f yards\n" +
			"\n" +
			"Near Pittsburgh:\n" +
			"  1' north = %.0f yards\n" +
			"  1' east  = %.0f yards\n",
			x00.distanceTo(y00),
			x00.distanceTo(z00),
			x40.distanceTo(y40),
			x40.distanceTo(z40)
		);
	}

	// Demonstrates addition/subtraction.
	public static void addition()
	{
		GlobalCoordinates x = new GlobalCoordinates(1, 2);
		GlobalCoordinates y = new GlobalCoordinates(3, 4);

		GlobalCoordinates a = x.add(y);
		GlobalCoordinates b = x.subtract(y);
		GlobalCoordinates c = x.addDegrees(5, 6);
		GlobalCoordinates d = x.subtractDegrees(7, 8);

		System.out.printf(
			"(+1, +2) + (+3, +4) = (%+.0f, %+.0f)\n" +
			"(+1, +2) - (+3, +4) = (%+.0f, %+.0f)\n" +
			"(+1, +2) + (+5, +6) = (%+.0f, %+.0f)\n" +
			"(+1, +2) - (+7, +8) = (%+.0f, %+.0f)\n",
			a.latitude(), a.longitude(),
			b.latitude(), b.longitude(),
			c.latitude(), c.longitude(),
			d.latitude(), d.longitude()
		);
	}

	// Demonstrates direction calculation.
	public static void direction()
	{
		GlobalCoordinates origin = new GlobalCoordinates( 0,  0);
		GlobalCoordinates north  = new GlobalCoordinates( 1,  0);
		GlobalCoordinates south  = new GlobalCoordinates(-1,  0);
		GlobalCoordinates east   = new GlobalCoordinates( 0,  1);
		GlobalCoordinates west   = new GlobalCoordinates( 0, -1);

		System.out.printf(
			"north is %5.1f degrees\n" +
			"south is %5.1f degrees\n" +
			"east  is %5.1f degrees\n" +
			"west  is %5.1f degrees\n",
			origin.directionTo(north).degrees(),
			origin.directionTo(south).degrees(),
			origin.directionTo(east ).degrees(),
			origin.directionTo(west ).degrees()
		);
	}
}
