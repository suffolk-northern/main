package train_controller;

import java.util.*;
import java.lang.Math;

import train_model.communication.ControllerLink;
import train_model.communication.TrackMovementCommand;
import train_model.communication.MboMovementCommand;
import updater.Updateable;


public class TrainController implements Updateable
{

	ControllerLink link;

	boolean manualMode;		// States of commands from controller GUI
	boolean lightsCMD;
	boolean rightDoorsCMD;
	boolean leftDoorsCMD;
	boolean heaterOnCMD;
	int driverSetSpeed;

	double currentSpeed;		// Model states

	int speedCMD;			// Commands from track
	int currAuth;

	double MAXSPEED = 19.4;		// Controller's own vars
	double MAXPOWER = 120000;
	double MAX_SDECEL = 1.2;
	double mass = 50*907.185;
	double powerCMD;
	double brakeCMD;	
	double brakePowerConv = 1;
	int setSpeed;
	double error;
	double timeConstant = 5;
	double Kp = 2.5 / timeConstant;
	double Ki = 0.25;
	double [] queue = new double[5000];
	int queueInsert = 0;
	int queueFill = 0;
	double averageError;
	TreeMap<Integer, Double> stations = new TreeMap<>();
	double distanceFromLastStation = 0.0;
	int stationCounter = 1;
	int stationsToDisplay = 1;
	boolean manualBrake;
	double movingAuth = 0.0;
	double lastAuth = 0.0;
	double lastSpeed = 0.0;
	boolean returning = false;
	boolean disable = false;
	ArrayList<String> ads = new ArrayList<>();


	public TrainController() 
	{
		// Before train recieves first command, make everything safe
		manualMode = true;
		speedCMD = 0;
		currAuth = 0;
		driverSetSpeed = 0;
		manualBrake = true;

		// Relative station distances
		stations.put(1, 240.0);
		stations.put(2, 590.0);
		stations.put(3, 1400.0);
		stations.put(4, 440.0);
		stations.put(5, 620.0);
		stations.put(6, 620.0);
		stations.put(7, 300.0);
		stations.put(8, 900.0);
		stations.put(9, 900.0);

		ads.add("Broken frisbee? Get a new one today! Visit getnewfrisbee.com");
		ads.add("Got blisters on your feet from playing frisbee? Visit frisbeecleats.com");
		ads.add("Do your teamates make fun of you because you can't catch a frisbee? Visit frisbeelessons.com");
		ads.add("Can't hold a conversation without bringing up frisbee? Visit frisbeefreaksanonymous.com");
	}

	public void registerTrain(ControllerLink link)
	{
		this.link = link;
		link.power(0);
		link.serviceBrake(1.0);
	}

	public void launchGUI()
	{
		TrainControllerUI gui = new TrainControllerUI(this);
		gui.setVisible(true);
	}

	// Setters/getters for driver GUI

	public boolean getBrakesEngaged()
	{
		if(brakePowerConv > 0)
			return true;
		return false;
	}

	public void setBrakesEngaged(boolean command)
	{
		if(manualMode)
		{
			if(command)
			{
				link.serviceBrake(1.0);
				link.power(0);
			}
		}
		manualBrake = command;
	}

	public void setDriverSpeed(int command)
	{
		driverSetSpeed = command;
	}

	public void setManualMode(boolean command)
	{
		manualMode = command;
	}

	public void setLights(boolean command) 
	{
		if(command)
			link.lightsOn();
		else
			link.lightsOff();
	}

	public boolean getLights() 
	{
		return link.lights();
	}

	public void setHeater(boolean command)
	{
		if(command)
			link.heaterOn();
		else
			link.heaterOff();
	}

	public double getTemp() 
	{
		return link.temperature();
	}

	public double getPowerKW() 
	{
		return MAXPOWER * powerCMD / 1000;
	}

	public double getSpeed()
	{
		return link.speed() * 2.23694;
	}

	public int getCTCspeed()
	{
		return speedCMD;
	}

