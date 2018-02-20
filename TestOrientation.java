import track_model.Orientation;

public class TestOrientation
{
	public static void main(String[] args)
	{
		Orientation east = Orientation.degrees(90.0);

		System.out.printf(
			"East is %.0f degrees and %.2f radians\n",
			east.degrees(),
			east.radians()
		);

		System.out.printf(
			"90 degrees right of east is %.0f degrees, south\n",
			east.addDegrees(90.0).degrees()
		);

		System.out.printf(
			"90 degrees + 300 degrees = %.0f degrees\n",
			east.addDegrees(300.0).degrees()
		);

		System.out.printf(
			"90 degrees - 180 degrees = %.0f degrees\n",
			east.subtractDegrees(180.0).degrees()
		);

		System.out.printf(
			"90 degrees - Math.PI radians = %.0f degrees\n",
			east.subtractRadians(Math.PI).degrees()
		);
	}
}
