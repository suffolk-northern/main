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
    private static final DbHelper dbHelper = new DbHelper();

    private static final ArrayList<TrainData> trains = new ArrayList<>();

    private static final Orientation GREEN_LINE_ORIENTATION = Orientation.radians(0.9 * Math.PI);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launchTestUI();
    }

    public TrackModel() {
        for (int i = 1; i <= getBlockCount("Green"); i++) {
            if (getBlock("Green", i).isOccupied) {
                setOccupancy("Green", i, false);
            }
        }
    }

    public static void launchUI() {
        tmf = new TrackModelFrame(dbHelper);
        tmf.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        tmf.setLocationRelativeTo(null);
        tmf.setVisible(true);
    }

    public static void launchTestUI() {
        tmf = new TrackModelFrame(dbHelper);
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
            Connection conn = dbHelper.getConnection();
            exist = dbHelper.tableExists(conn, "BLOCKS")
                    && dbHelper.tableExists(conn, "CONNECTIONS")
                    && dbHelper.tableExists(conn, "CROSSINGS")
                    && dbHelper.tableExists(conn, "STATIONS");
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    /**
     * Retrieves specific information related to a block on a line.
     *
     * @param line
     * @param block
     * @return Track block object
     */
    public static TrackBlock getBlock(String line, int block) {
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
                tb.setIsPowerOn(rs.getBoolean(9));
                tb.setIsOccupied(rs.getBoolean(10));
                tb.setIsHeaterOn(rs.getBoolean(11));
                tb.setMessage(rs.getString(12));
                tb.setStartCoordinates(rs.getDouble(13), rs.getDouble(14));
                tb.setClosedForMaintenance(rs.getBoolean(17));
                tb.setCenterCoordinates(rs.getDouble(18), rs.getDouble(19));

                rs = dbHelper.query(conn, "SELECT NEXT_BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                rs = dbHelper.query(conn, "SELECT X, Y FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + rs.getInt(1) + ";");
                tb.setEndCoordinates(rs.getDouble(1), rs.getDouble(2));

                rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                if (rs.next()) {
                    tb.setPrevBlockId(rs.getInt(4));
                    tb.setNextBlockId(rs.getInt(6));
                    tb.setIsSwitch(rs.getInt(8) != 0);
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
                System.out.println("Invalid block.");
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
        line = line.toLowerCase();
        try {
            Connection conn = dbHelper.getConnection();
            ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + " AND SWITCH_BLOCK;");
            if (rs.next()) {
                int mainBlock = rs.getInt(9) < 0 ? rs.getInt(4) : rs.getInt(6);
                int switchBlock = rs.getInt(8);

                String query = "UPDATE CONNECTIONS SET CURRENT_SETTING=? WHERE LINE=? AND BLOCK=?";
                if (rs.getInt(10) == mainBlock) {
                    Object[] values = {switchBlock, line, block};
                    dbHelper.execute(conn, query, values);
                } else {
                    Object[] values = {mainBlock, line, block};
                    dbHelper.execute(conn, query, values);
                }
                success = true;
                if (tmf != null) {
                    tmf.refreshTables();
                }
            } else {
                System.out.println("Not a switch.");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    public static Station getStation(String line, int block) {
        if (!doTablesExist()) {
            return null;
        }
        Station s = null;
        line = line.toLowerCase();
        try {
            Connection conn = dbHelper.getConnection();
            ResultSet rs = dbHelper.query(conn, "SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
            if (rs.next()) {
                s = new Station(line, block);
                s.setSection(rs.getString(2).charAt(0));
                s.setName(rs.getString(4));
                s.setBeacon(rs.getString(6));
            } else {
                System.out.println("Invalid station.");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return s;
    }

    public static Crossing getCrossing(String line, int block) {
        if (!doTablesExist()) {
            return null;
        }
        Crossing c = null;
        try {
            Connection conn = dbHelper.getConnection();
            ResultSet rs = dbHelper.query(conn, "SELECT * FROM CROSSINGS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
            if (rs.next()) {
                c = new Crossing(line, block);
                c.setSignal(rs.getBoolean(3));
            } else {
                System.out.println("Invalid crossing.");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }

    public static void setBlockMessage(String line, int block, String message) {
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                String query = "UPDATE BLOCKS SET MESSAGE=? WHERE LINE=? AND BLOCK=?";
                Object[] values = {message, line, block};
                dbHelper.execute(conn, query, values);
                conn.close();

                if (tmf != null) {
                    tmf.refreshTables();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setBlockMessage(int trainId, int driverId, TrackMovementCommand tmc) {
        for (TrainData td : trains) {
            if (td.trainModel.id() == trainId) {
                td.trainModel.trackCircuit().send(tmc);
            }
        }
    }

    public static void setMaintenance(String line, int block, boolean maintain) {
        line = line.toLowerCase();
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                String query = "UPDATE BLOCKS SET MAINTENANCE=? WHERE LINE=? AND BLOCK=?";
                Object[] values = {maintain, line, block};
                dbHelper.execute(conn, query, values);
                conn.close();

                if (tmf != null) {
                    tmf.refreshTables();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setOccupancy(String line, int block, boolean occupied) {
        line = line.toLowerCase();
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                String query = "UPDATE BLOCKS SET OCCUPIED=? WHERE LINE=? AND BLOCK=?";
                Object[] values = {occupied, line, block};
                dbHelper.execute(conn, query, values);
                conn.close();

                if (tmf != null) {
                    tmf.refreshTables();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static int exchangePassengers(int departing) {
        int boarding = Station.generatePassengers();

//        ctc.updatePassengers(departing, boarding, train, station);
        return boarding;
    }

    public static void setPower(String line, int block, boolean on) {
        line = line.toLowerCase();
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                String query = "UPDATE BLOCKS SET POWER=? WHERE LINE=? AND BLOCK=?";
                Object[] values = {on, line, block};
                dbHelper.execute(conn, query, values);
                conn.close();

                if (tmf != null) {
                    tmf.refreshTables();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setCrossingSignal(String line, int block, boolean on) {
        line = line.toLowerCase();
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                String query = "UPDATE CROSSINGS SET SIGNAL=? WHERE LINE=? AND BLOCK=?";
                Object[] values = {on, line, block};
                dbHelper.execute(conn, query, values);
                conn.close();

                if (tmf != null) {
                    tmf.refreshTables();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setHeater(String line, int block, boolean on) {
        line = line.toLowerCase();
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                String query = "UPDATE BLOCKS SET HEATER=? WHERE LINE=? AND BLOCK=?";
                Object[] values = {on, line, block};
                dbHelper.execute(conn, query, values);
                conn.close();

                if (tmf != null) {
                    tmf.refreshTables();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    /*
//    * FOR EXPERIMENTAL PURPOSES AT THE MOMENT
//     */
//    private static void getConnections(String line, int block) {
//        try {
//            Connection conn = dbHelper.getConnection();
//            ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block);
//            if (rs.next()) {
//                if (rs.getInt(5) == 1) {
//                    System.out.println("PREV: " + rs.getInt(4));
//                }
//                if (rs.getInt(7) == 1) {
//                    System.out.println("NEXT: " + rs.getInt(6));
//                }
//                if (rs.getObject(9) != null) {
//                    switch (rs.getInt(9)) {
//                        case -1:
//                            System.out.println("Going backwards: " + rs.getInt(8));
//                            break;
//                        case 1:
//                            System.out.println("Going forwards: " + rs.getInt(8));
//                            break;
//                        default:
//                            System.out.println("Only re-entries switch");
//                            break;
//                    }
//                }
//            } else {
//                System.out.println("Invalid line or block.");
//            }
//            conn.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public static TrackBlock getFirstBlock(String line) {
        TrackBlock tb = null;
        line = line.toLowerCase();
        if (doTablesExist()) {
            try {
                Connection conn = dbHelper.getConnection();
                ResultSet rs = dbHelper.query(conn, "SELECT BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND SWITCH_BLOCK=-1 AND SWITCH_VALID=-2");
                tb = getBlock(line, rs.getInt(1));
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tb;
    }

    public static ArrayList<Integer> getDefaultLine(String line) {
        ArrayList<Integer> blocks = new ArrayList<>();
        try {
            int swit = 0;
            int valid = 0;
            int cur = getFirstBlock(line).block;
            int prev = 0;

            Connection conn = dbHelper.getConnection();
            while (swit != -1 || valid != 1) {
                ResultSet rs = dbHelper.query(conn, "SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + cur);
                if (rs.next()) {
                    blocks.add(cur);
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
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return blocks;
    }

    public static int getBlockCount(String line) {
        if (!doTablesExist()) {
            return 0;
        }
        int count = 0;
        line = line.toLowerCase();
        try {
            Connection conn = dbHelper.getConnection();
            ResultSet rs = dbHelper.query(conn, "SELECT COUNT(BLOCK) FROM BLOCKS WHERE LINE='" + line + "';");
            if (rs.next()) {
                count = rs.getInt(1);
            } else {
                System.out.println("Invalid line.");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    public TrackBlock getYardBlock() {
        return getBlock("YARD", 0);
    }

//    public void setYardMessage(String message) {
//        if (doTablesExist()) {
//            dbHelper.connect();
//            String query = "UPDATE BLOCKS SET MESSAGE=? WHERE BLOCK=-1";
//            Object[] values = {message};
//            dbHelper.execute(query, values);
//            dbHelper.close();
//
//            if (tmf != null) {
//                tmf.refreshTables();
//            }
//        }
//    }
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

    private void sendTrackCircuitMessage(TrackCircuit circuit) {
//        circuit.send();
    }

    /**
     * Registers train locally. Sets its initial location.
     *
     * @param tm
     * @param line
     */
    public void registerTrain(TrainModel tm, String line) {
        if (doTablesExist()) {
            trains.add(new TrainData(tm, getFirstBlock(line)));
            tm.slew(new Pose(getFirstBlock(line).start, GREEN_LINE_ORIENTATION));
        } else {
            JOptionPane.showMessageDialog(null, "Track database not found. Trains were not registered.\n\nPlease import track database.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
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
        int blocks = getBlockCount(line);
        double minDist = 9999;
        TrackBlock closest = null;
        for (int i = 1; i <= blocks; i++) {
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
