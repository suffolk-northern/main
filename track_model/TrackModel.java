/*
 * Roger Xue
 *
 * Main Track Model.
 */
package track_model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import train_model.Pose;
import train_model.TrainModel;
import train_model.communication.BeaconMessage;
import train_model.communication.TrackMovementCommand;

import updater.Updateable;

public class TrackModel implements Updateable {

	private static int temperature;
	// Main Track Model Frame.
	private static TrackModelFrame tmf;
	// Database helper.
	protected static final DbHelper dbHelper = new DbHelper();

	// ArrayList of registered trains.
	protected static ArrayList<TrainData> trains = new ArrayList<>();
	// ArrayLists of track model objects.
	protected static ArrayList<TrackBlock> blocks = new ArrayList<>();
	protected static ArrayList<Crossing> crossings = new ArrayList<>();
	protected static ArrayList<Station> stations = new ArrayList<>();
	protected static ArrayList<Beacon> beacons = new ArrayList<>();

	// Hard-coded initial orientations.
	private static final Orientation GREEN_LINE_ORIENTATION = Orientation.radians(0.9 * Math.PI);
	private static final Orientation RED_LINE_ORIENTATION = Orientation.radians(0.3737 * Math.PI);
	// Hard-coded initial yard coordinates.
	private static final double YARD_X_LOCATION = 1100;
	private static final double YARD_Y_LOCATION = -1900;

	/**
	 * For testing purposes.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		TrackModel tm = new TrackModel();
		tm.launchTestUI();
	}

	/**
	 * Initializes Track Model object.
	 */
	public TrackModel() {
		initializeLocalArrays();
	}

	/**
	 * Generates a yard block.
	 *
	 * @param line
	 * @return TrackBlock yard
	 */
	private TrackBlock generateYardBlock(String line) {
		TrackBlock tb = new TrackBlock(line, 0);
		//
		// Hard-coded coordinates.
		//
		tb.setStartCoordinates(YARD_X_LOCATION, YARD_Y_LOCATION);
		tb.setEndCoordinates(YARD_X_LOCATION, YARD_Y_LOCATION);
		//
		// Retrives blocks directly connected to yard.
		//
		if (doTablesExist()) {
			try {
				try (Connection conn = dbHelper.getConnection()) {
					// Retrieve yard next block
					ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND SWITCH_BLOCK=0 "
							+ "AND (ABS(SWITCH_VALID)=2 OR (SWITCH_VALID=1 AND PREV_VALID=1) OR (SWITCH_VALID=-1 AND NEXT_VALID=1));");
					while (rs.next()) {
						tb.nextBlockId = rs.getInt(3);
						tb.nextBlockDir = rs.getInt(9) < 0 ? 1 : 0;
					}
					// Retrieve yard previous block
					rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND SWITCH_BLOCK=0 "
							+ "AND ((SWITCH_VALID=-1 AND PREV_VALID=1) OR (SWITCH_VALID=1 AND NEXT_VALID=1));");
					while (rs.next()) {
						tb.prevBlockId = rs.getInt(3);
						tb.prevBlockDir = rs.getInt(9) == rs.getInt(5) ? 1 : 0;
					}
				}
			} catch (SQLException ex) {
				Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return tb;
	}

