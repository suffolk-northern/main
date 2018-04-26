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
        Vitality vital;
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
	double Kp = 0.98; 		// Default values
	double Ki = 1 - Kp;


	int driverSetSpeed;
	double speedCMD;		// ctc/mbo cmd
	double setSpeed;	
	double powerCMD;		// to link

	boolean manualBrake;
        boolean emergencyBrake;
        boolean passengerEBrakeRequest;
	double brakeCMD;		// to link

	int currAuth;			// ctc/mbo cmd
	double movingAuth = 0.0;
        double totalMovingAuth = 0.0;
        boolean stationGap = false;
        boolean afterStation = false;
        boolean mboMode;
        double distFromLastBeacon;
        
        TrainControllerUI gui;
	ArrayList<String> ads = new ArrayList<>();
        int adsCounter = 0;
        double adsTimeCounter = 0;
        TreeMap<String, String> stations = new TreeMap<>();
        boolean allowKSet = true;
        boolean allowDoors = true;
        boolean dwelling;
        int dwellBurstCyclesCount = 0;
        int MIN_DWELL = 10;             // min seconds to dwell
        boolean leftSide = false;
        int trainID;
        String approachingStation;
        boolean firstBeacon = true;
        boolean passingTypeApproach = false;
        int passingTypeCount = 0;
        
        boolean eBrakeFailure = false;
        boolean sBrakeFailure = false;
        boolean engineFailure = false;
        boolean signalFailure = false;
        
	public TrainController(int id) 
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
                passengerEBrakeRequest = false;
                trainID = id;
                approachingStation = new String();
                
		ads.add("Come to Pitt, the #1 public university in northeast Oakland");
		ads.add("Broken frisbee? Get a new one today! Visit getnewfrisbee.com");
		ads.add("Got blisters on your feet from playing frisbee? Visit betterfrisbeecleats.com");
		ads.add("Do your teamates make fun of you because you can't catch a frisbee? Visit frisbeelessons.com");
		ads.add("Can't hold a conversation without bringing up frisbee? Visit frisbeefreaksanonymous.com");
                
                stations.put("g2", "PIONEER");
                stations.put("g9", "EDGEBROOK");
                stations.put("g16", "BABEL");
                stations.put("g22", "WHITED");
                stations.put("g31", "SOUTH BANK");
                stations.put("g39", "CENTRAL");
                stations.put("g48", "INGLEWOOD");
                stations.put("g57", "OVERBROOK");
                stations.put("g65", "GLENBURY");
                stations.put("g73", "DORMONT");
                stations.put("g77", "MT LEBANON");
                stations.put("g88", "POPLAR");
                stations.put("g96", "CASTLE SHANNON");
                stations.put("g105", "DORMONT");
                stations.put("g114", "GLENBURY");
                stations.put("g123", "OVERBROOK");
                stations.put("g132", "INGLEWOOD");
                stations.put("g141", "CENTRAL");
                stations.put("r7", "SHADYSIDE");
                stations.put("r16", "HERRON AVE");
                stations.put("r21", "SWISSVILLE");
                stations.put("r25", "PENN STATION");
                stations.put("r35", "STEEL PLAZA");
                stations.put("r45", "FIRST AVE");
                stations.put("r48", "STATION SQUARE");
                stations.put("r60", "SOUTH HILLS JUNCTION");        
        }

	public void registerTrain(ControllerLink link)
	{
		this.link = link;
		link.power(0);
		link.serviceBrake(1.0);
                gui = new TrainControllerUI(this);
                // Connect Control class here
                vital = new Vitality(this);
	}
        
        public ControllerLink getLink()
        {
            return link;
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

        public double[] getKs()
        {
            double [] ks = {Kp, Ki};
            return ks;
        }
        
	public void setBrakesEngaged(boolean command)
	{
		manualBrake = command;
	}
        
        public void setEmergencyBrake(boolean cmd)
        {
            emergencyBrake = cmd;
            if(emergencyBrake)
                link.applyEmergencyBrake();
            else
                link.releaseEmergencyBrake();
        }
        
        public boolean[] getFailures()
        {
            boolean [] failures = {true, true, true, true};
            if(!eBrakeFailure)
                failures[0] = false;
            if(!sBrakeFailure)
                failures[1] = false;
            if(!engineFailure)
                failures[2] = false;
            if(!signalFailure)
                failures[3] = false;
            return failures;
        }
        
        public String getStationText()
        {
            System.out.println(approachingStation);
            //String s = "Passing " + approachingStation + "\n";
            String s = "";
            if(stationGap)
                s = "Arriving at " + approachingStation + "\n";
            if(approachingStation.equals("") | afterStation)
                s = "";
            return s;
        }
        
        private void failureDetector()
        {
            eBrakeFailure = false;
            sBrakeFailure = false;
            engineFailure = false;
            signalFailure = false;         
            if(!link.emergencyBrake() & emergencyBrake)
                eBrakeFailure = true;
            if(link.serviceBrake() == 38101.77 & brakeCMD != 1)
                sBrakeFailure = true;
            if(link.power() == 0 & powerCMD != 0)
                engineFailure = true;
        }

	public void setDriverSpeed(int command)
	{
		driverSetSpeed = command;
	}

	public void setManualMode(boolean command)
	{
		manualMode = command;
	}
        
        public boolean getManualMode()
        {
                return manualMode;
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
			link.openDoor(DoorLocation.left);
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
			link.openDoor(DoorLocation.right);
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
            if(leftDoorsCMD & !rightDoorsCMD)
                return 2;
            if(leftDoorsCMD & rightDoorsCMD)
                return 3;
            return -1;
        }
        
        private void advertise(int millis)
        {
            if(adsCounter == ads.size())
                adsCounter = 0;           
            link.advertisement(ads.get(adsCounter));
            if(adsTimeCounter >= 60)
            {
                adsTimeCounter = 0;
                adsCounter++;
            }
            adsTimeCounter += (double)millis/1000;
        }
        
        private void passengerActions()
        {
            if(link.receivedEmergencyBrakeRequest())
            {
                passengerEBrakeRequest = true;
                emergencyBrake = true;
                link.applyEmergencyBrake();
            }
        }
        
        public boolean eBrakeState()
        {
            return link.emergencyBrake();
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
                System.out.println(approachingStation); ///////// delete
                double disp = displacement(millis);
		movingAuth -= disp;
                totalMovingAuth -= disp;
                distFromLastBeacon += disp;
               // System.out.println("\t\tafterStation: " + afterStation);
                if (beaconMsg!=null & !afterStation & (distFromLastBeacon > 5 | firstBeacon) )
                {
                    firstBeacon = false;
                   // System.out.println("beaconRecevived!");
                    bm = beaconMsg.string.split(",");
                    if((currAuth == 0 | currAuth == (int)Double.parseDouble(bm[0])) & !mboMode)
                    {
                        currAuth = (int)Double.parseDouble(bm[0]);
                        movingAuth = currAuth;
                        stationGap = true;
                    }
                    if( mboMode & Math.abs(movingAuth - Double.parseDouble(bm[0])) < 10 )
                    {
                        stationGap = true;
                    }
                    approachingStation = stations.get(bm[2]);
                    if(bm[1].equals("-1"))
                        leftSide = true;
                    else
                        leftSide = false;
                }
                if(beaconMsg!=null & afterStation & distFromLastBeacon > 5)
                {
                    afterStation = false;
                    approachingStation = "";
                }
                if(beaconMsg!=null)
                {
                    distFromLastBeacon = 0;
                }
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
                            approachingStation = "";
                        }
                    }
                    stationGap = false;
		}
	}

        private void updateHeater()
        {
            if(getTemp() >= temperatureCMD + 1 & link.heater())
                link.heaterOff();
            else if(getTemp() < temperatureCMD - 1 & !link.heater())
                link.heaterOn();
        }
        
	// millis is ignored as this is not a model module
	// All units are meters and seconds
	public void update(int millis) 
	{            
                advertise(millis);
                updateHeater();
                passengerActions();
                failureDetector();

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

                // Calculate power and braking vitally
                double[] cmds = vital.decision(setSpeed);
                powerCMD = cmds[0];
                brakeCMD = cmds[1];
                
		// If driver braking in manual mode, or if authority about to expire
		if((manualBrake && manualMode) || movingAuth - 4 <= Math.pow(currentSpeed,2) / (2 * MAX_SDECEL))
		{
			brakeCMD = 1;
			powerCMD = 0;
		}
                if(emergencyBrake | eBrakeFailure | dwelling)
                {
                    powerCMD = 0;
                    brakeCMD = 1;
                }
                if(sBrakeFailure)
                {
                    powerCMD = 0;
                }
		link.power(powerCMD);		
		link.serviceBrake(brakeCMD);
	}
}