	public double getCTCauth()
	{
		return currAuth;
	}

	public double getMovingAuth()
	{
		return movingAuth;
	}

	public int getStationsToDisplay()
	{
		return stationsToDisplay;
	}


	// Distance from last station
	private int updateStationDistance(int millis) 
	{
	int correction = 0;
	if (stationCounter == 9) 
	{
		returning = true;
	}
	if (stationCounter <= 1) 
	{
		if (stationCounter < 1)
			stationCounter = 2;
		returning = false;
	}
	if (returning)
		correction = 1;
	if ( distanceFromLastStation > stations.get(stationCounter - correction) )
	{
		distanceFromLastStation = 0.0;
		if (returning)
			return stationCounter--;
		return stationCounter++;
	}
	else
		distanceFromLastStation += 1760 * displacement(millis) / 3600;
	if(returning)
		return -1 * stationCounter + 1;
	return -1 * stationCounter;	
	}

	// Calculates train displacement since last update
	private double displacement(int millis)
	{
		double avgSpeed = (link.speed() + lastSpeed)*2.23694 / 2;
		return (double)millis/1000 * avgSpeed;
	}

	// Updates authority states
	private void authority(int millis)
	{
		TrackMovementCommand ctcMsg = link.receiveFromTrack();
		MboMovementCommand mboMsg = link.receiveFromMbo();
		if (mboMsg!=null)
		{
			speedCMD = mboMsg.speed;
			currAuth = mboMsg.authority;
			movingAuth = currAuth;
		}
		if (ctcMsg!=null)
		{
			speedCMD = ctcMsg.speed;
			currAuth = ctcMsg.authority;
			movingAuth = currAuth;
		}
		movingAuth -= displacement(millis);
		if(movingAuth<0)
			movingAuth = 0;
	}


	// millis is ignored as this is not a model module
	public void update(int millis) 
	{
		lastSpeed = link.speed();

		// Update authority
		authority(millis);

		// Set desired speed
		if(manualMode)
		{
			setSpeed = driverSetSpeed;
			movingAuth = currAuth; // authority never expires
		}
		else
			setSpeed = speedCMD;

		// If authority is about to expire
		currentSpeed = link.speed() * 2.23694; // convert to mph
		disable = false;
		if(movingAuth<=30)
		{
			setSpeed = 0;
			link.serviceBrake(Math.max(1, 1 - movingAuth/20));
			powerCMD = -1*mass*(1-movingAuth/20)*MAX_SDECEL*currentSpeed*0.44704/MAXPOWER;
			disable = true;
		}

		// Obtain error and average error
		if(currentSpeed == 0)
			currentSpeed = 0.01;
		error = (double)setSpeed - currentSpeed;
		queue[queueInsert++] = error;
		if(queueInsert%queue.length == 0)
			queueInsert = 0;
		averageError = 0;
		for(int i=0; i<queueFill; i++)
		{
			averageError += queue[i] / queueFill;
		}
		if(++queueFill > queue.length)
			queueFill = queue.length;

		// Compute power and brake commands. Default power cmd => max braking
		powerCMD = -1*mass*currentSpeed*0.44704*MAX_SDECEL/MAXPOWER;
		if(!manualBrake & !disable)
		{
			powerCMD = Kp*error*currentSpeed*mass;
			powerCMD += Ki*averageError*currentSpeed*mass;
			powerCMD /= MAXPOWER;
			if(powerCMD > 1)
				powerCMD = 1;
			brakePowerConv = Math.min( 1.0, -1 * powerCMD*MAXPOWER / (mass * currentSpeed * 0.44704 * MAX_SDECEL) );
			link.power( Math.max(0.0, powerCMD) );		
			link.serviceBrake( Math.max(0.0, brakePowerConv) );
		}

		// Update stations to display on map
		int d = updateStationDistance(millis);
		if(distanceFromLastStation<20)
			if(returning)
				stationsToDisplay = -1*d+1;
			else
				stationsToDisplay = -1*d;
		else
			stationsToDisplay = d;

	}

}

