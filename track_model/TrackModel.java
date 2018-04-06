/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import train_model.communication.BeaconRadio;
import train_model.communication.TrackCircuit;
import train_model.communication.TrackMovementCommand;

import updater.Updateable;

/**
 *
 * @author Gowest
 */
public class TrackModel implements Updateable {

    private static int temperature;
    private static TrackModelFrame tmf;
    protected static final DbHelper dbHelper = new DbHelper();

    protected static final ArrayList<TrainData> trains = new ArrayList<>();
    protected static ArrayList<TrackBlock> blocks = new ArrayList<>();
    protected static ArrayList<Crossing> crossings = new ArrayList<>();
    protected static ArrayList<Station> stations = new ArrayList<>();

    private static final Orientation GREEN_LINE_ORIENTATION = Orientation.radians(0.9 * Math.PI);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TrackModel tm = new TrackModel();
        tm.launchTestUI();
    }

    public TrackModel() {
        initializeLocalArrays();
    }

    private TrackBlock generateYardBlock(String line) {
        TrackBlock tb = new TrackBlock(line, 0);
        tb.setStartCoordinates(265, -1500);
        return tb;
    }

    protected void initializeLocalArrays() {
        blocks = new ArrayList<>();
        crossings = new ArrayList<>();
        stations = new ArrayList<>();

        blocks.add(generateYardBlock("green"));
        blocks.add(generateYardBlock("red"));

        if (doTablesExist()) {
            try {
                try (Connection conn = dbHelper.getConnection()) {
                    ResultSet rs = dbHelper.query(conn, "SELECT LINE, BLOCK FROM BLOCKS WHERE BLOCK > 0;");
                    while (rs.next()) {
                        blocks.add(getBlockFromDatabase(rs.getString("Line"), rs.getInt("Block")));
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
            JOptionPane.showMessageDialog(null, "Track database not found. Trains were not registered.\n\nPlease import track database.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void launchUI() {
        tmf = new TrackModelFrame(this);
        tmf.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        tmf.setLocationRelativeTo(null);
        tmf.setVisible(true);
    }

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
     * @return
     */
    protected static boolean doTablesExist() {
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

    public static TrackBlock getBlock(String line, int block) {
        for (TrackBlock tb : blocks) {
            if (tb.line.equalsIgnoreCase(line) && tb.block == block) {
                return tb;
            }
        }
        System.out.println("Track block not found.");
        return null;
    }

    /**
     * Retrieves specific information related to a block on a line.
     *
     * @param line
     * @param block
     * @return Track block object
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
                tb = new TrackBlock(line, block);
                tb.setSection(rs.getString(2).charAt(0));
                tb.setLength(rs.getFloat(4));
                tb.setCurvature(rs.getFloat(5));
                tb.setGrade(rs.getFloat(6));
                tb.setSpeedLimit(rs.getInt(7));
                tb.setIsUnderground(rs.getBoolean(8));
                tb.setStartCoordinates(rs.getDouble(9), rs.getDouble(10));
                tb.setCenterCoordinates(rs.getDouble(13), rs.getDouble(14));

                rs = dbHelper.query(conn, "SELECT NEXT_BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                rs = dbHelper.query(conn, "SELECT X, Y FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + rs.getInt(1) + ";");
                tb.setEndCoordinates(rs.getDouble(1), rs.getDouble(2));

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
                rs = dbHelper.query(conn, "SELECT * FROM CROSSINGS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                tb.setIsCrossing(rs.next());
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

    public static Station getStation(String line, int block) {
        for (Station s : stations) {
            if (s.line.equalsIgnoreCase(line) && s.block == block) {
                return s;
            }
        }
        System.out.println("Station not found.");
        return null;
    }

    private static Station getStationFromDatabase(String line, int block) {
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
                    s.setBeacon(rs.getString(6));
                } else {
                    System.out.println("Invalid station.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return s;
    }

    public static Crossing getCrossing(String line, int block) {
        for (Crossing c : crossings) {
            if (c.line.equalsIgnoreCase(line) && c.block == block) {
                return c;
            }
        }
        System.out.println("Crossing not found.");
        return null;
    }

    private static Crossing getCrossingFromDatabase(String line, int block) {
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

    public static void setBlockMessage(String line, int block, TrackMovementCommand tmc) {
        for (TrainData td : trains) {
            if (td.trackBlock.line.equalsIgnoreCase(line) && td.trackBlock.block == block) {
                td.trainModel.trackCircuit().send(tmc);
            }
        }
    }

    public static void setMaintenance(String line, int block, boolean maintain) {
        TrackBlock tb = getBlock(line, block);
        if (tb != null) {
            tb.closedForMaintenance = maintain;
            if (tmf != null) {
                tmf.refreshTables();
            }
        }
    }

    public static void setOccupancy(String line, int block, boolean occupied) {
        TrackBlock tb = getBlock(line, block);
        if (tb != null) {
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

    public static void setPower(String line, int block, boolean on) {
        TrackBlock tb = getBlock(line, block);
        if (tb != null) {
            tb.isPowerOn = on;
            if (tmf != null) {
                tmf.refreshTables();
            }
        }
    }

    public static void setCrossingSignal(String line, int block, boolean signal) {
        Crossing c = getCrossing(line, block);
        if (c != null) {
            c.signal = signal;
            if (tmf != null) {
                tmf.refreshTables();
            }
        }
    }

    public static void setHeater(String line, int block, boolean heater) {
        TrackBlock tb = getBlock(line, block);
        if (tb != null) {
            tb.isHeaterOn = heater;
            if (tmf != null) {
                tmf.refreshTables();
            }
        }
    }

    public static TrackBlock getFirstBlock(String line) {
        for (TrackBlock tb : blocks) {
            if (tb.line.equalsIgnoreCase(line) && tb.switchBlockId == 0 && tb.switchDirection == -2) {
                return tb;
            }
        }
        System.out.println("Track block not found.");
        return null;
    }

    public static ArrayList<Integer> getDefaultLine(String line) {
        ArrayList<Integer> defaultLine = new ArrayList<>();
        try {
            int swit = 0;
            int valid = 0;
            int cur = getFirstBlock(line).block;
            int prev = 0;

            try (Connection conn = dbHelper.getConnection()) {
                while (swit != 0 || valid != 1) {
                    ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + cur);
                    if (rs.next()) {
                        defaultLine.add(cur);
                        if (cur > prev && rs.getInt(7) == 1) {
                            prev = cur;
                            cur = rs.getInt(6);
                        } else if (cur < prev && rs.getInt(5) == 1) {
                            prev = cur;
                            cur = rs.getInt(4);
                        } else {
                            prev = cur;
                            cur = rs.getInt(8);
                        }

                        swit = rs.getInt(8);
                        valid = rs.getInt(9);
                    } else {
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return defaultLine;
    }

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
                } else {
                    System.out.println("Invalid.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

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
                } else {
                    System.out.println("Invalid line.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    public void setYardMessage(int trainId, int driverId, TrackMovementCommand tmc) {
        for (TrainData td : trains) {
            if (td.trainModel.id() == trainId) {
                td.trainModel.trackCircuit().send(tmc);
            }
        }
    }

    private void sendBeacon(BeaconRadio radio) {
//        radio.send();

    }

    /**
     * Registers train locally. Sets its initial location.
     *
     * @param tm
     * @param line
     */
    public void registerTrain(TrainModel tm, String line) {
        if (doTablesExist()) {
            trains.add(new TrainData(tm, getYardBlock(line)));
//            tm.slew(new Pose(getFirstBlock(line).start, GREEN_LINE_ORIENTATION));
        }
    }

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
     * Not mathematically sound, but good enough??? for our purposes
     *
     * @param gc
     * @param line
     * @return
     */
    public static TrackBlock getClosestBlock(GlobalCoordinates gc, String line) {
        line = line.toLowerCase();
        int totalBlocks = getBlockCount(line);
        double minDist = 9999;
        TrackBlock closest = null;
        for (int i = 1; i <= totalBlocks; i++) {
            TrackBlock temp = getBlock(line, i);
            if (closest == null) {
                closest = temp;
            }
            double tempDist = temp.getDistanceTo(gc);
            if (tempDist < minDist) {
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

    public double getDistanceTo(String line, int block, GlobalCoordinates gc) {
        TrackBlock tb = getBlock(line, block);
        double minDist = 9999;
        double latDiff, lonDiff;
        for (int i = 0; i < tb.length; i += 5) {
            latDiff = gc.latitude() - tb.getPositionAlongBlock(i).latitude();
            lonDiff = gc.longitude() - tb.getPositionAlongBlock(i).longitude();
            double tempDist = Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2));
            if (tempDist < minDist) {
                minDist = tempDist;
            }
        }
        return minDist;
    }

    public GlobalCoordinates getPositionAlongBlock(String line, int block, double meters) {
        TrackBlock tb = getBlock(line, block);
        if (meters > tb.length) {
            return null;
        }
        double newX, newY;

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

    int count = 0;

    @Override
    public void update(int time) {
        if (count == 2000 / time) {
            TrackBlock curBlock;
            for (TrainData td : trains) {
                curBlock = getClosestBlock(td.trainModel.location(), "Green");
//                System.out.println(td.trainModel.id() + ": " + curBlock.block);
                if (!curBlock.isOccupied) {
                    setOccupancy(curBlock.line, curBlock.block, true);
                }
                if (td.trackBlock.block != curBlock.block) {
                    if (td.trackBlock != null) {
                        setOccupancy(td.trackBlock.line, td.trackBlock.block, false);
                    }
                    td.trackBlock = curBlock;
                }
            }
            count = 0;
        }
        count++;
    }

    private static void testing() {
        for (int j = 1; j <= 150; j++) {
            TrackBlock tb = getBlock("Green", j);
            for (int i = 1; i < tb.length; i += 30) {
                GlobalCoordinates gc = tb.getPositionAlongBlock(i);
                if (gc != null) {
                    System.out.println(j + " " + gc.latitude() + "," + gc.longitude() + " Closest: " + getClosestBlock(gc, "Green").block);
                }
            }
        }
    }

    private class TrainData {

        public TrainModel trainModel;
        public TrackBlock trackBlock;

        public TrainData(TrainModel tm, TrackBlock tb) {
            this.trainModel = tm;
            this.trackBlock = tb;
        }
    }
}
