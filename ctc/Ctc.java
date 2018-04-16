/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctc;

/**
 *
 * @author missm
 */
import java.util.ArrayDeque;
import java.util.Date;
import java.util.StringTokenizer;

import updater.Updateable;
import track_model.TrackModel;
import track_model.TrackBlock;
import track_model.Station;
import train_model.communication.TrackMovementCommand;
import updater.*;
import mbo.CtcRadio;

/**
 *
 * @author missm
 */
public class Ctc implements Updateable{

	public static final int TRAINCOLS = 8;
	public static final int TRACKCOLS = 9;

	public static Ctc ctc;
	public static CtcUI ui;
	
	public static TrackModel trackmodel;
	public static CtcRadio greenradio;
	public static CtcRadio redradio;

	public static ArrayDeque<Train> trains = new ArrayDeque<Train>();
	public static ArrayDeque<Block> blueline = new ArrayDeque<Block>();
	public static ArrayDeque<Block> greenline = new ArrayDeque<Block>();
	public static ArrayDeque<Block> redline = new ArrayDeque<Block>();
	public static ArrayDeque<Block> switches = new ArrayDeque<Block>();
	public static ArrayDeque<Block> stations = new ArrayDeque<Block>();
	public static ArrayDeque<TrackCon> trackcons = new ArrayDeque<TrackCon>();
	
	public static ArrayDeque<Train> dispatched = new ArrayDeque<Train>();

	public static Updater updater;
	public static int period;
	public static Clock clock;
	
	public static double through = 0;

	public static void showUI() {
		ui.showUI();
	}

	public void update(int time)
	{
		// ask track model for updates
		TrackBlock tb;
		
		toUpdate = new ArrayDeque<Block>();
		brokenBlocks = new ArrayDeque<Block>();
		
		for(Block block : greenline)
		{
			tb = trackmodel.getBlock(block.line, block.num);
			// update block properties
			updateBlock(block, tb);
		}
		
		ArrayDeque<Block> btemp = toUpdate.clone();
		
		for(Block b : btemp)
		{
			b.occupied = trackmodel.getBlock(b.line, b.num).isIsOccupied();
		}
		/*
		for(Block b : brokenBlocks)
		{
			b.broken = true;
		}
		*/
		btemp = toUpdate.clone();
		
		for(Block b : btemp)
		{
			updateAuth(b);
		}
		
		toUpdate = new ArrayDeque<Block>();
		brokenBlocks = new ArrayDeque<Block>();
		
		for(Block block : redline)
		{
			tb = trackmodel.getBlock(block.line, block.num);
			// update block properties
			updateBlock(block, tb);
			
		}
		
		/*
		for(Block b : brokenBlocks)
		{
			b.broken = true;
		}
		*/
		btemp = toUpdate.clone();
		
		for(Block b : btemp)
		{
			b.occupied = trackmodel.getBlock(b.line, b.num).isIsOccupied();
		}
		
		btemp = toUpdate.clone();
		
		for(Block b : btemp)
		{
			updateAuth(b);
		}
		
		clock.advance(time);
		String clockDisp = String.format("%02d", clock.time().getHours()) + ":" + String.format("%02d",clock.time().getMinutes());
		ui.updateClock(clockDisp);
		
		updateTrack();
		updateTrains();
		return;
	}
	
	private static ArrayDeque<Block> toUpdate;
	private static ArrayDeque<Block> brokenBlocks;
	
	private static void updateBlock(Block block, TrackBlock tb)
	{
		Train train = null;
		boolean oldOcc = block.occupied;
		boolean newOcc = false;
		
		if(block.num != 0)
			newOcc = tb.isIsOccupied();
		else
			newOcc = false;
		
		if(newOcc != oldOcc)
		{
			toUpdate.add(block);
			
			// do some train logic
			if(newOcc)
			{
				if(isForwardSwitch(block))
				{
					if(block.prev.occupied)
						train = getTrain(block.prev);
					if(train == null && block.sw_curr_to.occupied)
						train = getTrain(block.sw_curr_to);
					
					//System.out.println("forward switch");
				}
				else if(isBackwardSwitch(block))
				{
					if(block.sw_from.contains(getBlock(block.line,0)))
					{
						train = dispatched.poll();
						//System.out.println("yard");
					}
					else
					{
						if(block.next.occupied)
							train = getTrain(block.next);
						if(train == null && block.sw_curr_from.occupied)
							train = getTrain(block.sw_curr_from);
					}
					
					//System.out.println("backward switch");
				}
				else if(block.prev.num == 0)
				{
					train = dispatched.poll();
					//System.out.println("yard");
				}
				else
				{
					//System.out.println("Train moved from " + block.prev.line + " " + block.prev.num);
					if(block.prev.occupied)
						train = getTrain(block.prev);
					if(train == null && block.next.occupied)
						train = getTrain(block.next);

					//System.out.println("normal");
				}
				
				if(train != null)
				{
					//System.out.println(train.ID + " moved to " + block.display());
					train.setLoc(block);
				}
				else
				{
					//System.out.println("no train found");
					block.broken = true;
				}
			}
			else if(!newOcc && isForwardSwitch(block))
			{
				 if(block.sw_curr_to.equals(getBlock(block.line,0)))
				 {
					 train = getTrain(block);
					 train.setLoc(getBlock(block.line,0));
				 }
			}
		}
	}
	
	/*
    public MyCtc()
    {
        ctc = this;
        ui = new MyCtcUI(ctc);
    }
	 */
	//public static void main(String[] args)
	
	public void setUpdater(int per, Updater u)
	{
		period = per;
		updater = u;
	}
	
	protected void changeSpeedUp(int speedup)
	{
		updater.scheduleAtFixedRate(period / speedup);
	}
	
	public void setTrain(int ID)
	{
		Train train = new Train(ID,getBlock("green",0),0);
		trains.add(train);
		
		updateTrains();
	}
	
	public void setTrackModel(TrackModel tm)
	{
		this.trackmodel = tm;
		
		initGreen();
		//initRed();
		
		// add trains to yards
		//Train train = new Train(0,getBlock("green",0),0);
		//trains.add(train);
		
		updateTrack();
		updateTrains();
		
		/*
		for(Block blk : greenline)
		{
			System.out.println(blk.display());
			
			if(!isBackwardSwitch(blk))
				System.out.println("Prev " + blk.prev.display());
			else
			{
				System.out.println("Prev " + blk.sw_from.peekFirst().display());
				System.out.println("Prev " + blk.sw_from.peekLast().display());
			}
			
			if(!isForwardSwitch(blk))
				System.out.println("Next " + blk.next.display());
			else
			{
				System.out.println("Next " + blk.sw_to.peekFirst().display());
				System.out.println("Next " + blk.sw_to.peekLast().display());
			}
			
			System.out.print(blk.nextBlockDir + " " + blk.prevBlockDir);
			if(blk.sw)
				System.out.print(blk.switchDir);
			System.out.println();
		}
		
		*/
		
	}
	
	public void setCtcRadios(CtcRadio green, CtcRadio red)
	{
		this.greenradio = green;
		this.redradio = red;
	}
	
