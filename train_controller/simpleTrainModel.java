package my.firstProject;

import java.io.*;
import java.util.*;

public class simpleTrainModel {
	private boolean lightsOn;
	private boolean doorsOpen = false;
	private boolean brakesEngaged;
	private Double velocity;
	private Double CTCspeed; 
	private Double CTCauth;
	private Double mass = 37000.0;
	private Double timeIncrement = 0.05;
	private Double maxBraking = 0.35; // mph per timeIncrement seconds
	private Double maxAccel = 0.2; // mph per timeIncrement seconds

	private int counter = 0; // Only needed for demo purposes
	Random rand = new Random();


	public simpleTrainModel() {
		// Initial safe states
		doorsOpen = true;
		lightsOn = false;
		velocity = 0.0;
		brakesEngaged = true;
		CTCspeed = 50.0;
		CTCauth = 100.0;
	}
	
	public void setDoors(boolean command) {	
		doorsOpen = command;
	}

	public boolean getDoors() {
		return doorsOpen;
	}

	public void setLights(boolean command) {
		lightsOn = command;
	}

	public boolean getLights() {
		return lightsOn;
	}

	public void setBrakes(boolean command) {
		brakesEngaged = command;
	}

	public boolean getBrakes() {
		return brakesEngaged;
	}

	public Double getVelocity() {
		return velocity;
	}

	public Double getCTCspeed() {
		return CTCspeed;
	}

	public Double getCTCauth() {
		return CTCauth;
	}

	public Double getMass() {
		return mass;
	}

	public Double getTimeIncrement() {
		return timeIncrement;
	}

	public Double getMaxBraking() {
		return maxBraking;
	}

	public void setPowerCommand(Double powerCommand) {
		Double deltaV;
		if(velocity==0) {
			velocity = 0.01;
		}
		if(brakesEngaged==true && velocity>0) {
			deltaV = 0.0;
			if (velocity<maxBraking)
				velocity = 0.0;
			else
				velocity -= maxBraking;
		}
		else {
			deltaV = powerCommand*timeIncrement/(velocity*mass);
			if(deltaV>maxAccel)
				deltaV = maxAccel;
			if(deltaV<-1*maxBraking)
				deltaV = -1*maxBraking;
		}
				
		velocity += deltaV;
		//System.out.println("" + brakesEngaged);
	}

	public void update() 
	{
		if (counter%250==0)
			CTCspeed = Double.valueOf(5*rand.nextInt(8));
		if (counter%180==0)
			CTCauth = 70.0 + Double.valueOf(10*rand.nextInt(34));
		counter++;
	}
}
