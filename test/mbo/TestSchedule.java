package test.mbo;

import java.sql.Time;
import java.util.ArrayList;

import mbo.schedules.*;
import mbo.MboSchedulerUI;
/**
 *
 * @author Kaylene Stocking
 */
public class TestSchedule {
	public static void main(String[] args)
	{
		Time driverT = new Time(5, 40, 0);
		DriverEvent.EventType de = DriverEvent.EventType.EMBARK;
		int trainID = 1;
		DriverEvent driverE = new DriverEvent(driverT, de, 1);
		ArrayList<DriverEvent> driverEvents = new ArrayList<DriverEvent>();
		driverEvents.add(driverE);
		DriverSchedule dSched = new DriverSchedule(1, driverEvents);
		ArrayList<DriverSchedule> driverSchedules = new ArrayList<DriverSchedule>();
		driverSchedules.add(dSched);
		
		Time trainT = new Time(5, 40, 0);
		TrainEvent.EventType te = TrainEvent.EventType.DEPARTURE;
		TrainEvent trainE = new TrainEvent(trainT, te, "Yard");
		ArrayList<TrainEvent> trainEvents = new ArrayList<TrainEvent>();
		trainEvents.add(trainE);
		TrainSchedule tSched = new TrainSchedule(1, trainEvents);
		ArrayList<TrainSchedule> trainSchedules = new ArrayList<TrainSchedule>();
		trainSchedules.add(tSched);
		
		LineSchedule ls = new LineSchedule(driverSchedules, trainSchedules);
		
		MboSchedulerUI ui = new MboSchedulerUI("Green");
		ui.setSchedule(ls);
		ui.setVisible(true);
	}
}
