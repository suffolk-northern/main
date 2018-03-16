package train_controller;

import java.util.*;
import java.lang.Math;

import train_model.communication.ControllerLink;
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
	double Ki = 0.5;
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
	double updateTime = 0;
	boolean returning = false;


	public TrainController(ControllerLink link) 
	{
		this.link = link;

		// Before train recieves first command, make everything safe
		manualMode = false;
		speedCMD = 0;
		currAuth = 0;
		driverSetSpeed = 0;
		link.power(0);
		link.serviceBrake(1.0);
		manualBrake = false;

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
			link.serviceBrake(1.0);
			manualBrake = true;
		}
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
		return link.speed();
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
	private int updateStationDistance() 
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
		distanceFromLastStation += 1760 * displacement() / 3600;
	if(returning)
		return -1 * stationCounter + 1;
	return -1 * stationCounter;	
	}

	// Calculates train displacement since last update
	private double displacement()
	{
		double avgSpeed = (link.speed() + lastSpeed) / 2;
		return updateTime * avgSpeed;
	}

	// Updates authority states
	private void authority()
	{
		if (link.receiveFromTrack() == null)
			return;
		// New authority message
	}



	// millis is ignored as this is not a model module
	public void update(int millis) 
	{
		updateTime = millis;
		lastSpeed = link.speed();

		// Update authority
		authority();

		// Set desired speed
		if(manualMode)
			setSpeed = driverSetSpeed;
		else
			setSpeed = speedCMD;
	
		setSpeed = 20;
		// Obtain error and average error
		currentSpeed = link.speed();
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

		// Compute power and brake commands
		powerCMD = Kp*error*currentSpeed*mass;
		powerCMD += Ki*averageError*currentSpeed*mass;
		powerCMD /= MAXPOWER;
		if(powerCMD > 1)
			powerCMD = 1;
		brakePowerConv = Math.min( 1.0, -1 * powerCMD*MAXPOWER / (mass * currentSpeed * MAX_SDECEL) );
		if(!manualBrake)
		{
			link.power( Math.max(0.0, powerCMD) );		
			link.serviceBrake( Math.max(0.0, brakePowerConv) );
		}
	//	System.out.println("\t\tpow: " + powerCMD);
	//	System.out.println("\t\t\t\tbrake: " + brakePowerConv);
	//	System.out.println("\t\t\t\t\t\tspeed: " + link.speed());

		// Update stations to display on map
		int d = updateStationDistance();
		if(distanceFromLastStation<20)
			if(returning)
				stationsToDisplay = -1*d+1;
			else
				stationsToDisplay = -1*d;
		else
			stationsToDisplay = d;

	}

}

