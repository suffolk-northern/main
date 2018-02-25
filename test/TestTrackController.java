/*
 * Ryan Matthews
 *
 * Basic demonstration of track controller interface
 */

package test;

import track_controller.TrackController;
import track_controller.communication.Authority;
import track_controller.communication.Branch;
import track_controller.communication.ControllerLink;
import track_controller.communication.CtcLink;
import track_controller.communication.Program;
import track_controller.communication.Speed;

// Map:
//
//
//                                            = = =
//                                           /
//                   = = = - = = = = - switch
//                  /                        \
//  = = = = - switch                          = =
//                  \
//
//  <--- controller 1 --->   <--- controller 2 --->
//

public class TestTrackController
{
	public static void main(String[] args)
	{
		TrackController controller1 = new TrackController();
		TrackController controller2 = new TrackController();

		controller1.linkToController(
			Branch.left,
			controller2, Branch.common
		);

		loadProgram1(controller1);
		loadProgram2(controller2);

		CtcLink ctcLink1 = controller1.ctcLink();
		CtcLink ctcLink2 = controller2.ctcLink();

		sendAuthority(ctcLink1, ctcLink2);
		sendSpeed(ctcLink1, ctcLink2);
	}

	// Simulates loading the program for controller 1.
	private static void loadProgram1(TrackController controller)
	{
		Program program = new Program(
			Program.run(4),
			Program.run(3),
			Program.run(0)
		);

		controller.loadProgram(program);
	}

	// Simulates loading the program for controller 2.
	private static void loadProgram2(TrackController controller)
	{
		Program program = new Program(
			Program.run(4),
			Program.run(3),
			Program.run(2)
		);

		controller.loadProgram(program);
	}

	// Simulates sending the following Authority from the CTC:
	//
	//                                            1 0 0
	//                                           /
	//                   1 1 1 - 1 1 1 1 - switch
	//                  /                        \
	//  0 0 1 1 - switch                          0 0
	//                  \
	private static void sendAuthority(CtcLink link1, CtcLink link2)
	{
		link1.authority(Branch.common, new Authority(
			new boolean[] {  true,  true, false, false }
		));

		link1.authority(Branch.left,   new Authority(
			new boolean[] {  true,  true,  true }
		));

		link2.authority(Branch.common, new Authority(
			new boolean[] {  true,  true,  true,  true }
		));

		link2.authority(Branch.left,   new Authority(
			new boolean[] {  true, false, false }
		));

		link2.authority(Branch.right,  new Authority(
			new boolean[] { false, false }
		));
	}

	// Simulates sending the following speeds from the CTC:
	//
	//                                                       50 50 50
	//                                                      /
	//                       15 20 25 - 30 35 40 45 - switch
	//                      /                               \
	//   0  0  5 10 - switch                                 45 45
	//                      \
	private static void sendSpeed(CtcLink link1, CtcLink link2)
	{
		link1.speeds(Branch.common, new Speed[]{
			new Speed(Speed.run(2)),
			new Speed(Speed.run(1)),
			new Speed(Speed.run(0)),
			new Speed(Speed.run(0))
		});

		link1.speeds(Branch.left,   new Speed[]{
			new Speed(Speed.run(3)),
			new Speed(Speed.run(4)),
			new Speed(Speed.run(5))
		});

		link2.speeds(Branch.common, new Speed[]{
			new Speed(Speed.run(9)),
			new Speed(Speed.run(8)),
			new Speed(Speed.run(7)),
			new Speed(Speed.run(6))
		});

		link2.speeds(Branch.left,   new Speed[]{
			new Speed(Speed.run(10)),
			new Speed(Speed.run(10)),
			new Speed(Speed.run(10))
		});

		link2.speeds(Branch.right,  new Speed[]{
			new Speed(Speed.run(9)),
			new Speed(Speed.run(9))
		});
	}
}