	private static void initGreen()
	{
		greenline = new ArrayDeque<Block>();
		String line = "Green";
		TrackBlock bl;
		Block block;
		int numbl = trackmodel.getBlockCount(line);
		
		//greenline.add(new Block(line,true));
		
		// read in all blocks
		for(int i = 0; i <= numbl; i++)
		{
			bl = trackmodel.getBlock(line, i);
			
			if(bl.isIsSwitch() && bl.isIsStation())
			{
				block = new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false,0,bl.getSwitchDirection(),null,null,null,null);
				block.station = trackmodel.getStation(line,bl.getBlock()).getName(); 
				block.hasStation = true;
				switches.add(block);
				stations.add(block);
				greenline.add(block);
			}
			else if(bl.isIsCrossing())
			{
				greenline.add(new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,true));
			}
			else if(bl.isIsStation())
			{
				block = new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false,true,trackmodel.getStation(line,bl.getBlock()).getName());
				greenline.add(block);
				stations.add(block);
			}
			else if(bl.isIsSwitch())
			{
				block = new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false,0,bl.getSwitchDirection(),null,null,null,null);
				switches.add(block);
				greenline.add(block);
			}
			else
			{
				 greenline.add(new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false));
			}
		}
		
		Block from;
		Block to;
		Block extra;
		ArrayDeque<Block> blockf;
		ArrayDeque<Block> blockt;
		
		// add connections
		for(Block blk : greenline)
		{
			bl = trackmodel.getBlock(line,blk.num);
			if(bl.isIsSwitch())
			{
				// next block aka forward switch
				if(bl.getSwitchDirection() > 0)
				{
					blockf = new ArrayDeque<Block>();
					blockt = new ArrayDeque<Block>();
					
					from = getBlock(line,bl.getPrevBlockId());
					to = getBlock(line,bl.getNextBlockId());
					extra = getBlock(line,bl.getSwitchBlockId());
					blockf.add(blk); 
					blockt.add(to);
					blockt.add(extra);
					
					blk.setSwitch(bl.getSwitchDirection(), blockf.clone(), blockt.clone());
					blk.prev = getBlock(line,bl.getPrevBlockId());
					
					if(to.equals(getBlock(to.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_to = to;
					}
					else if(extra.equals(getBlock(to.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_to = extra;						
					}
				}
				// prev block aka backward switch
				else
				{
					blockf = new ArrayDeque<Block>();
					blockt = new ArrayDeque<Block>();
					
					from = getBlock(line,bl.getPrevBlockId());
					to = getBlock(line,bl.getNextBlockId());
					extra = getBlock(line,bl.getSwitchBlockId());
					blockf.add(from); 
					blockf.add(extra);
					blockt.add(blk);
					
					blk.setSwitch(bl.getSwitchDirection(), blockf.clone(), blockt.clone());
					blk.next = getBlock(line,bl.getNextBlockId());
					
					blk.sw_curr_to = blk;
					
					if(from.equals(getBlock(from.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_from = from;
					}
					else if(extra.equals(getBlock(from.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_from = extra;						
					}
				}
			}
			else
			{
				blk.prev = getBlock(line,bl.getPrevBlockId());
				blk.next = getBlock(line,bl.getNextBlockId());
				
				if(blk.num == 0)
					blk.occupied = false;
			}
			
		}
		
		
	}
	
	private static void initRed()
	{
		redline = new ArrayDeque<Block>();
		String line = "Red";
		TrackBlock bl;
		Block block;
		int numbl = trackmodel.getBlockCount(line);
		
		//redline.add(new Block(line,true));
		
		// read in all blocks
		for(int i = 0; i <= numbl; i++)
		{
			bl = trackmodel.getBlock(line, i);
			
			if(bl.isIsCrossing())
			{
				redline.add(new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,true));
			}
			else if(bl.isIsStation())
			{
				redline.add(new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false,true,trackmodel.getStation(line,bl.getBlock()).getName()));
			}
			else if(bl.isIsSwitch())
			{
				block = new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false,0,bl.getSwitchDirection(),null,null,null,null);
				switches.add(block);
				redline.add(block);
			}
			else
			{
				 redline.add(new Block(line,bl.getSection(),bl.getBlock(),bl.getLength(),bl.getNextBlockDir(),bl.getPrevBlockDir(),null,null,false));
			}
		}
		
		Block from;
		Block to;
		Block extra;
		ArrayDeque<Block> blockf;
		ArrayDeque<Block> blockt;
		
		// add connections
		for(Block blk : redline)
		{
			bl = trackmodel.getBlock(line,blk.num);
			if(bl.isIsSwitch())
			{
				// next block aka forward switch
				if(bl.getSwitchDirection() > 0)
				{
					blockf = new ArrayDeque<Block>();
					blockt = new ArrayDeque<Block>();
					
					from = getBlock(line,bl.getPrevBlockId());
					to = getBlock(line,bl.getNextBlockId());
					extra = getBlock(line,bl.getSwitchBlockId());
					blockf.add(from); 
					blockt.add(to);
					blockt.add(extra);
					
					blk.setSwitch(bl.getSwitchDirection(), blockf.clone(), blockt.clone());
					blk.prev = getBlock(line,bl.getPrevBlockId());
					
					blk.sw_curr_from = blk;
					
					if(to.equals(getBlock(to.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_to = to;
					}
					else if(extra.equals(getBlock(to.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_to = extra;						
					}
					
				}
				// prev block aka backward switch
				else
				{
					blockf = new ArrayDeque<Block>();
					blockt = new ArrayDeque<Block>();
					
					from = getBlock(line,bl.getPrevBlockId());
					to = getBlock(line,bl.getNextBlockId());
					extra = getBlock(line,bl.getSwitchBlockId());
					blockf.add(from); 
					blockf.add(extra);
					blockt.add(to);
					
					blk.setSwitch(bl.getSwitchDirection(), blockf.clone(), blockt.clone());
					blk.next = getBlock(line,bl.getNextBlockId());
					
					blk.sw_curr_to = blk;
					
					if(from.equals(getBlock(from.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_from = from;
					}
					else if(extra.equals(getBlock(from.line,bl.getSwitchPosition())))
					{
						blk.sw_curr_from = extra;						
					}
				}
			}
			else
			{
				
				blk.prev = getBlock(line,bl.getPrevBlockId());
				blk.next = getBlock(line,bl.getNextBlockId());
			}
			
		}
	}
	
	public Ctc() {
		ctc = this;
		ui = new CtcUI(ctc);
		
		clock = new Clock();		
		
		
		/*
		blueline.add(new Block("blue", true));
		blueline.add(new Block("blue", 'A', 1, 100, null, null, false));
		blueline.add(new Block("blue", 'A', 2, 100, null, null, false, 1, null, null, null, null));
		blueline.add(new Block("blue", 'A', 3, 100, null, null, false, true, "Cathy"));
		blueline.add(new Block("blue", 'A', 4, 100, null, null, false));
		blueline.add(new Block("blue", 'A', 5, 100, null, null, true));
		blueline.add(new Block("blue", 'A', 6, 100, null, null, false, 2, null, null, null, null));
		blueline.add(new Block("blue", 'A', 7, 100, null, null, false));
		blueline.add(new Block("blue", 'A', 8, 100, null, null, false));

		Block block = getBlock("blue", '\0', 0);
		block.setNext(getBlock("blue", 'A', 1));
		block.setPrev(getBlock("blue", 'A', 8));

		block = getBlock("blue", 'A', 1);
		block.setNext(getBlock("blue", 'A', 2));
		block.setPrev(getBlock("blue", '\0', 0));

		block = getBlock("blue", 'A', 2);
		ArrayDeque<Block> from = new ArrayDeque<Block>();
		ArrayDeque<Block> to = new ArrayDeque<Block>();
		from.add(getBlock("blue", 'A', 1));
		from.add(getBlock("blue", 'A', 7));
		to.add(getBlock("blue", 'A', 2));
		block.setSwitch(1, from, to);
		block.setNext(getBlock("blue", 'A', 3));
		switches.add(block);

		block = getBlock("blue", 'A', 3);
		block.setNext(getBlock("blue", 'A', 4));
		block.setPrev(getBlock("blue", 'A', 2));

		block = getBlock("blue", 'A', 4);
		block.setNext(getBlock("blue", 'A', 5));
		block.setPrev(getBlock("blue", 'A', 3));

		block = getBlock("blue", 'A', 5);
		block.setNext(getBlock("blue", 'A', 6));
		block.setPrev(getBlock("blue", 'A', 4));

		block = getBlock("blue", 'A', 6);
		from = new ArrayDeque<Block>();
		to = new ArrayDeque<Block>();
		from.add(getBlock("blue", 'A', 6));
		to.add(getBlock("blue", 'A', 7));
		to.add(getBlock("blue", 'A', 8));
		block.setSwitch(2, from, to);
		block.setPrev(getBlock("blue", 'A', 5));
		switches.add(block);

		block = getBlock("blue", 'A', 7);
		block.setNext(getBlock("blue", 'A', 1));
		block.setPrev(getBlock("blue", 'A', 6));

		block = getBlock("blue", 'A', 8);
		block.setNext(getBlock("blue", '\0', 0));
		block.setPrev(getBlock("blue", 'A', 6));

		Train train = new Train(1, getBlock("blue", '\0', 0));
		trains.add(train);

		Train train2 = new Train(2, getBlock("blue", '\0', 0));
		trains.add(train2);

		*/

		
		


	}
	
	public static void enableMBO(String line)
	{
		if(line.equalsIgnoreCase("green"))
		{
			greenradio.enableMovingBlock();
			greenradio.showController();
		}
		else if(line.equalsIgnoreCase("red"))
		{
			redradio.enableMovingBlock();
			redradio.showController();
		}
	}
	
	public static void disableMBO(String line)
	{
		if(line.equalsIgnoreCase("green"))
		{
			greenradio.disableMovingBlock();
			greenradio.hideController();
		}
		else if(line.equalsIgnoreCase("red"))
		{
			redradio.disableMovingBlock();
			redradio.hideController();
		}
	}
	
	public static void autoMode(String line)
	{
		if(line.equalsIgnoreCase("green"))
		{
			
		}
		else if(line.equalsIgnoreCase("red"))
		{
			
		}
	}
	
	public static void manMode(String line)
	{
		if(line.equalsIgnoreCase("green"))
		{
			
		}
		else if(line.equalsIgnoreCase("red"))
		{
			
		}
	}

	private static Train getTrain(int ID) {
		ArrayDeque<Train> temp = trains.clone();
		Train mytrain = temp.poll();

		while (!temp.isEmpty() && mytrain.getID() != ID) {
			mytrain = temp.poll();
		}

		if (mytrain.getID() == ID) {
			return mytrain;
		} else {
			return null;
		}
	}

	protected boolean routeTrain(String ID, String l, String bl, double sp) {
		Train train = getTrain(Integer.parseInt(ID));

		char sec;
		int num;

		if (bl.equalsIgnoreCase("YARD")) {
			sec = '\0';
			num = 0;
		} else {
			sec = bl.charAt(0);
			num = Integer.parseInt(bl.substring(1));
		}

		Block block = getBlock(l, num);

		routeTrain(train, block, sp);

		return true;
	}

	private static void routeTrain(Train train, Block dest, double sp) {
		ArrayDeque<Block> route = findRoute(train, train.getLoc(), dest);

		//printRoute(route);

		train.setRoute(route);

		/*
		
		ArrayDeque<SwitchAndPos> swpos = getSwitches(route);

		printSwitchesOnRoute(swpos);

		ArrayDeque<SwitchAndPos> temp = swpos.clone();
		SwitchAndPos curr = null;
		Block f;
		Block t;
		int swID;
		Block block;
		while (!temp.isEmpty()) {
			curr = temp.poll();
			f = curr.getFrom();
			t = curr.getTo();
			swID = curr.getBlock().getSwID();
			block = curr.getBlock();
			//if (!block.getSwitchCurrFrom().equals(f) || !block.getSwitchCurrTo().equals(t)) {
				//setSwitch(block, f, t);
			//}

		}
		*/
		double auth = calcAuth(route, route.getFirst(), route.getLast());
		double speed = sp; // from ui

		train.setSpeed(speed);
		train.setAuth(auth);
		sendSpeedAuth(train, speed, auth);
		updateTrains();
	}

	private static void printSwitchesOnRoute(ArrayDeque<SwitchAndPos> swpos) {
		System.out.println("Switches");

		Block block;

		ArrayDeque<SwitchAndPos> temp2 = swpos.clone();

		if (temp2.isEmpty()) {
			System.out.println("none");
		}

		SwitchAndPos curr = null;
		int swID;
		Block f;
		Block t;
		while (!temp2.isEmpty()) {
			curr = temp2.poll();
			f = curr.getFrom();
			t = curr.getTo();
			swID = curr.getBlock().getSwID();

			System.out.println(f.display() + " " + t.display());
		}
	}

	private static void printRoute(ArrayDeque<Block> route) {
		System.out.println("Route");

		ArrayDeque<Block> temp = route.clone();
		while (!temp.isEmpty()) {
			System.out.println(temp.poll().display());
		}
	}

	private static boolean isForwardSwitch(Block block) {
		if (!block.hasSwitch()) {
			return false;
		}

		if (block.getSwitchFrom().contains(block)) {
			return true;
		}

		return false;
	}

	private static boolean isBackwardSwitch(Block block) {
		if (!block.hasSwitch()) {
			return false;
		}

		if (block.getSwitchTo().contains(block)) {
			return true;
		}

		return false;
	}

	private static double calcAuth(ArrayDeque<Block> route, Block start, Block end) {
		
		//System.out.println("calc auth");
		
		double auth = 0;
		boolean flipped = false;

		if(route.isEmpty())
			return auth;
		
		ArrayDeque<Block> temp = route.clone();
		Block block = temp.poll();
		Block prev = null;
		
		boolean success = false;

		// authority does not include our current block
		while (!block.equals(start)) {
			prev = block;
			block = temp.poll();
		}

		while (!temp.isEmpty()) {
			prev = block;
			block = temp.poll();

			// check for trains in the way
			if (block.occupied || block.broken) 
			{
				if(!prev.equals(route.peekFirst()))
					auth -= prev.length;
				
				return auth;
			} // check switches are in correct position
			else if (block.hasSwitch()) {
				if (isForwardSwitch(block) && temp.peek() != null && block.sw_to.contains(temp.peek()) && (!block.getSwitchCurrTo().equals(temp.peek()))) 
				{
					auth += block.length;
					success = false;
					
					if(flipped)
						return auth;
					
					// check if it is safe to flip switch
					if(block.sw_to.peekFirst().occupied == false && block.sw_to.peekLast().occupied == false)
					{
						success = getFlip(block);
						if(success)
						{
							updateTrack();
							flipped = true;
						}
					}
					
					if(!success)
						return auth;
				} 
				else if(isForwardSwitch(block) && prev != null && block.sw_to.contains(prev) && (!block.getSwitchCurrTo().equals(prev)))
				{
					success = false;
					
					if(flipped)
					{
						return auth;
					}
					
					// check if it is safe to flip switch
					if(block.sw_to.peekFirst().occupied == false && block.sw_to.peekLast().occupied == false)
					{
						success = getFlip(block);
						if(success)
						{
							updateTrack();
							flipped = true;
						}
					}

					if(!success)
					{	
						if(!prev.equals(route.peekFirst()))
							auth -= prev.length;
						
						return auth;
					}
					
					auth += block.length;
				}
				else if (isBackwardSwitch(block) && prev != null && block.sw_from.contains(prev) && !block.getSwitchCurrFrom().equals(prev)) 
				{
					success = false;
					
					if(flipped)
					{
						return auth;
					}
					
					
					// check if it is safe to flip switch
					if(block.sw_from.peekFirst().occupied == false && block.sw_from.peekLast().occupied == false)
					{
						success = getFlip(block);
						if(success)
						{
							updateTrack();
							flipped = true;
						}
					}

					if(!success)
					{	
						if(!prev.equals(route.peekFirst()))
							auth -= prev.length;
						
						return auth;
					}
					
					auth += block.length;
				} 
				else if (isBackwardSwitch(block) && temp.peek() != null && block.sw_from.contains(temp.peek()) && !block.getSwitchCurrFrom().equals(temp.peek())) 
				{
					auth += block.length;
					success = false;
					
					if(flipped)
						return auth;
					
					// check if it is safe to flip switch
					if(block.sw_from.peekFirst().occupied == false && block.sw_from.peekLast().occupied == false)
					{
						success = getFlip(block);
						if(success)
						{
							updateTrack();
							flipped = true;
						}
					}
					
					if(!success)
					{	
						if(!prev.equals(route.peekFirst()))
							auth -= prev.length;
						
						return auth;
					}
				}
				else 
				{
					auth += block.length;
				}
				
				flipped = true;
				
			} else {
				auth += block.length;
			}
		}

		return auth;
	}
	
	private static TrackCon getTrackCon(Block block)
	{
		ArrayDeque<TrackCon> temp = trackcons.clone();
		
		TrackCon tc = null;
		
		while(!temp.isEmpty())
		{
			tc = temp.poll();
			if(tc.hasBlock(block))
				return tc;
		}
		
		return null;
	}
	
	private static TrackCon getTrackCon(int id)
	{
		ArrayDeque<TrackCon> temp = trackcons.clone();
		
		TrackCon tc = null;
		
		while(!temp.isEmpty())
		{
			tc = temp.poll();
			if(tc.ID == id)
				return tc;
		}
		
		return null;
	}

	private static Block getBlock(String line, int num) {
		
		ArrayDeque<Block> temp = null;
		
		if(line.equalsIgnoreCase("green"))
		{
			temp = greenline.clone();
		}
		else if(line.equalsIgnoreCase("red"))
		{
			temp = redline.clone();
		}
		
		Block block;
		while (!temp.isEmpty()) {
			block = temp.poll();
			if (block.line.equalsIgnoreCase(line) && block.getNum() == num) 
			{
				return block;
			}
		}

	return null;
	}

	private static String toCap(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	private static void updateTrack() {
		// for all track block in blue line
		Object[][] rows = new Object[greenline.size() - 1 /*+ redline.size() - 1*/][TRACKCOLS];

		ArrayDeque<Block> temp = greenline.clone();
		Block block;
		int count = 0;

		while (!temp.isEmpty()) {
			block = temp.poll();

			if (block.section == '\0' && block.num == 0) {
				block = temp.poll();
			}

			rows[count][0] = toCap(block.line);
			rows[count][1] = block.section;
			rows[count][2] = block.num;
			rows[count][3] = toCap(String.valueOf(block.occupied));

			String str = "";
			
			if (!block.hasSwitch()) {
				rows[count][4] = "";
			} else {
				if(block.sw_curr_from.num == 0)
					str += "YARD";
				else
					str += "" + block.sw_curr_from.section + block.sw_curr_from.num;
				
				str += ", ";
				
				if(block.sw_curr_to.num == 0)
					str += "YARD";
				else
					str += "" + block.sw_curr_to.section + block.sw_curr_to.num;
					
				rows[count][4] = str;
			}

			rows[count][5] = "";
			
			if(block.closed)
				rows[count][6] = "Closed";
			else if(block.broken)
				rows[count][6] = "Broken";
			else
				rows[count][6] = "";

			if (!block.rrxing) {
				rows[count][7] = "";
			} else {
				rows[count][7] = toCap(String.valueOf(block.rrxing_status));
			}

			rows[count][8] = "";
			if (block.hasStation) {
				rows[count][8] = toCap(block.station);
			}

			count++;
		}
		
		/*
		temp = redline.clone();
		count = 0;

		while (!temp.isEmpty()) {
			block = temp.poll();

			if (block.section == '\0' && block.num == 0) {
				block = temp.poll();
			}

			rows[count][0] = toCap(block.line);
			rows[count][1] = block.section;
			rows[count][2] = block.num;
			rows[count][3] = toCap(String.valueOf(block.occupied));

			if (!block.hasSwitch()) {
				rows[count][4] = "";
			} else {
				rows[count][4] = "" + block.sw_curr_from.section + block.sw_curr_from.num + ", " + block.sw_curr_to.section + block.sw_curr_to.num;
			}

			rows[count][5] = "";
			rows[count][6] = "";

			if (!block.rrxing) {
				rows[count][7] = "";
			} else {
				rows[count][7] = toCap(String.valueOf(block.rrxing_status));
			}

			rows[count][8] = "";
			if (block.hasStation) {
				rows[count][8] = toCap(block.station);
			}

			count++;
		}
		*/
		ui.updateTrackTable(rows, greenline.size() - 1 /*+ redline.size() - 1*/);
	}

	private static boolean getFlip(Block swblock)
	{
		boolean flipped = false;
		ArrayDeque<Train> mytrains = new ArrayDeque<Train>();
		
		for(Train train : trains)
		{
			if(getFirstSwitch(train.route).peekFirst().equals(swblock))
			{
				mytrains.add(train);
			}
		}
		
		Train closest = null;
		double dist = Integer.MAX_VALUE;
		
		for(Train train : mytrains)
		{
			if(getDistOnRoute(train,swblock) < dist)
			{
				dist = getDistOnRoute(train,swblock);
				closest = train;
			}
		}
		
		// if switch is not in right config, flip it
		Block desired = getFirstSwitch(closest.route).peekLast();
		if((isForwardSwitch(swblock) && !swblock.sw_curr_to.equals(desired)) || (isBackwardSwitch(swblock) && !swblock.sw_curr_from.equals(desired)))
		{
			//System.out.println("Old switch pos: " + trackmodel.getBlock(swblock.line,swblock.num).getSwitchPosition());
			flipped = trackmodel.flipSwitch(swblock.line,swblock.num);
			//System.out.println("New switch pos: " + trackmodel.getBlock(swblock.line,swblock.num).getSwitchPosition());
			if(flipped)
			{
				if(isForwardSwitch(swblock))
					swblock.sw_curr_to = desired;
				else
					swblock.sw_curr_from = desired;
			}
		}
		
		return flipped;
	}
	
	private static double getDistOnRoute(Train train, Block block)
	{
		ArrayDeque<Block> rtemp = train.route.clone();
		double dist = 0;
		Block curr = null;
		
		while(!rtemp.isEmpty())
		{
			curr = rtemp.poll();
			if(curr.equals(train.location))
				break;
		}
		
		while(!rtemp.isEmpty() && !curr.equals(block))
		{
			curr = rtemp.poll();
			dist += curr.length;
		}
		
		return dist;
	}
	
	private static ArrayDeque<Block> getFirstSwitch(ArrayDeque<Block> route)
	{
		ArrayDeque<Block> retsw = null;
		Block sw = null;
		Block prev = null;
		Block next = null;
		ArrayDeque<Block> rtemp = route.clone();
		
		while(!rtemp.isEmpty())
		{
			prev = sw;
			sw = rtemp.poll();
			if(sw.sw)
			{
				retsw = new ArrayDeque<Block>();
				retsw.add(sw);
				
				if(!rtemp.isEmpty())
					next = rtemp.poll();
				
				if(isForwardSwitch(sw))
				{
					if(prev != null && sw.sw_to.contains(prev))
					{
						retsw.add(prev);
						return retsw;
					}
					else if(next != null && sw.sw_to.contains(next))
					{
						retsw.add(next);
						return retsw;
					}
				}
				else if(isBackwardSwitch(sw))
				{
					if(prev != null && sw.sw_from.contains(prev))
					{
						retsw.add(prev);
						return retsw;
					}
					else if(next != null && sw.sw_from.contains(next))
					{
						retsw.add(next);
						return retsw;
					}
				}
			}
		}
		
		return retsw;
	}
	
	private static void updateTrains() {
		// for all trains in trains
		Object[][] rows = new Object[trains.size()][TRAINCOLS];

		Train train;
		ArrayDeque<Train> temp = trains.clone();
		int count = 0;

		while (!temp.isEmpty()) {
			train = temp.poll();

			rows[count][0] = toCap(train.location.line);
			rows[count][1] = train.ID;
			rows[count][2] = "" + train.location.section + train.location.num;
			if (rows[count][2].equals("" + '\0' + 0)) {
				rows[count][2] = "YARD";
			}

			if (train.route == null) {
				rows[count][3] = "";
			} 
			else if(train.route.isEmpty())
			{
				rows[count][3] = "" + train.location.section + train.location.num;
			}
			else {
				rows[count][3] = "" + train.route.getLast().section + train.route.getLast().num;
			}

			if (rows[count][3].equals("" + '\0' + 0)) {
				rows[count][3] = "YARD";
			}

			rows[count][4] = "";
			rows[count][5] = train.authority * 1.09361; // convert to yards
			rows[count][6] = train.setpoint_speed;
			rows[count][7] = train.passengers;

			count++;

		}

		ui.updateTrainTable(rows, trains.size());

	}


	protected void readIn(String str) {
		StringTokenizer stok = new StringTokenizer(str, " ");
		String s = stok.nextToken();

		if (s.equalsIgnoreCase("MBO")) {
			tellMBOSwitches();
		} else if ((s + " " + stok.nextToken()).equalsIgnoreCase("Track Controller")) {

			s = stok.nextToken();
			if (s.equalsIgnoreCase("Train")) {
				Train train;
				int tid = Integer.parseInt(stok.nextToken());
				train = getTrain(tid);
				String line = stok.nextToken();
				char section = stok.nextToken().charAt(0);
				int number = Integer.parseInt(stok.nextToken());
				Block block = getBlock(line, number);
				train.setLoc(block);
				updateTrains();
			} else if (s.equalsIgnoreCase("Block")) {
				String line = stok.nextToken();
				char section = stok.nextToken().charAt(0);
				int number = Integer.parseInt(stok.nextToken());
				Block block = getBlock(line, number);
				Block from;
				Block to;

				while (stok.hasMoreTokens()) {
					s = stok.nextToken();

					if (s.equalsIgnoreCase("Occupancy")) {
						block.setOccupied(Boolean.parseBoolean(stok.nextToken()));
						updateAuth(block);
					} else if (s.equalsIgnoreCase("Switch")) {
						line = stok.nextToken();
						section = stok.nextToken().charAt(0);
						number = Integer.parseInt(stok.nextToken());
						from = getBlock(line, number);

						line = stok.nextToken();
						section = stok.nextToken().charAt(0);
						number = Integer.parseInt(stok.nextToken());
						to = getBlock(line, number);

						block.setSwitchPos(from, to);
						updateAuth(block);

					} else if (s.equalsIgnoreCase("RRXing")) {
						block.rrxing_status = (Boolean.parseBoolean(stok.nextToken()));
					} else if (s.equalsIgnoreCase("Status")) {

					}

				}

				updateTrack();
			} else if (s.equalsIgnoreCase("Ticket")) {
				String stat = stok.nextToken();
				Block block = getStation(stat);
				Train train = getTrain(block);
				train.passengers += Integer.parseInt(stok.nextToken());

				updateTrains();
				calcThroughput();
			}
		}
	}
	
	public static void updatePassengers(int departing, int boarding, int trainID, String line, int num)
	{
		Train train = getTrain(trainID);
		Block st = null;
		
		ArrayDeque<Block> stemp = stations.clone();
		for(Block block : stemp)
		{
			if(block.line.equalsIgnoreCase(line) && block.num == num)
			{
				st = block;
				break;
			}		
		}
		
		if(!train.location.equals(st))
			return;
		
		train.passengers -= departing;
		train.passengers += boarding;
		
		calcThroughput();
		
		
	}

	private static Train getTrain(Block location) {
		Train train = null;
		ArrayDeque<Train> temp = trains.clone();

		while (!temp.isEmpty()) {
			train = temp.poll();

			if (train.location.equals(location)) 
			{
				if(location.num != 0)
				{
					//System.out.println("Train found at " + location.display());
					return train;
				}
				else
				{
					//System.out.println("Train dispatched");
					return dispatched.poll();
				}
			}
		}

		//System.out.println("null");
		return null;
	}

	private static Block getStation(String name) {
		ArrayDeque<Block> temp = greenline.clone();
		Block block;

		while (!temp.isEmpty()) {
			block = temp.poll();

			if (block.hasStation) {
				if (block.station.equalsIgnoreCase(name)) {
					return block;
				}
			}
		}
		
		temp = redline.clone();

		while (!temp.isEmpty()) {
			block = temp.poll();

			if (block.hasStation) {
				if (block.station.equalsIgnoreCase(name)) {
					return block;
				}
			}
		}

		return null;

	}

	private static void updateAuth(Block bl) {
		ArrayDeque<Train> temp = trains.clone();

		Train train;

		while (!temp.isEmpty()) {
			train = temp.poll();

			if (train.getRoute() != null && train.getRoute().contains(bl)) {
				train.setAuth(calcAuth(train.getRoute(), train.getLoc(), train.getRoute().peekLast()));
				sendSpeedAuth(train, train.setpoint_speed, train.authority);
			}
		}

		updateTrains();
	}

	protected void tellMBOSwitches() {
		// for all blocks in switches
		System.out.println("To MBO");
		ArrayDeque<Block> temp = switches.clone();
		Block sw;

		while (!temp.isEmpty()) {
			sw = temp.poll();
			System.out.println("Switch in block " + sw.display());
			System.out.println("Current position: from " + sw.getSwitchCurrFrom().display() + ", to " + sw.getSwitchCurrTo().display());
		}
	}
	
	public int[][] requestSwitches(String line)
	{
		ArrayDeque<Block> temp = switches.clone();
		Block sw;
		int numswitches = 0;
		
		while (!temp.isEmpty()) {
			sw = temp.poll();
			if(sw.line.equals(line))
			{
				numswitches++;
			}
		}
		
		temp = switches.clone();
		int[][] MBOswitches = new int[numswitches][2];
		int count = 0;
		
		while (!temp.isEmpty()) {
			sw = temp.poll();
			if(sw.line.equals(line))
			{
				MBOswitches[count][0] = sw.sw_curr_from.num;
				MBOswitches[count][1] = sw.sw_curr_to.num;
				
				count++;
			}
		}
		
		return MBOswitches;
	}
	
	protected static void blockMaintenance(String line, String block, boolean close)
	{
		if(block.equalsIgnoreCase("YARD"))
			return;
		
		Block bl = getBlock(line,Integer.parseInt(block.substring(1)));
		
		trackmodel.setMaintenance(line,Integer.parseInt(block.substring(1)),close);
		if(!close)
		{
			bl.broken = false;
			bl.closed = false;
		}
		else
		{
			bl.closed = true;
		}
	}

	private static void loadSched() {

	}

	private static void readInTrack() {

	}

	private static void sendSpeedAuth(Train train, double speed, double auth) {
		Block loc = train.getLoc();

		//SwitchAndPos swpos = getSwitches(train.getRoute()).peek();

		Block sw = null;
		Block from = null;
		Block to = null;

		//if (swpos != null) {
		//	sw = swpos.getBlock();
		//	from = swpos.getFrom();
		//	to = swpos.getTo();
		//}

		//System.out.println("To Track Controller");
		//System.out.println("Train " + train.getID() + " at location " + loc.display());
		//System.out.println("Send speed = " + speed + ", authority = " + auth);
		
		//if (swpos != null) {
		//	System.out.println("Next switch position, in block: " + sw.display() + " from: " + from.display() + " to: " + to.display());
		//}
		

		//System.out.println();
		
		speed = speed * 1.60934; // convert to kph from mph
		
		String msg = speed + " " + auth;
		//System.out.println(train.ID + " at " + train.location.display() + ": " + msg);
		
		if(auth < 0)
			auth = 0;
		
		TrackMovementCommand tmc = new TrackMovementCommand((int)speed,(int)auth);
		
		if(train.location.num == 0)
		{
			trackmodel.setYardMessage(train.ID, 0, tmc);
			dispatched.add(train);
		}
		else
			trackmodel.setBlockMessage(loc.line, loc.num, tmc);
	}
	
	protected static void sendSpeedAuthShort(String trainID, double speed, double auth)
	{
		Train train = getTrain(Integer.parseInt(trainID));
		
		String msg = speed + " " + auth;
		
		TrackMovementCommand tmc = new TrackMovementCommand((int)speed,(int)auth);
		
		if(train.location.num == 0)
		{
			trackmodel.setYardMessage(0, 0, tmc);
		}
		else
			trackmodel.setBlockMessage(train.location.line, train.location.num, tmc);
	}
	
	private static void combineAuth(TrackCon tc, boolean[] s, boolean[] l, boolean[] r)
	{
		// either combine route with current track con authority or just send route as pieces
		
	}
	
	private static void routeToBool(ArrayDeque<Block> r)
	{
		ArrayDeque<Block> route = r.clone();
		TrackCon tc = getTrackCon(route.peek());
		ArrayDeque<Block> tc_track;
		Block tc_block;
		Block r_block = route.poll();
		char status;
		
		boolean[] straight_array = new boolean[tc.nstraight];
		boolean[] left_array = new boolean[tc.nleft];
		boolean[] right_array = new boolean[tc.nright];
		
		
		int count = 0;
		
		while(r_block != null)
		{
			count = 0;
			
			if(!tc.hasBlock(r_block))
			{
				combineAuth(tc,straight_array,left_array,right_array);
				tc = getTrackCon(r_block);
				straight_array = new boolean[tc.nstraight];
				left_array = new boolean[tc.nleft];
				right_array = new boolean[tc.nright];
			}
			
			if(tc.straight.contains(r_block))
			{
				tc_track = tc.straight.clone();
				status = 's';
			}
			else if(tc.left.contains(r_block))
			{
				tc_track = tc.left.clone();
				status = 'l';
			}
			else
			{
				tc_track = tc.right.clone();
				status = 'r';
			}
			
			while(!tc_track.isEmpty())
			{
				tc_block = tc_track.poll();
				if(tc_block.equals(r_block))
				{
					switch(status)
					{
						case 's':
							straight_array[count] = true;
							break;
						case 'l':
							left_array[count] = true;
							break;
						case 'r':
							right_array[count] = true;
							break;	
					}

					r_block = route.poll();
				}

				count++;
			}
		}
		

	}

	//private static void setSwitch(Block swBlock, Block from, Block to) {
	//	System.out.println("To Track Controller");
	//	System.out.println("Set switch at block " + swBlock.display());
	//	System.out.println("To configuration from = " + from.display() + ", to = " + to.display());
	//}

	private static void getTrackConUpdate() {

	}

	private static void getTicketInfo() {

	}

	private static void calcThroughput() {
		ArrayDeque<Train> temp = trains.clone();
		through = 0;

		while (!temp.isEmpty()) {
			through += temp.poll().passengers;
		}

		ui.updateThroughput(through);
	}

	private static ArrayDeque<Block> explored;

	private static ArrayDeque<Block> findRoute(Train train, Block start, Block dest) {
		ArrayDeque<Block> route = new ArrayDeque<Block>();
		//explored = new ArrayDeque<>();

		routes = new ArrayDeque<ArrayDeque<Block>>();

		int max = 0;
		
		if(dest.line.equalsIgnoreCase("green"))
		{
			maxlen = 2* greenline.size();
		}
		else if(dest.line.equalsIgnoreCase("red"))
		{
			maxlen = 2 * redline.size();
		}
		
		initFrom = train.getPrevBlock();
		
		findRouteRec(start, dest, new ArrayDeque<Block>());

		int minsize = Integer.MAX_VALUE;
		ArrayDeque<Block> curr;
		ArrayDeque<ArrayDeque<Block>> temp = routes.clone();
		while (!temp.isEmpty()) {
			curr = temp.poll();
			if (curr.size() < minsize) {
				minsize = curr.size();
				route = curr;
			}

		}

		/*
		System.out.println();
		ArrayDeque<Block> rtemp = route.clone();
		while(!rtemp.isEmpty())
			System.out.print(rtemp.poll().display() + " ");
		System.out.println();
		*/
		
		return route;
	}

	private static ArrayDeque<ArrayDeque<Block>> routes;
	private static int maxlen;
	private static Block initFrom;

	private static void findRouteRec(Block start, Block dest, ArrayDeque<Block> route) {
		
		/*
		// for debugging
		ArrayDeque<Block> temp = route.clone();
		for(Block blk : temp)
		{
			System.out.print(blk.display() + " ");
		}
		System.out.println();
		*/
		
		Block cameFrom = route.peekLast();
		if(cameFrom == null)
			cameFrom = initFrom;
		
		route.add(start);
		//explored.add(start);

		if (route.size() > maxlen) {
			return;
		}

		if (start.equals(dest)) {
			routes.add(route);
			if(route.size() < maxlen)
				maxlen = route.size();
			return;
		}

		Block blkToAdd;
		Block block = start;
		ArrayDeque<Block> neighbors = new ArrayDeque<Block>();

		if (block.hasSwitch()) 
		{
			if(isForwardSwitch(block))
			{
				if(block.nextBlockDir == 1 && !block.sw_to.contains(cameFrom))
				{
					blkToAdd = block.getSwitchTo().clone().peekFirst();
					if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
				}
				if(block.switchDir < 2 && block.switchDir > -2 && !block.sw_to.contains(cameFrom))
				{
					blkToAdd = block.getSwitchTo().clone().peekLast();
					if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
				}
				if(block.prevBlockDir == 1)
				{
					blkToAdd = block.prev;
					if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
				}
			}
			else
			{			
				if(block.nextBlockDir == 1)
				{
					blkToAdd = block.next;
					if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
				}
				if(block.switchDir < 2 && block.switchDir > -2 && !block.sw_from.contains(cameFrom))
				{
					blkToAdd = block.getSwitchFrom().clone().peekLast();
					if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
				}
				if(block.prevBlockDir == 1 && !block.sw_from.contains(cameFrom))
				{
					blkToAdd = block.getSwitchFrom().clone().peekFirst();
					if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
				}
			}
		} 
		else 
		{
			if(block.nextBlockDir == 1)
			{
				blkToAdd = block.next;
				if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
			}
			if(block.prevBlockDir == 1)
			{
				blkToAdd = block.prev;
				if(!cameFrom.equals(blkToAdd))
						neighbors.add(blkToAdd);
			}
		}
		
		/*
		// for debugging
		ArrayDeque<Block> ntemp = neighbors.clone();
		System.out.print("\nblock: ");
		System.out.println(start.display());
		System.out.print("neighbors: ");
		while(!ntemp.isEmpty())
			System.out.print(ntemp.poll().display() + " ");
		System.out.println();
		System.out.print("route: ");
		ArrayDeque<Block> rtemp = route.clone();
		while(!rtemp.isEmpty())
			System.out.print(rtemp.poll().display() + " ");
		System.out.println();
		*/
		
		while (!neighbors.isEmpty()) {
			block = neighbors.poll();
			
			while(block.num == 0 && dest.num != 0)
			{
				if(neighbors.isEmpty())
					return;
		
				block = neighbors.poll();
			}
				
			findRouteRec(block, dest, route.clone());

		}

	}

	private static ArrayDeque<SwitchAndPos> getSwitches(ArrayDeque<Block> route) {
		if (route.getFirst().equals(route.getLast())) {
			return null;
		}

		ArrayDeque<SwitchAndPos> switches = new ArrayDeque<SwitchAndPos>();

		SwitchAndPos swpos;
		Block from = null;
		Block to = null;

		ArrayDeque<Block> temp = route.clone();
		Block prev = null;
		Block curr = temp.poll();

		do {

			if (curr.hasSwitch()) {

				if (curr.getSwitchFrom().contains(curr)) {
					from = curr;
					if (temp.peek() != null) {
						to = temp.peek();
					} else {
						to = null;
					}
				} else if (curr.getSwitchTo().contains(curr)) {
					to = curr;
					if (prev != null) {
						from = prev;
					} else {
						from = null;
					}
				}

				if (from != null && to != null) {
					swpos = new SwitchAndPos(curr, from, to);
					switches.add(swpos);
				}
			}

			prev = curr;
			curr = temp.poll();

		} while (!temp.isEmpty());

		return switches;
	}
	
	private static class TrackCon
	{
		private int ID;
		private ArrayDeque<Block> straight;
		private ArrayDeque<Block> left;
		private ArrayDeque<Block> right;
		private int nstraight;
		private int nleft;
		private int nright;
		private boolean[] straight_auth;
		private boolean[] left_auth;
		private boolean[] right_auth;
		
		public TrackCon(int id)
		{
			ID = id;
			straight = null;
			left = null;
			right = null;
			nstraight = 0;
			nleft = 0;
			nright = 0;
			straight_auth = new boolean[nstraight];
			left_auth = new boolean[nleft];
			right_auth = new boolean[nright];
		}
		
		public TrackCon(int id, ArrayDeque<Block> s, ArrayDeque<Block> l, ArrayDeque<Block> r)
		{
			ID = id;
			straight = s;
			left = l;
			right = r;
			nstraight = straight.size();
			nleft = left.size();
			nright = right.size();
			straight_auth = new boolean[nstraight];
			left_auth = new boolean[nleft];
			right_auth = new boolean[nright];
		}
		
		public boolean hasBlock(Block block)
		{
			return straight.contains(block) || left.contains(block) || right.contains(block);
		}
	}

	private static class SwitchAndPos {

		private Block block;
		private Block from;
		private Block to;

		public SwitchAndPos() {
			block = null;
			from = null;
			to = null;
		}

		public SwitchAndPos(Block b, Block f, Block t) {
			block = b;
			from = f;
			to = t;
		}

		public Block getBlock() {
			return block;
		}

		public Block getFrom() {
			return from;
		}

		public Block getTo() {
			return to;
		}
	}

	protected static class Train {

		protected int ID;
		protected Block location;
		protected double authority;
		protected double setpoint_speed;
		protected ArrayDeque<Block> route;
		protected Block dest;
		protected double deadline;
		protected int passengers;
		protected int driverID;
		protected Block lastBlock;

		public Train() {
			ID = 0;
			location = null;
			authority = 0;
			setpoint_speed = 0;
			route = null;
			dest = null;
			deadline = 0;
			passengers = 0;
			driverID = 0;
			lastBlock = null;
		}

		public Train(int id, Block loc, int dID) {
			ID = id;
			location = loc;
			authority = 0;
			setpoint_speed = 0;
			route = null;
			dest = null;
			deadline = 0;
			passengers = 0;
			driverID = dID;
			lastBlock = null;

		}

		public Train(int id, Block loc, double auth, double speed, ArrayDeque<Block> r) {
			ID = id;
			location = loc;
			authority = auth;
			setpoint_speed = speed;
			route = r;
			dest = route.getLast();
			deadline = 0;
			passengers = 0;
			driverID = 0;
			lastBlock = null;
		}

		private Block getPrevBlock()
		{
			if(lastBlock != null)
				return lastBlock;
			
			return new Block();
		}
		
		private void setPrevBlock(Block pb)
		{
			lastBlock = pb;
		}
		
		private ArrayDeque<Block> getRoute() {
			return route;
		}

		private void setLoc(Block newLoc) {
			lastBlock = location;
			location = newLoc;
			route.poll();
		}

		public Block getLoc() {
			return location;
		}

		public void setRoute(ArrayDeque<Block> r) {
			route = r;
		}

		public void setSpeed(double s) {
			setpoint_speed = s;
		}

		public void setAuth(double a) {
			authority = a;
		}

		public int getID() {
			return ID;
		}
		
		public void setDriver(int id)
		{
			driverID = id;
		}
		
		public int getDriver()
		{
			return driverID;
		}
	}

	private static class Block {

		private boolean yard;
		private String line;
		private char section;
		private int num;
		private boolean occupied;
		private double length;
		private Block next;
		private Block prev;
		private boolean sw;
		private int switchID;
		private boolean rrxing;
		private ArrayDeque<Block> sw_from;
		private ArrayDeque<Block> sw_to;
		private boolean rrxing_status;
		private boolean signal;
		private boolean broken;
		private boolean closed;
		private Block sw_curr_from;
		private Block sw_curr_to;
		private boolean hasStation;
		private String station;
		private int nextBlockDir;
		private int prevBlockDir;
		private int switchDir;

		public Block() {
			yard = false;
			line = null;
			section = 0;
			num = 0;
			occupied = false;
			length = 0;
			next = null;
			prev = null;
			sw = false;
			switchID = 0;
			rrxing = false;
			sw_from = null;
			sw_to = null;
			rrxing_status = false;
			signal = false;
			broken = false;
			sw_curr_from = null;
			sw_curr_to = null;
			hasStation = false;
			station = "";
			closed = false;
		}

		public Block(String l, boolean isYard) {
			yard = isYard;
			line = l;
			section = '\0';
			num = 0;
			occupied = false;
			length = 0;
			next = null;
			prev = null;
			sw = false;
			switchID = 0;
			rrxing = false;
			sw_from = null;
			sw_to = null;
			rrxing_status = false;
			signal = false;
			broken = false;
			sw_curr_from = null;
			sw_curr_to = null;
			hasStation = false;
			station = "";
			closed = false;
		}

		public Block(String l, char sec, int n, double len, int nextdir, int prevdir, Block nextb, Block prevb, boolean rr) {
			
			if(n!=0)
				yard = false;
			else yard = true;
			
			line = l;
			section = sec;
			num = n;
			occupied = false;
			length = len;
			next = nextb;
			prev = prevb;
			sw = false;
			switchID = 0;
			rrxing = rr;
			sw_from = null;
			sw_to = null;
			rrxing_status = false;
			signal = false;
			broken = false;
			sw_curr_from = null;
			sw_curr_to = null;
			hasStation = false;
			station = "";
			nextBlockDir = nextdir;
			prevBlockDir = prevdir;
			closed = false;
		}

		public Block(String l, char sec, int n, double len, int nextdir, int prevdir, Block nextb, Block prevb, boolean rr, int swID, int swdir, ArrayDeque<Block> swf, ArrayDeque<Block> swt, Block currf, Block currt) {
			
			if(n!=0)
				yard = false;
			else yard = true;
			
			line = l;
			section = sec;
			num = n;
			occupied = false;
			length = len;
			next = nextb;
			prev = prevb;
			sw = true;
			switchID = swID;
			rrxing = rr;
			sw_from = swf;
			sw_to = swt;
			rrxing_status = false;
			signal = false;
			broken = false;
			sw_curr_from = currf;
			sw_curr_to = currt;
			hasStation = false;
			station = "";
			nextBlockDir = nextdir;
			prevBlockDir = prevdir;
			switchDir = swdir;
			closed = false;
		}

		public Block(String l, char sec, int n, double len, int nextdir, int prevdir, Block nextb, Block prevb, boolean rr, boolean stat, String statID) {
			
			if(n!=0)
				yard = false;
			else yard = true;
			
			line = l;
			section = sec;
			num = n;
			occupied = false;
			length = len;
			next = nextb;
			prev = prevb;
			sw = false;
			switchID = 0;
			rrxing = rr;
			sw_from = null;
			sw_to = null;
			rrxing_status = false;
			signal = false;
			broken = false;
			sw_curr_from = null;
			sw_curr_to = null;
			hasStation = stat;
			station = statID;
			nextBlockDir = nextdir;
			prevBlockDir = prevdir;
			closed = false;
		}

		private boolean isOccupied() {
			return occupied;
		}

		private void setOccupied(boolean stat) {
			occupied = stat;
		}

		private double getLength() {
			return length;
		}

		private String display() {
			return line + " " + section + " " + num;
		}

		private String getLine() {
			return line;
		}

		private char getSec() {
			return section;
		}

		private int getNum() {
			return num;
		}

		private void setPrev(Block p) {
			prev = p;
		}

		private void setNext(Block n) {
			next = n;
		}

		private int getSwID() {
			return switchID;
		}

		private void setSwitch(int id, ArrayDeque<Block> from, ArrayDeque<Block> to) {
			sw = true;
			switchID = id;
			sw_from = from;
			sw_to = to;
			sw_curr_from = from.peekFirst();
			sw_curr_to = to.peekFirst();
		}

		private void setSwitchPos(Block currf, Block currt) {
			if (sw_from.contains(currf) && sw_to.contains(currt)) {
				sw_curr_from = currf;
				sw_curr_to = currt;
			}

		}

		private Block getNext() {
			return next;
		}

		private boolean hasSwitch() {
			return sw;
		}

		private ArrayDeque<Block> getSwitchTo() {
			return sw_to;
		}

		private ArrayDeque<Block> getSwitchFrom() {
			return sw_from;
		}

		private Block getSwitchCurrFrom() {
			return sw_curr_from;
		}

		private Block getSwitchCurrTo() {
			return sw_curr_to;
		}

		private ArrayDeque<Block> getSwitchPos() {
			if (sw == false) {
				return null;
			}

			ArrayDeque<Block> pos = new ArrayDeque<Block>();

			pos.add(sw_curr_from);
			pos.add(sw_curr_to);

			return pos;
		}

	}
}
