package train_controller;

import java.util.*;
import java.lang.Math;

import train_model.communication.ControllerLink;
import train_model.communication.TrackMovementCommand;
import train_model.communication.MboMovementCommand;
import train_model.communication.BeaconMessage;
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
        int temperatureCMD = 70;

	double currentSpeed;
	double lastSpeed = 0.0; 	// to calc displacement


	double MAXSPEED = 19.4;	
	double MAXPOWER = 120000;
	double MAX_SDECEL = 1.2;
	double mass = 50*907.185;
	double error;
	double timeConstant = 5;
	double Kp = 0.98; 		// Default values
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
        boolean emergencyBrake;
	double brakeCMD;		// to link
	double loopBrake;
        double startBrake;

	int currAuth;			// ctc/mbo cmd
	double movingAuth = 0.0;
        double totalMovingAuth = 0.0;
        boolean stationGap = false;
        boolean afterStation = false;
        boolean mboMode;
        double distFromLastBeacon;
        
        TrainControllerUI gui;
	ArrayList<String> ads = new ArrayList<>();
        boolean allowKSet = true;
        boolean allowDoors = true;
        boolean dwelling;
        int dwellBurstCyclesCount = 0;
        int MIN_DWELL = 10;             // min seconds to dwell
        boolean leftSide = false;
        
	public TrainController() 
	{
		// Before train recieves first command, make everything safe
		manualMode = true;
		speedCMD = 0;
		currAuth = 0;
		driverSetSpeed = 0;
		manualBrake = true;
                emergencyBrake = false;
                currentSpeed = 0;
                powerCMD = 0;
                

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
                gui = new TrainControllerUI(this);
	}

	public void launchGUI()
	{
		gui.setVisible(true);
	}
        
	// Setters/getters for driver GUI

	public void setKs(double p, double i)
	{
		Kp = p;
		Ki = i;
	}

	public void setBrakesEngaged(boolean command)
	{
		manualBrake = command;
	}
        
        public void setEmergencyBrake()
        {
            emergencyBrake = true;
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
		if(cmd && currentSpeed == 0)
                {
		//	link.openDoor(DoorLocation.left);
                        leftDoorsCMD = true;
                }
		else
                {
			link.closeDoor(DoorLocation.left);
                        leftDoorsCMD = false;
                }
        }
        
        public void setRightDoors(boolean cmd)
        {
		if(cmd && currentSpeed == 0)
                {
		//	link.openDoor(DoorLocation.right);
                        rightDoorsCMD = true;
                }
		else
                {
			link.closeDoor(DoorLocation.right);
                        rightDoorsCMD = false;
                }
        }
        
        public void setTemp(int temp)
        {
            temperatureCMD = temp;
        }

	public double getTemp() 
	{
		return link.temperature() * 9/5 + 32;   // convert C to F
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
        
        public boolean getAllowKSet()
        {
            return allowKSet;
        }

        public boolean getAllowDoors()
        {
            return allowDoors;
        }
        
        public int getDoorStates()
        {
            if(!leftDoorsCMD & !rightDoorsCMD)
                return 0;
            if(!leftDoorsCMD & rightDoorsCMD)
                return 1;
            if(leftDoorsCMD & ! rightDoorsCMD)
                return 2;
            if(leftDoorsCMD & rightDoorsCMD)
                return 3;
            return -1;
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
                BeaconMessage beaconMsg = link.receiveFromBeacons();
                String[] bm;
		if (ctcMsg!=null & !stationGap)
		{
                    currAuth = ctcMsg.authority;
                    speedCMD = (double)ctcMsg.speed * 0.277778; // convert kph to mps
                    movingAuth = currAuth;
                    mboMode = false;
		}
		if (mboMsg!=null)
		{
                    currAuth = mboMsg.authority;
                    speedCMD = (double)mboMsg.speed * 0.277778; // convert kph to mps
                    movingAuth = currAuth;
                    mboMode = true;
		}
                double disp = displacement(millis);
		movingAuth -= disp;
                totalMovingAuth -= disp;
                distFromLastBeacon += disp;
                
                if(beaconMsg!=null)
                {
                    distFromLastBeacon = 0;
                }
                
                if (beaconMsg!=null & !afterStation & !mboMode)
                {
                    bm = beaconMsg.string.split(",");
                    if(currAuth == 0 | currAuth == (int)Double.parseDouble(bm[0]))
                    {
                        currAuth = (int)Double.parseDouble(bm[0]);
                        movingAuth = currAuth;
                        stationGap = true;
                    }
                    if(bm[1].equals("-1"))
                        leftSide = true;
                    else
                        leftSide = false;
                }
                if (beaconMsg!=null & afterStation & distFromLastBeacon < 5);
                    afterStation = false;
		if((movingAuth < 5 && currentSpeed == 0) | dwelling)
		{
                    // Train stopped for auth reasons
                    if(stationGap | dwelling)
                    {
                        afterStation = true;
                        // Wait min dwell time
                        if(dwellBurstCyclesCount++ * (double)millis/1000 < MIN_DWELL)
                        {
                            dwelling = true;
                            // Open appropriate Doors;
                            setLeftDoors(leftSide);
                            setRightDoors(!leftSide);
                            leftDoorsCMD = leftSide;
                            rightDoorsCMD = !leftSide;
                        }
                        else
                        {
                            dwelling = false;
                            dwellBurstCyclesCount = 0;
                            // Close appropriate doors
                            setLeftDoors(false);
                            setRightDoors(false);
                            leftDoorsCMD = false;
                            rightDoorsCMD = false;
                        }
                    }
                    stationGap = false;
		}
	}
        
        public void updateHeater()
        {
            if(getTemp() >= temperatureCMD & link.heater())
                link.heaterOff();
            else if(getTemp() < temperatureCMD & !link.heater())
                link.heaterOn();
        }
        
	// millis is ignored as this is not a model module
	// All units are meters and seconds
	public void update(int millis) 
	{            
                updateHeater();
                
                if(currentSpeed>0)
                {
                    allowKSet = false;
                    allowDoors = true;
                }
                else
                    allowDoors = false;
                
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
		if((manualBrake && manualMode) || movingAuth - 4 <= Math.pow(currentSpeed,2) / (2 * MAX_SDECEL))
		{
			brakeCMD = 1;
			powerCMD = 0;
		}
                if(emergencyBrake)
                {
                    link.power(0);
                    link.applyEmergencyBrake();
                    return;
                }
                if(dwelling)
                {
                    link.power(0);
                    link.serviceBrake(1);
                    return;
                }
		link.power(powerCMD);		
		link.serviceBrake(brakeCMD);
		//////////////////////////////////////////
	}
}