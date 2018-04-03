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
import java.util.StringTokenizer;

import updater.Updateable;
import track_model.TrackModel;
import train_model.communication.TrackMovementCommand;

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

	public static ArrayDeque<Train> trains = new ArrayDeque<Train>();
	public static ArrayDeque<Block> blueline = new ArrayDeque<Block>();
	public static ArrayDeque<Block> switches = new ArrayDeque<Block>();
	public static ArrayDeque<TrackCon> trackcons = new ArrayDeque<TrackCon>();

	public static double through = 0;

	public static void showUI() {
		ui.showUI();
	}

	public void update(int time)
	{
		// ask track model for updates
		
		updateTrack();
		updateTrains();
		return;
	}
	
	/*
    public MyCtc()
    {
        ctc = this;
        ui = new MyCtcUI(ctc);
    }
	 */
	//public static void main(String[] args)
	
	public void setTrackModel(TrackModel tm)
	{
		this.trackmodel = tm;
	}
	
	public Ctc() {
		ctc = this;
		ui = new CtcUI(ctc);
		//showUI();

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

		updateTrack();
		updateTrains();


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

		Block block = getBlock(l, sec, num);

		routeTrain(train, block, sp);

		return true;
	}

	private static void routeTrain(Train train, Block dest, double sp) {
		ArrayDeque<Block> route = findRoute(train, train.getLoc(), dest);

		printRoute(route);

		train.setRoute(route);

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
			if (!block.getSwitchCurrFrom().equals(f) || !block.getSwitchCurrTo().equals(t)) {
				setSwitch(block, f, t);
			}

		}

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
		double auth = 0;

		ArrayDeque<Block> temp = route.clone();
		Block block = temp.poll();
		Block prev = null;

		// authority does not include our current block
		while (!block.equals(start)) {
			prev = block;
			block = temp.poll();
		}

		while (!temp.isEmpty()) {
			prev = block;
			block = temp.poll();

			// check for trains in the way
			if (block.isOccupied()) {
				return auth;
			} // check switches are in correct position
			else if (block.hasSwitch()) {
				if (isForwardSwitch(block) && temp.peek() != null && (!block.getSwitchCurrTo().equals(temp.peek()))) {
					auth += block.getLength();
					return auth;
				} else if (isBackwardSwitch(block) && prev != null && !block.getSwitchCurrFrom().equals(prev)) {
					return auth;
				} else {
					auth += block.getLength();
				}
			} else {
				auth += block.getLength();
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

	private static Block getBlock(String line, char sec, int num) {
		ArrayDeque<Block> temp = blueline.clone();
		Block block;
		while (!temp.isEmpty()) {
			block = temp.poll();
			if (block.getLine().equals("blue") && block.getSec() == sec && block.getNum() == num) {
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
		Object[][] rows = new Object[blueline.size()][TRACKCOLS];

		ArrayDeque<Block> temp = blueline.clone();
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

		ui.updateTrackTable(rows, blueline.size());
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
			} else {
				rows[count][3] = "" + train.route.getLast().section + train.route.getLast().num;
			}

			if (rows[count][3].equals("" + '\0' + 0)) {
				rows[count][3] = "YARD";
			}

			rows[count][4] = "";
			rows[count][5] = train.authority;
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
				Block block = getBlock(line, section, number);
				train.setLoc(block);
				updateTrains();
			} else if (s.equalsIgnoreCase("Block")) {
				String line = stok.nextToken();
				char section = stok.nextToken().charAt(0);
				int number = Integer.parseInt(stok.nextToken());
				Block block = getBlock(line, section, number);
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
						from = getBlock(line, section, number);

						line = stok.nextToken();
						section = stok.nextToken().charAt(0);
						number = Integer.parseInt(stok.nextToken());
						to = getBlock(line, section, number);

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

	private static Train getTrain(Block location) {
		Train train = null;
		ArrayDeque<Train> temp = trains.clone();

		while (!temp.isEmpty()) {
			train = temp.poll();

			if (train.getLoc().equals(location)) {
				return train;
			}
		}

		return train;
	}

	private static Block getStation(String name) {
		ArrayDeque<Block> temp = blueline.clone();
		Block block;

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
	
	protected int[][] requestSwitches(String line)
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

	private static void loadSched() {

	}

	private static void readInTrack() {

	}

	private static void sendSpeedAuth(Train train, double speed, double auth) {
		Block loc = train.getLoc();

		SwitchAndPos swpos = getSwitches(train.getRoute()).peek();

		Block sw = null;
		Block from = null;
		Block to = null;

		if (swpos != null) {
			sw = swpos.getBlock();
			from = swpos.getFrom();
			to = swpos.getTo();
		}

		System.out.println("To Track Controller");
		System.out.println("Train " + train.getID() + " at location " + loc.display());
		System.out.println("Send speed = " + speed + ", authority = " + auth);
		
		if (swpos != null) {
			System.out.println("Next switch position, in block: " + sw.display() + " from: " + from.display() + " to: " + to.display());
		}
		

		System.out.println();
		
		String msg = speed + " " + auth;
		
		TrackMovementCommand tmc = new TrackMovementCommand((int)speed,(int)auth);
		
		if(train.location.num == 0)
			trackmodel.setYardMessage(train.ID, 0, tmc);
		else
			trackmodel.setBlockMessage(loc.line, loc.num, msg);
	}
	
	private static void sendSpeedAuthShort(Train train, double speed, double auth)
	{
		String msg = speed + " " + auth;
		
		TrackMovementCommand tmc = new TrackMovementCommand((int)speed,(int)auth);
		
		if(train.location.num == 0)
			trackmodel.setYardMessage(train.ID, 0, tmc);
		else
			trackmodel.setBlockMessage(train.location.line, train.location.num, msg);
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

	private static void setSwitch(Block swBlock, Block from, Block to) {
		System.out.println("To Track Controller");
		System.out.println("Set switch at block " + swBlock.display());
		System.out.println("To configuration from = " + from.display() + ", to = " + to.display());
	}

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

		return route;
	}

	private static ArrayDeque<ArrayDeque<Block>> routes;

	private static void findRouteRec(Block start, Block dest, ArrayDeque<Block> route) {
		route.add(start);
		//explored.add(start);

		if (route.size() > blueline.size()) {
			return;
		}

		if (start.equals(dest)) {
			routes.add(route);
			return;
		}

		Block block = start;
		ArrayDeque<Block> neighbors = new ArrayDeque<Block>();

		if (block.hasSwitch() && isForwardSwitch(block)) {
			neighbors = block.getSwitchTo().clone();
		} else {
			neighbors.add(block.getNext());
		}

		while (!neighbors.isEmpty()) {
			block = neighbors.poll();
			//if(!explored.contains(block))
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

		public Train() {
			ID = 0;
			location = null;
			authority = 0;
			setpoint_speed = 0;
			route = null;
			dest = null;
			deadline = 0;
			passengers = 0;
		}

		public Train(int id, Block loc) {
			ID = id;
			location = loc;
			authority = 0;
			setpoint_speed = 0;
			route = null;
			dest = null;
			deadline = 0;
			passengers = 0;

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

		}

		private ArrayDeque<Block> getRoute() {
			return route;
		}

		private void setLoc(Block newLoc) {
			location = newLoc;
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
		private Block sw_curr_from;
		private Block sw_curr_to;
		private boolean hasStation;
		private String station;

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
		}

		public Block(String l, char sec, int n, double len, Block nextb, Block prevb, boolean rr) {
			yard = false;
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
		}

		public Block(String l, char sec, int n, double len, Block nextb, Block prevb, boolean rr, int swID, ArrayDeque<Block> swf, ArrayDeque<Block> swt, Block currf, Block currt) {
			yard = false;
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
		}

		public Block(String l, char sec, int n, double len, Block nextb, Block prevb, boolean rr, boolean stat, String statID) {
			yard = false;
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
