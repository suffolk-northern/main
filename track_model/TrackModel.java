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

    protected static ArrayList<TrainData> trains = new ArrayList<>();
    protected static ArrayList<TrackBlock> blocks = new ArrayList<>();
    protected static ArrayList<Crossing> crossings = new ArrayList<>();
    protected static ArrayList<Station> stations = new ArrayList<>();

    private static final Orientation GREEN_LINE_ORIENTATION = Orientation.radians(0.9 * Math.PI);
    private static final Orientation RED_LINE_ORIENTATION = Orientation.radians(0.3737 * Math.PI);

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
        tb.setStartCoordinates(600, -2100);
        tb.setEndCoordinates(600, -2100);

        if (doTablesExist()) {
            try {
                try (Connection conn = dbHelper.getConnection()) {
                    // Retrive yard next block
                    ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND SWITCH_BLOCK=0 "
                            + "AND (ABS(SWITCH_VALID)=2 OR (SWITCH_VALID=1 AND PREV_VALID=1) OR (SWITCH_VALID=-1 AND NEXT_VALID=1));");
                    while (rs.next()) {
                        tb.nextBlockId = rs.getInt(3);
                        tb.nextBlockDir = rs.getInt(9) < 0 ? 1 : 0;
                    }
                    // Retrive yard previous block
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
        if (tb != null && block != 0) {
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
            if (tb.line.equalsIgnoreCase(line) && tb.switchBlockId == 0
                    && (Math.abs(tb.switchDirection) == 2
                    || (tb.switchDirection == 1 && tb.prevBlockDir == 1)
                    || (tb.switchDirection == -1 && tb.nextBlockDir == 1))) {
                return tb;
            }
        }
        System.out.println("Track block not found.");
        return null;
    }

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
                String line = td.trackBlock.line;
                if (tmc.authority > 0) {
                    td.trainModel.slew(new Pose(getFirstBlock(line).start,
                            line.equalsIgnoreCase("green") ? GREEN_LINE_ORIENTATION : RED_LINE_ORIENTATION));
                }
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
            tm.slew(new Pose(getYardBlock(line).start, GREEN_LINE_ORIENTATION));
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
            double tempDist = getDistanceTo(temp, gc);
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

    private double getDistanceTo(TrackBlock tb, GlobalCoordinates gc) {
        return getDistanceTo(tb.line, tb.block, gc);
    }

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

    private GlobalCoordinates getPositionAlongBlock(TrackBlock tb, double meters) {
        return getPositionAlongBlock(tb.line, tb.block, meters);
    }

    public GlobalCoordinates getPositionAlongBlock(String line, int block, double meters) {
        TrackBlock tb = getBlock(line, block);
        if (meters > tb.length) {
            return null;
        }
        // Special case if block is yard
        if (tb.length == 0) {
            return tb.start;
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
                curBlock = getClosestBlock(td.trainModel.location(), td.trackBlock.line);
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
            if (tmf != null) {
                tmf.refreshTables();
            }
            count = 0;
        }
        count++;
    }

    private void testing(String line) {
        for (int j = 1; j <= getBlockCount(line); j++) {
            TrackBlock tb = getBlock(line, j);
            for (int i = 1; i < tb.length; i += 5) {
                GlobalCoordinates gc = getPositionAlongBlock(tb, i);
                if (gc != null) {
                    System.out.println(j + " " + gc.latitude() + "," + gc.longitude() + " Closest: " + getClosestBlock(gc, line).block);

                }
            }
        }
    }

    protected class TrainData {

        public TrainModel trainModel;
        public TrackBlock trackBlock;

        public TrainData(TrainModel tm, TrackBlock tb) {
            this.trainModel = tm;
            this.trackBlock = tb;
        }
    }
}
