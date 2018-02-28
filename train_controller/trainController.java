package my.firstProject;

import java.io.*;
import java.util.*;
import java.lang.Math;

public class trainController {

	simpleTrainModel Model;
	boolean manualMode;
	Double setPointSpeed = 0.0;
	Double currAuth;
	Double lastAuth;
	Double movingAuth;
	Double driverSetSpeed;
	Double kW = 0.0;
	boolean driverHardBrakes;
	Double Kp = 1.0;
	Double Ki = 40000.0;
	Double queue[] = new Double[500];

	Double distanceFromLastStation;
	TreeMap<Integer, Double> stations = new TreeMap<>();
	int stationCounter;
	int stationsToDisplay;
	boolean returning = false;
	
	public trainController() {
		Model = new simpleTrainModel();
		manualMode = true;
		driverSetSpeed = 0.0;
		currAuth = 0.0;
		lastAuth = 0.0;
		movingAuth = 0.0;
		driverHardBrakes = true;
		distanceFromLastStation = 0.0;
		stations.put(1, 24.0);
		stations.put(2, 59.0);
		stations.put(3, 140.0);
		stations.put(4, 44.0);
		stations.put(5, 62.0);
		stations.put(6, 62.0);
		stations.put(7, 30.0);
		stations.put(8, 90.0);
		stations.put(9, 90.0);
		stationCounter = 1;
		stationsToDisplay = 1;

		for(int i=0; i<queue.length; i++) {
			queue[i]=0.0;
		}


	}


	public void setDriverSetSpeed(Double command) {
		driverSetSpeed = command;
	}

	public void setManualMode(boolean command) {
		manualMode = command;
	}

	public boolean getLights() {
		return Model.getLights();
	}

	public boolean getDoors() {
		return Model.getDoors();
	}

	public boolean getBrakesEngaged() {
		return Model.getBrakes();
	}

	public void setBrakesEngaged(boolean command) {
		Model.setBrakes(command);
		driverHardBrakes = command;
	}

	public Double getSpeed() {
		return Model.getVelocity();
	}

	public Double getCTCspeed() {
		return Model.getCTCspeed();
	}

	public Double getCTCauth() {
		return Model.getCTCauth();
	}

	public Double getMovingAuth() {
		return movingAuth;
	}

	public Double getPowerKW() {
		return kW;
	}

	public int getStationsToDisplay() {
		return stationsToDisplay;
	}

	private int updateStationDistance() {
		int correction = 0;
		if (stationCounter==9) {
			returning = true;
		}
		if (stationCounter<=1) {
			if (stationCounter<1)
				stationCounter = 2;
			returning = false;
		}
		if (returning)
			correction = 1;
		if ( distanceFromLastStation > stations.get(stationCounter - correction) ) {
			distanceFromLastStation = 0.0;
			if (returning)
				return stationCounter--;
			return stationCounter++;
		}
		else
			distanceFromLastStation += 1760*Model.getVelocity()*Model.getTimeIncrement()/3600;
		if(returning)
			return -1*stationCounter+1;
		return -1*stationCounter;
		
	}

	private void printArr (Double [] arr) {
		for(int i=0; i<arr.length; i++) {
			if(arr[i]!=null) 
				System.out.print("" + arr[i] + ", ");
		}
		System.out.println();
	}

	public void update() { // Returns station(s) to display on map
		Model.update();

		// Update queue to include X most recent speeds
		Double sum = 0.0;
		//int numSamples = queue.length;
		for(int i=0; i<queue.length-1; i++) {
			queue[queue.length-1-i] = queue[queue.length-2-i];
			sum += queue[queue.length-2-i];
		}
		queue[0] = setPointSpeed - Model.getVelocity();
		printArr(queue);
		sum += queue[0];
//		System.out.println("\t" + sum);
//		System.out.println("\t\t" + numSamples);
//		System.out.println("\t\t\t" + queue.length);

		// Update stations to display on map
		int d = updateStationDistance();
		if(distanceFromLastStation<20)
			if(returning)
				stationsToDisplay = -1*d+1;
			else
				stationsToDisplay = -1*d;
		else
			stationsToDisplay = d;
	//	System.out.println(d);
	//	System.out.println("\t\t\t" + stationCounter);
	//	System.out.println("\t" + returning);


		// Update authority and distance
		lastAuth = currAuth;
		currAuth = Model.getCTCauth();
		if (lastAuth!=currAuth) 
			movingAuth = currAuth;
		else {
			movingAuth -= 1760*Model.getVelocity()*Model.getTimeIncrement()/3600;
		}

		// Set max braking if authority is almost expired
		if(movingAuth<50) 
			Model.setBrakes(true);
		else if(!driverHardBrakes)
			Model.setBrakes(false);


		// Update power command
		if (manualMode)
			setPointSpeed = driverSetSpeed;
		else
			setPointSpeed = getCTCspeed();

		Double timeConstant = 5.0;	

		Double error = setPointSpeed - Model.getVelocity();
		Double powerCommand = Kp*error*Model.getVelocity()*Model.getMass()/timeConstant; // Proportional term
		Double averageError = sum/queue.length;
		System.out.println(averageError);
		powerCommand += Ki*averageError; // Integral term

		if(driverHardBrakes) {
			powerCommand = -1*Model.getMaxBraking()*Model.getVelocity()*Model.getMass()/Model.getTimeIncrement(); // Craft power to drive velocity down by maxBraking per timeIncrement
		}


		kW = powerCommand*Math.pow(1609.34,2)/(1000*Math.pow(3600,2)); // 1 mile = 1609.34 meters

		Model.setPowerCommand(powerCommand);
	}

}
