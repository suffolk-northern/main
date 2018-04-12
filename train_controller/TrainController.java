package train_controller;

import java.util.*;
import java.lang.Math;

import train_model.communication.ControllerLink;
import train_model.communication.TrackMovementCommand;
import train_model.communication.MboMovementCommand;
import train_model.DoorLocation;
import updater.Updateable;



public class TrainController implements Updateable
{

	ControllerLink link;

	boolean manualMode;		// States of commands from controller GUI
	boolean lightsCMD;
	boolean rightDoorsCMD;
	boolean leftDoorsCMD;
	boolean heaterOnCMD;

	double currentSpeed;
	double lastSpeed = 0.0; 	// to calc displacement


	double MAXSPEED = 19.4;	
	double MAXPOWER = 120000;
	double MAX_SDECEL = 1.2;
	double mass = 50*907.185;
	double error;
	double timeConstant = 5;
	double Kp = 0.98; 		// Range: 0 -> 0.5
	double Ki = 1 - Kp;
	double [] queue = new double[5000];
	int queueInsert = 0;
	int queueFill = 0;
	double averageError;

	int driverSetSpeed;
	double speedCMD;		// ctc/mbo cmd
	double setSpeed;	
	double powerCMD;		// to link
	double loopPower;

	boolean manualBrake;
	double brakeCMD;		// to link
	double loopBrake;
        double startBrake;

	int currAuth;			// ctc/mbo cmd
	double movingAuth = 0.0;

	ArrayList<String> ads = new ArrayList<>();

	public TrainController() 
	{
		// Before train recieves first command, make everything safe
		manualMode = true;
		speedCMD = 0;
		currAuth = 0;
		driverSetSpeed = 0;
		manualBrake = true;

		ads.add("Come to Pitt, the #1 public university in northeast Oakland");
		ads.add("Broken frisbee? Get a new one today! Visit getnewfrisbee.com");
		ads.add("Got blisters on your feet from playing frisbee? Visit betterfrisbeecleats.com");
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

	public void setKs(double p)
	{
		Kp = p;
		Ki = 1 - p;
	}

	public void setBrakesEngaged(boolean command)
	{
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
        
        public void setLeftDoors(boolean cmd)
        {
		if(cmd)
			link.openDoor(DoorLocation.left);
		else
			link.closeDoor(DoorLocation.left);
        }
        
        public void setRightDoors(boolean cmd)
        {
		if(cmd)
			link.openDoor(DoorLocation.right);
		else
			link.closeDoor(DoorLocation.right);
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
		double b = mass * brakeCMD * MAX_SDECEL * currentSpeed / 1000;
		double p = MAXPOWER * powerCMD / 1000;
		if(b > p)
		{
			assert p == 0;
			return -1 * b;
		}
		assert b == 0;
		return p;
	}

	public double getSpeed()
	{
		return link.speed() * 2.23694;	 // convert mps to mph
	}

	public double getCTCspeed()
	{
		return speedCMD * 2.2369;	// convert mps to mph
	}

	public double getCTCauth()
	{
		return currAuth * 1.09361;	 // convert m to yd
	}

	public double getMovingAuth()
	{
		return movingAuth * 1.09361;	 // convert m to yd
	}

	// Calculates train displacement since last update
	private double displacement(int millis)
	{
		double avgSpeed = (link.speed() + lastSpeed) / 2;
		return (double)millis/1000 * avgSpeed;
	}

	// Updates authority states
	private void authority(int millis)
	{
		TrackMovementCommand ctcMsg = link.receiveFromTrack();
		MboMovementCommand mboMsg = link.receiveFromMbo();
		if (mboMsg!=null)
		{
			speedCMD = (double)mboMsg.speed * 0.277778; // convert kph to mps
			currAuth = mboMsg.authority;
			movingAuth = currAuth;
		}
		if (ctcMsg!=null)
		{
			speedCMD = (double)ctcMsg.speed *  0.277778; // convert kph to mps
			currAuth = ctcMsg.authority;
			movingAuth = currAuth;
		}
		movingAuth -= displacement(millis);
		if(movingAuth < 0)
		{
			// TODO flash console light or something
		}
	}

	// millis is ignored as this is not a model module
	// All units are meters and seconds
	public void update(int millis) 
	{
		lastSpeed = currentSpeed;
		currentSpeed = link.speed();

		// Update authority
		authority(millis);

		// Set desired speed
		if(manualMode)
                {
			setSpeed = (double)driverSetSpeed * 0.44704;	// mph -> mps
                        if(setSpeed > speedCMD)
                            setSpeed = speedCMD;
                }
		else
			setSpeed = speedCMD;

		// Obtain error and average error
		error = setSpeed - currentSpeed;
		queue[queueInsert++] = error;
		if(queueInsert%queue.length == 0)
			queueInsert = 0;
		averageError = 0;
		for(int i=0; i < queueFill; i++)
		{
			averageError += queue[i] / queueFill;
		}
		if(++queueFill > queue.length)
			queueFill = queue.length;

		////  C O N T R O L    L O O P  /////////
		loopPower = Kp * error * currentSpeed * mass;
		loopPower += Ki * averageError * currentSpeed * mass;
		loopPower /= MAXPOWER;
		if(currentSpeed == 0)
			loopPower = 0.01;
		powerCMD = loopPower;
		if(powerCMD > 1)
			powerCMD = 1;
		if(powerCMD < 0)
			powerCMD = 0;
		loopBrake = -1 * loopPower * MAXPOWER / (mass * currentSpeed * MAX_SDECEL);
		brakeCMD = loopBrake;
		if(brakeCMD > 1)
			brakeCMD = 1;
		if(brakeCMD < 0)
			brakeCMD = 0;
		// If driver braking in manual mode, or if authority about to expire
		if((manualBrake && manualMode) || movingAuth - 0.1 <= Math.pow(currentSpeed,2) / (2 * MAX_SDECEL))
		{
			brakeCMD = 1;
			powerCMD = 0;
		}
		link.power(powerCMD);		
		link.serviceBrake(brakeCMD);
		//////////////////////////////////////////
	}
}
