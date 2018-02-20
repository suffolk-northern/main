import track_model.GlobalCoordinates;
import track_model.Orientation;
import train_model.PointMass;
import train_model.Pose;

public class TestPointMass
{
	public static void main(String[] args)
	{
		GlobalCoordinates origin = new GlobalCoordinates(0.0, 0.0);

		Pose initialPose = new Pose(origin, Orientation.degrees(0.0));

		PointMass rock1kg  = new PointMass(1.0, initialPose);
		PointMass rock2kg  = new PointMass(2.0, initialPose);

		rock1kg.push(1.0, 1000);
		rock2kg.push(1.0, 1000);

		System.out.printf(
			"After pushing with 1N at 0.0 degrees for 1s:\n" +
			"  1kg rock:\n" +
			"    displacement %.2f yards %.1f degrees\n" +
			"    speed %.1fm/s\n" +
			"  2kg rock:\n" +
			"    displacement %.2f yards %.1f degrees\n" +
			"    speed %.1fm/s\n",
			origin.distanceTo(rock1kg.pose().position),
			origin.directionTo(rock1kg.pose().position).degrees(),
			rock1kg.speed(),
			origin.distanceTo(rock2kg.pose().position),
			origin.directionTo(rock2kg.pose().position).degrees(),
			rock2kg.speed()
		);

		System.out.println();

		Orientation east = Orientation.degrees(90.0);

		rock1kg.orientation(east);
		rock2kg.orientation(east);

		rock1kg.push(1.0, 1000);
		rock2kg.push(1.0, 1000);

		System.out.printf(
			"Then pushing at 90.0 degrees for 1s:\n" +
			"  1kg rock:\n" +
			"    displacement %.2f yards %.1f degrees\n" +
			"    speed %.1fm/s\n" +
			"  2kg rock:\n" +
			"    displacement %.2f yards %.1f degrees\n" +
			"    speed %.1fm/s\n",
			origin.distanceTo(rock1kg.pose().position),
			origin.directionTo(rock1kg.pose().position).degrees(),
			rock1kg.speed(),
			origin.distanceTo(rock2kg.pose().position),
			origin.directionTo(rock2kg.pose().position).degrees(),
			rock2kg.speed()
		);
	}
}