	/**
	 * Initializes the local arrays holding the track model objects.
	 */
	protected void initializeLocalArrays() {
		blocks = new ArrayList<>();
		ArrayList<TrackBlock> blocksTemp = new ArrayList<>();   // Dumb way for program initialization without track database.
		crossings = new ArrayList<>();
		stations = new ArrayList<>();
		//
		// Generates yard blocks.
		//
		blocksTemp.add(generateYardBlock("green"));
		blocksTemp.add(generateYardBlock("red"));
		//
		// Gathers data from database.
		//
		if (doTablesExist()) {
			try {
				try (Connection conn = dbHelper.getConnection()) {
					ResultSet rs = dbHelper.query(conn, "SELECT LINE, BLOCK FROM BLOCKS WHERE BLOCK > 0;");
					while (rs.next()) {
						blocksTemp.add(getBlockFromDatabase(rs.getString("Line"), rs.getInt("Block")));
					}
					rs = dbHelper.query(conn, "SELECT LINE, BLOCK FROM CROSSINGS;");
					while (rs.next()) {
						crossings.add(getCrossingFromDatabase(rs.getString("Line"), rs.getInt("Block")));
					}
					rs = dbHelper.query(conn, "SELECT LINE, BLOCK FROM STATIONS;");
					while (rs.next()) {
						stations.add(getStationFromDatabase(rs.getString("Line"), rs.getInt("Block")));
					}
					rs.close();
				}
			} catch (SQLException ex) {
				Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Track database not found.\n\nPlease import track database or train control system will shut down.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		//
		// Shifts switches.
		//
		shiftSwitches(blocksTemp);
		//s
		// Sets up blocks array so program can continue. It's dumb. DO NOT CHANGE.
		//
		blocks = blocksTemp;
		//
		// Gets station locations and sets beacons.
		//
		for (int i = 0; i < stations.size(); i++) {
			Station s = stations.get(i);
			Station dupe = null;
			TrackBlock tb = getBlock(s.line, s.block);
			//
			// Checks for stations attributed to multiple blocks.
			//
			for (int j = 0; j < i; j++) {
				Station temp = stations.get(j);
				if (s.name.equalsIgnoreCase(temp.name)) {
					dupe = temp;
					break;
				}
			}
			if (dupe != null) {
				GlobalCoordinates sLocation = getPositionAlongBlock(tb, tb.length / 2);
				double newLon = (sLocation.longitude() + dupe.location.longitude()) / 2;
				double newLat = (sLocation.latitude() + dupe.location.latitude()) / 2;
				s.setLocation(new GlobalCoordinates(newLat, newLon));
				dupe.setLocation(new GlobalCoordinates(newLat, newLon));
			} else {
				s.setLocation(getPositionAlongBlock(tb, tb.length / 2));
			}
			s.setBeaconPrev(new Beacon(getPositionAlongBlock(tb, 10), s.block + " PREV"));
			s.setBeaconNext(new Beacon(getPositionAlongBlock(tb, tb.length - 10), s.block + " NEXT"));
			beacons.add(s.beaconPrev);
			beacons.add(s.beaconNext);
		}
	}

	/**
	 * Launches main UI for integration.
	 */
	public void launchUI() {
		tmf = new TrackModelFrame(this);
		tmf.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		tmf.setLocationRelativeTo(null);
		tmf.setVisible(true);
	}

	/**
	 * Launches initial integration UI if no track database is loaded.
	 * Difference is it kills the whole program if no database is loaded.
	 */
	public void launchInitialUI() {
		tmf = new TrackModelFrame(this);
		tmf.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		tmf.setLocationRelativeTo(null);
		tmf.setVisible(true);
	}

	/**
	 * Changes close operation on initial UI after database is loaded. Don't
	 * want it to exit the whole program on close so now it'll dispose itself on
	 * close.
	 */
	public void closeInitialUI() {
		tmf.dispose();
	}

	/**
	 * Launches test UI for testing.
	 */
	private void launchTestUI() {
		tmf = new TrackModelFrame(this);
		tmf.setLocationRelativeTo(null);
		tmf.setVisible(true);
		if (doTablesExist()) {
			TestFrame tf = new TestFrame(tmf, dbHelper);
			tf.setLocationRelativeTo(tmf);
			tf.setVisible(true);
		}
	}

	/**
	 * Checks if database tables exist.
	 *
	 * @return doTablesExist
	 */
	public static boolean doTablesExist() {
		boolean exist = false;
		try {
			try (Connection conn = dbHelper.getConnection()) {
				exist = dbHelper.tableExists(conn, "BLOCKS")
						&& dbHelper.tableExists(conn, "CONNECTIONS")
						&& dbHelper.tableExists(conn, "CROSSINGS")
						&& dbHelper.tableExists(conn, "STATIONS");
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return exist;
	}

	/**
	 * Gets a block based on specific line name and block number.
	 *
	 * @param line
	 * @param block
	 * @return TrackBlock
	 */
	public static TrackBlock getBlock(String line, int block) {
		for (TrackBlock tb : blocks) {
			if (tb.line.equalsIgnoreCase(line) && tb.block == block) {
				return tb;
			}
		}
		System.out.println("Track block not found. (" + line + " " + block + ")");
		return null;
	}

	/**
	 * Retrieves block data from the database.
	 *
	 * @param line
	 * @param block
	 * @return TrackBlock
	 */
	private static TrackBlock getBlockFromDatabase(String line, int block) {
		if (!doTablesExist()) {
			return null;
		}
		TrackBlock tb = null;
		line = line.toLowerCase();
		try {
			Connection conn = dbHelper.getConnection();
			ResultSet rs = dbHelper.query(conn, "SELECT * FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
			if (rs.next()) {
				//
				// Retrieves track block data.
				//
				tb = new TrackBlock(line, block);
				tb.setSection(rs.getString(2).charAt(0));
				tb.setLength(rs.getFloat(4));
				tb.setCurvature(rs.getFloat(5));
				tb.setGrade(rs.getFloat(6));
				tb.setSpeedLimit(rs.getInt(7));
				tb.setIsUnderground(rs.getBoolean(8));
				tb.setStartCoordinates(rs.getDouble(9), rs.getDouble(10));
				tb.setCenterCoordinates(rs.getDouble(13), rs.getDouble(14));
				//
				// Sets track block end coordinates from the next block it's connected to.
				//
				rs = dbHelper.query(conn, "SELECT NEXT_BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
				rs = dbHelper.query(conn, "SELECT X, Y FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + rs.getInt(1) + ";");
				tb.setEndCoordinates(rs.getDouble(1), rs.getDouble(2));
				//
				// Determines the neighboring blocks and if it is a switch.
				//
				rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
				if (rs.next()) {
					tb.setPrevBlockId(rs.getInt(4));
					tb.setPrevBlockDir(rs.getInt(5));
					tb.setNextBlockId(rs.getInt(6));
					tb.setNextBlockDir(rs.getInt(7));
					tb.setIsSwitch(rs.getInt(9) != 0);
					if (tb.isIsSwitch()) {
						tb.setSwitchBlockId(rs.getInt(8));
						tb.setSwitchDirection(rs.getInt(9));
						tb.setSwitchPosition(rs.getInt(10));
					}
				}
				//
				// Determines if the block has a crossing.
				//
				rs = dbHelper.query(conn, "SELECT * FROM CROSSINGS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
				tb.setIsCrossing(rs.next());
				//
				// Determines if the block has a station.
				//
				rs = dbHelper.query(conn, "SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
				tb.setIsStation(rs.next());
				rs.close();
				conn.close();
			} else {
				System.out.println("Invalid block. " + line + " " + block);
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return tb;
	}

	/**
	 * Shifts switches to be more magical. :)
	 * Forward going switches are shifted back one.
	 *
	 * @param tbs
	 */
	private void shiftSwitches(ArrayList<TrackBlock> tbs) {
		for (int i = 0; i < tbs.size(); i++) {
			TrackBlock oldSwitch = tbs.get(i);
			//
			// If it's a forward switch, switches the switch info to the previous block.
			//
			if (oldSwitch.isSwitch && oldSwitch.switchDirection > 0) {
				TrackBlock newSwitch = tbs.get(i - 1);
				newSwitch.isSwitch = true;
				newSwitch.switchBlockId = oldSwitch.switchBlockId;
				newSwitch.switchDirection = oldSwitch.switchDirection;
				newSwitch.switchPosition = oldSwitch.switchPosition;
				oldSwitch.isSwitch = false;
				//
				// The next block of blocks connected to forward switch get shifted back.
				//
				for (int j = 0; j < tbs.size(); j++) {
					TrackBlock switchBlock = tbs.get(j);
					if (newSwitch.switchBlockId == switchBlock.block && newSwitch.line.equalsIgnoreCase(switchBlock.line)) {
						switchBlock.nextBlockId = newSwitch.block;
					}
				}
			}
		}
	}

	/**
	 * Flips a switch if input line and block is valid switch.
	 *
	 * @param line
	 * @param block
	 * @return Whether or not the switch was successfully flipped.
	 */
	public static boolean flipSwitch(String line, int block) {
		boolean success = false;
		TrackBlock tb = getBlock(line, block);
		if (tb != null && tb.isSwitch) {
			int mainBlock = tb.switchDirection < 0 ? tb.prevBlockId : tb.nextBlockId;
			int switchBlock = tb.switchBlockId;
			if (tb.switchPosition == mainBlock) {
				tb.switchPosition = switchBlock;
			} else {
				tb.switchPosition = mainBlock;
			}
			success = true;
			if (tmf != null) {
				tmf.refreshTables();
			}
		} else {
			System.out.println("Not a switch.");
		}
		return success;
	}

	/**
	 * Gets the two branches of a switch.
	 *
	 * @param line
	 * @param block
	 * @return
	 */
	public int[] getBranchesOfSwitch(String line, int block) {
		TrackBlock tb = getBlock(line, block);
		if (tb == null || !tb.isSwitch) {
			return null;
		}
		int[] branches = {tb.switchBlockId, tb.switchDirection > 0 ? tb.nextBlockId : tb.prevBlockId};
		return branches;
	}

	/**
	 * Retrieves a Station.
	 *
	 * @param line
	 * @param block
	 * @return Station
	 */
	public static Station getStation(String line, int block) {
		for (Station s : stations) {
			if (s.line.equalsIgnoreCase(line) && s.block == block) {
				return s;
			}
		}
		System.out.println("Station not found.");
		return null;
	}

	/**
	 * Gets station data from the database.
	 *
	 * @param line
	 * @param block
	 * @return Station
	 */
	private Station getStationFromDatabase(String line, int block) {
		if (!doTablesExist()) {
			return null;
		}
		Station s = null;
		line = line.toLowerCase();
		try {
			try (Connection conn = dbHelper.getConnection()) {
				ResultSet rs = dbHelper.query(conn, "SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
				if (rs.next()) {
					s = new Station(line, block);
					s.setSection(rs.getString(2).charAt(0));
					s.setName(rs.getString(4));
				} else {
					System.out.println("Invalid station.");
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return s;
	}

	/**
	 * Retrieves a Crossing.
	 *
	 * @param line
	 * @param block
	 * @return Crossing
	 */
	public static Crossing getCrossing(String line, int block) {
		for (Crossing c : crossings) {
			if (c.line.equalsIgnoreCase(line) && c.block == block) {
				return c;
			}
		}
		System.out.println("Crossing not found.");
		return null;
	}

	/**
	 * Gets a crossing from the database.
	 *
	 * @param line
	 * @param block
	 * @return Crossing
	 */
	private Crossing getCrossingFromDatabase(String line, int block) {
		if (!doTablesExist()) {
			return null;
		}
		Crossing c = null;
		try {
			try (Connection conn = dbHelper.getConnection()) {
				ResultSet rs = dbHelper.query(conn, "SELECT * FROM CROSSINGS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
				if (rs.next()) {
					c = new Crossing(line, block);
					c.setSignal(rs.getBoolean(3));
				} else {
					System.out.println("Invalid crossing.");
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return c;
	}

	/**
	 * Sets a block message to a specific block.
	 *
	 * @param line
	 * @param block
	 * @param tmc
	 */
	public static void setBlockMessage(String line, int block, TrackMovementCommand tmc) {
		for (TrainData td : trains) {
			if (td.trackBlock.line.equalsIgnoreCase(line) && td.trackBlock.block == block) {
				td.trainModel.trackCircuit().send(tmc);
				td.trackBlock.message = "Speed: " + tmc.speed + ", Auth: " + tmc.authority;
			}
		}
	}

	/**
	 * Opens and closes a track block for maintenance.
	 *
	 * @param line
	 * @param block
	 * @param maintain
	 */
	public static void setMaintenance(String line, int block, boolean maintain) {
		TrackBlock tb = getBlock(line, block);
		if (tb != null) {
			tb.closedForMaintenance = maintain;
			tb.isOccupied = maintain;
			if (tmf != null) {
				tmf.refreshTables();
			}
		}
	}

	/**
	 * Sets the occupancy of a track block.
	 *
	 * @param line
	 * @param block
	 * @param occupied
	 */
	protected static void setOccupancy(String line, int block, boolean occupied) {
		TrackBlock tb = getBlock(line, block);
		if (tb != null && block != 0 && !tb.closedForMaintenance) {
			tb.isOccupied = occupied;
			if (tmf != null) {
				tmf.refreshTables();
			}
		}
	}

	public static int exchangePassengers(int departing) {
		int boarding = Station.generatePassengers();

//        ctc.updatePassengers(departing, boarding, train, station);
		return boarding;
	}

	/**
	 * Sets the power status of a track block.
	 *
	 * @param line
	 * @param block
	 * @param power
	 */
	protected static void setPower(String line, int block, boolean on) {
		TrackBlock tb = getBlock(line, block);
		if (tb != null) {
			tb.isPowerOn = on;
			if (tmf != null) {
				tmf.refreshTables();
			}
		}
	}

	/**
	 * Sets the crossing signal for a crossing.
	 *
	 * @param line
	 * @param block
	 * @param signal
	 */
	public static void setCrossingSignal(String line, int block, boolean signal) {
		Crossing c = getCrossing(line, block);
		if (c != null) {
			c.signal = signal;
			if (tmf != null) {
				tmf.refreshTables();
			}
		}
	}

	/**
	 * Turns the track heater on and off.
	 *
	 * @param line
	 * @param block
	 * @param heaterStatus
	 */
	public static void setHeater(String line, int block, boolean heaterStatus) {
		TrackBlock tb = getBlock(line, block);
		if (tb != null && tb.isStation) {
			Station s = getStation(line, block);
			s.heater = heaterStatus;
			if (tmf != null) {
				tmf.refreshTables();
			}
		}
	}

	/**
	 * Gets the first block out of the yard.
	 *
	 * @param line
	 * @return TrackBlock
	 */
	public static TrackBlock getFirstBlock(String line) {
		for (TrackBlock tb : blocks) {
			if (tb.block == 0 && tb.line.equalsIgnoreCase(line)) {
				return getBlock(tb.line, tb.nextBlockId);
			}
		}
		System.out.println("Track block not found.");
		return null;
	}

	/**
	 * Returns the default route of a line.
	 *
	 * @param line
	 * @return Array of ints with the blocks traversed
	 */
	public static ArrayList<Integer> getDefaultLine(String line) {
		ArrayList<Integer> defaultLine = new ArrayList<>();
		// Red line temporarily hard coded
		if (line.equalsIgnoreCase("red")) {
			int[] redLine = {9, 8, 7, 6, 5, 4, 3, 2, 1, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
				37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
				53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 52, 51,
				50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35,
				34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19,
				18, 17, 16, 15, 1, 2, 3, 4, 5, 6, 7, 8, 9};
			for (int i : redLine) {
				defaultLine.add(i);
			}
			return defaultLine;
		}
		//
		// Iterates through blocks to find valid route.
		//
		try {
			TrackBlock first = getFirstBlock(line);
			int swit = 0;
			boolean valid = false;
			int cur = first.block;
			int prev = first.switchDirection > 0 ? 999 : -1;

			try (Connection conn = dbHelper.getConnection()) {
				while (swit != 0 || !valid) {
					ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + cur);
					if (rs.next()) {
						defaultLine.add(cur);
						if (cur > prev && rs.getInt(7) == 1) {
							prev = cur;
							cur = rs.getInt(6);
							valid = rs.getInt(9) == 1;
						} else if (cur < prev && rs.getInt(5) == 1) {
							prev = cur;
							cur = rs.getInt(4);
							valid = rs.getInt(9) == -1;
						} else {
							prev = cur;
							cur = rs.getInt(8);
							valid = false;
						}
						if (first != null && first.block == prev) {
							first = null;
							continue;
						}
						swit = rs.getInt(8);
					} else {
						break;

					}
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class
					.getName()).log(Level.SEVERE, null, ex);
		}
		return defaultLine;
	}

	/**
	 * Gets total number of track blocks.
	 *
	 * @return block count
	 */
	public static int getBlockCount() {
		if (!doTablesExist()) {
			return 0;
		}
		int count = 0;
		try {
			try (Connection conn = dbHelper.getConnection()) {
				ResultSet rs = dbHelper.query(conn, "SELECT COUNT(BLOCK) FROM BLOCKS;");
				if (rs.next()) {
					count = rs.getInt(1);
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return count;
	}

	/**
	 * Gets total number of track blocks for a line.
	 *
	 * @param line
	 * @return block count
	 */
	public static int getBlockCount(String line) {
		if (!doTablesExist()) {
			return 0;
		}
		int count = 0;
		line = line.toLowerCase();
		try {
			try (Connection conn = dbHelper.getConnection()) {
				ResultSet rs = dbHelper.query(conn, "SELECT COUNT(BLOCK) FROM BLOCKS WHERE LINE='" + line + "';");
				if (rs.next()) {
					count = rs.getInt(1);
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return count;
	}

	/**
	 * Sets a message to the yard.
	 *
	 * @param trainId
	 * @param driverId
	 * @param tmc
	 */
	public void setYardMessage(int trainId, int driverId, TrackMovementCommand tmc) {
		for (TrainData td : trains) {
			if (td.trainModel.id() == trainId) {
				String line = td.trackBlock.line;
				if (tmc.authority > 0) {
					td.trainModel.slew(new Pose(getFirstBlock(line).start,
							line.equalsIgnoreCase("green") ? GREEN_LINE_ORIENTATION : RED_LINE_ORIENTATION));
				}
				td.trainModel.trackCircuit().send(tmc);
			}
		}
	}

	/**
	 * Registers train locally. Sets its initial location.
	 *
	 * @param tm
	 * @param line
	 */
	public void registerTrain(TrainModel tm, String line) {
		line = line.toLowerCase();
		if (doTablesExist()) {
			trains.add(new TrainData(tm, getYardBlock(line)));
			tm.slew(new Pose(getYardBlock(line).start, GREEN_LINE_ORIENTATION));
		}
	}

	/**
	 * Retrieves a yard block for a specific line.
	 *
	 * @param line
	 * @return
	 */
	public TrackBlock getYardBlock(String line) {
		for (TrackBlock tb : blocks) {
			if (tb.line.equalsIgnoreCase(line) && tb.block == 0) {
				return tb;
			}
		}
		return null;
	}

	/**
	 * Returns the closest track block to a given GlobalCoordinate.
	 *
	 * @param gc
	 * @param line
	 * @return TrackBlock
	 */
	public TrackBlock getClosestBlock(GlobalCoordinates gc, String line) {
		line = line.toLowerCase();
		int totalBlocks = getBlockCount(line);
		double minDist = 9999;
		TrackBlock closest = null;
		for (int i = 0; i <= totalBlocks; i++) {
			TrackBlock temp = getBlock(line, i);
			if (closest == null) {
				closest = temp;
			}
			//
			// Calculates distance between a point and block. Keeps if it's shorter.
			//
			double tempDist = getDistanceTo(temp, gc);
			if (tempDist < minDist
					|| (tempDist == minDist && temp.block > closest.block)) {
				minDist = tempDist;
				closest = temp;
			}
		}
		return closest;
	}

	/**
	 * Returns the side of a track block a train is on based on its current
	 * coordinate location.
	 *
	 * Returns true if it's on the first half of the track block, false if on
	 * back half.
	 *
	 * @param gc
	 * @param line
	 * @param block
	 * @return
	 */
	public boolean getSide(GlobalCoordinates gc, String line, int block) {
		line = line.toLowerCase();
		TrackBlock tb = getBlock(line, block);
		double startDistance = Math.sqrt(Math.pow(gc.latitude() - tb.start.latitude(), 2) + Math.pow(gc.longitude() - tb.start.longitude(), 2));
		double endDistance = Math.sqrt(Math.pow(gc.latitude() - tb.end.latitude(), 2) + Math.pow(gc.longitude() - tb.end.longitude(), 2));
		return startDistance < endDistance;
	}

	/**
	 * Gets the distance from a GlobalCoordinate to a specific track block.
	 *
	 * @param tb
	 * @param gc
	 * @return distance
	 */
	private double getDistanceTo(TrackBlock tb, GlobalCoordinates gc) {
		return getDistanceTo(tb.line, tb.block, gc);
	}

	/**
	 * Gets the distance from a GlobalCoordinate to a specific track block.
	 *
	 * @param line
	 * @param block
	 * @param gc
	 * @return distance
	 */
	public double getDistanceTo(String line, int block, GlobalCoordinates gc) {
		TrackBlock tb = getBlock(line, block);
		double minDist = 99999;
		double tempDist;
		for (int i = 0; i <= tb.length; i += 5) {
			tempDist = gc.distanceTo(getPositionAlongBlock(tb, i));
			if (tempDist < minDist) {
				minDist = tempDist;
			}
		}
		return minDist;
	}

	/**
	 * Gets the coordinates for a certain distance along a specific track block.
	 *
	 * @param tb
	 * @param meters
	 * @return GlobalCoordinates
	 */
	private GlobalCoordinates getPositionAlongBlock(TrackBlock tb, double meters) {
		return getPositionAlongBlock(tb.line, tb.block, meters);
	}

	/**
	 * Gets the coordinates for a certain distance along a specific track block.
	 *
	 * @param line
	 * @param block
	 * @param meters
	 * @return GlobalCoordinates
	 */
	public GlobalCoordinates getPositionAlongBlock(String line, int block, double meters) {
		TrackBlock tb = getBlock(line, block);
		if (meters > tb.length) {
			return null;
		}
		// Special case if block is the yard
		if (tb.length == 0) {
			return tb.start;
		}
		double newX, newY;
		//
		// Math
		//
		if (tb.curvature == 0) {
			double xDiff = tb.xEnd - tb.xStart;
			double yDiff = tb.yEnd - tb.yStart;
			double xDist = xDiff * meters / tb.length;
			double yDist = yDiff * meters / tb.length;

			newX = tb.xStart + xDist;
			newY = tb.yStart + yDist;
		} else {
			boolean clockwise = tb.curvature > 0;
			double radius = Math.sqrt(Math.pow(tb.xStart - tb.xCenter, 2) + Math.pow(tb.yStart - tb.yCenter, 2));
			double angle = Math.atan2(tb.yStart - tb.yCenter, tb.xStart - tb.xCenter);
			angle = clockwise ? angle - meters / radius : angle + meters / radius;

			newX = tb.xCenter + radius * Math.cos(angle);
			newY = tb.yCenter + radius * Math.sin(angle);
		}
		return GlobalCoordinates.ORIGIN.addYards(newY * TrackBlock.METER_TO_YARD_MULTIPLIER, newX * TrackBlock.METER_TO_YARD_MULTIPLIER);
	}

	/**
	 * Calculates the distance a given GlobalCoordinate is along a specific
	 * track block.
	 *
	 * @param line
	 * @param block
	 * @param gc
	 * @return distance
	 */
	public double getDistanceAlongBlock(String line, int block, GlobalCoordinates gc) {
		TrackBlock tb = getBlock(line, block);
		// Special case if block is the yard
		if (tb.length == 0) {
			return tb.start.distanceTo(gc);
		}
		double distance = 0;
		GlobalCoordinates lowerBound = tb.start;
		GlobalCoordinates upperBound = tb.end;
		//
		// Math
		//
		for (int i = 1; i <= 20; i++) {
			double temp = tb.length / (Math.pow(2, i));

			if (lowerBound.distanceTo(gc) < upperBound.distanceTo(gc)) {
				upperBound = getPositionAlongBlock(tb, distance + temp);
			} else if (lowerBound.distanceTo(gc) > upperBound.distanceTo(gc)) {
				lowerBound = getPositionAlongBlock(tb, distance + temp);
				distance += temp;
			} else {
				distance += temp;
				break;
			}
		}
		return distance;
	}

	// Counter for update cycle
	private int count = 0;

	@Override
	public void update(int time) {

		TrackBlock curBlock;
		for (TrainData td : trains) {
			//
			// Tries to update UI every two seconds.
			//
			if (count == 2000 / time) {
				//
				// Manages occupied track blocks.
				//
				curBlock = getClosestBlock(td.trainModel.location(), td.trackBlock.line);
				if (!curBlock.isOccupied) {
					setOccupancy(curBlock.line, curBlock.block, true);
				}
				if (td.trackBlock.block != curBlock.block) {
					if (td.trackBlock != null) {
						setOccupancy(td.trackBlock.line, td.trackBlock.block, false);
					}
					td.trackBlock = curBlock;
					//
					// Refreshes UI.
					//
				}
				if (tmf != null) {
					tmf.refreshTables();
				}
				count = 0;
			}
			//
			// Needs to check beacons constantly.
			//
			for (Beacon b : beacons) {
				if (td.trainModel.location().distanceTo(b.location) < 5) {
					td.trainModel.beaconRadio().send(new BeaconMessage(b.message));
				}
			}
		}
		count++;
	}

	/**
	 * Testing stuff, please ignore.
	 *
	 * @param line
	 */
	private void testing() {
		for (TrackBlock tb : blocks) {
			for (int i = 1; i < tb.length; i += 5) {
				GlobalCoordinates gc = getPositionAlongBlock(tb, i);
				if (gc != null) {
					TrackBlock close = getClosestBlock(gc, tb.line);
					if (close.block != tb.block) {
						System.out.println("problem expected: " + tb.block + ", returned: " + close.block + ", " + i);
					}
				}
			}
		}

		for (TrackBlock tb : blocks) {
			for (int i = 0; i <= tb.length; i++) {
				double d = getDistanceAlongBlock(tb.line, tb.block, getPositionAlongBlock(tb, i));
				if (i - d > 5) {
					System.out.println("problem " + tb.block + ", " + i + " meters");
				}
			}
		}
	}

	/**
	 * Class used for keeping track of the last block a train was on.
	 */
	protected class TrainData {

		public TrainModel trainModel;
		public TrackBlock trackBlock;

		public TrainData(TrainModel tm, TrackBlock tb) {
			this.trainModel = tm;
			this.trackBlock = tb;
		}
	}
}
