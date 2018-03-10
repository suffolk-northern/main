/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import track_controller.TrackController;
import train_model.TrainModel;

import updater.Updateable;

/**
 *
 * @author Gowest
 */
public class TrackModel implements Updateable {

    private static TrackModelFrame tmf;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        tmf = new TrackModelFrame();
        tmf.setLocationRelativeTo(null);
        tmf.setVisible(true);
        if (doTablesExist()) {
            TestFrame tf = new TestFrame(tmf);
            tf.setVisible(true);
        }
//        getStation("Green", 3);
//        for (int i = 1; i < 151; i++) {
//            TrackBlock tb = getBlock("Green", i);
////            System.out.println(tb.toString());
//            System.out.println("-------------------");
//        }
//        for (int i = 1; i < 77; i++) {
//            getBlock("Red", i);
//            System.out.println("-------------------");
//        }
    }

    /**
     * Checks if database tables exist.
     *
     * @return
     */
    protected static boolean doTablesExist() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "BLOCKS", null);
            if (!rs.next()) {
                return false;
            }
            rs = dbm.getTables(null, null, "CONNECTIONS", null);
            if (!rs.next()) {
                return false;
            }
            rs = dbm.getTables(null, null, "CROSSINGS", null);
            if (!rs.next()) {
                return false;
            }
            rs = dbm.getTables(null, null, "STATIONS", null);
            if (!rs.next()) {
                return false;
            }
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TrackModelFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
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
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
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

                rs = stat.executeQuery("SELECT NEXT_BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                rs = stat.executeQuery("SELECT X, Y FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + rs.getInt(1) + ";");
                tb.setEndCoordinates(rs.getDouble(1), rs.getDouble(2));

                rs = stat.executeQuery("SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                tb.setIsSwitch(rs.next());
                rs = stat.executeQuery("SELECT * FROM CROSSINGS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                tb.setIsCrossing(rs.next());
                rs = stat.executeQuery("SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                tb.setIsStation(rs.next());

                getConnections(line, block);
            } else {
                System.out.println("Invalid block.");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tb;
    }

    public static Station getStation(String line, int block) {
        if (!doTablesExist()) {
            return null;
        }
        Station s = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
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

    public static void setBlockMessage(String line, int block, String message) {
        if (doTablesExist()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");

                String query = "UPDATE BLOCKS SET MESSAGE=? WHERE LINE=? AND BLOCK=?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, message);
                preparedStmt.setString(2, line);
                preparedStmt.setInt(3, block);
                preparedStmt.executeUpdate();

                tmf.populateTables();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setOccupancy(String line, int block, boolean occupied) {
        if (doTablesExist()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
                Statement stat = conn.createStatement();

                String query = "UPDATE BLOCKS SET OCCUPIED=? WHERE LINE=? AND BLOCK=?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setBoolean(1, occupied);
                preparedStmt.setString(2, line);
                preparedStmt.setInt(3, block);
                preparedStmt.executeUpdate();

                tmf.populateTables();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setPower(String line, int block, boolean on) {
        if (doTablesExist()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");

                String query = "UPDATE BLOCKS SET POWER=? WHERE LINE=? AND BLOCK=?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setBoolean(1, on);
                preparedStmt.setString(2, line);
                preparedStmt.setInt(3, block);
                preparedStmt.executeUpdate();

                tmf.populateTables();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setHeater(String line, int block, boolean on) {
        if (doTablesExist()) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");

                String query = "UPDATE BLOCKS SET HEATER=? WHERE LINE=? AND BLOCK=?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setBoolean(1, on);
                preparedStmt.setString(2, line);
                preparedStmt.setInt(3, block);
                preparedStmt.executeUpdate();

                tmf.populateTables();
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
    * FOR EXPERIMENTAL PURPOSES AT THE MOMENT
     */
    public static void getConnections(String line, int block) {
        System.out.println(block);
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block);
            if (rs.next()) {
                if (rs.getInt(5) == 1) {
                    System.out.println("PREV: " + rs.getInt(4));
                }
                if (rs.getInt(7) == 1) {
                    System.out.println("NEXT: " + rs.getInt(6));
                }
                if (rs.getObject(9) != null) {
                    switch (rs.getInt(9)) {
                        case -1:
                            System.out.println("Going backwards: " + rs.getInt(8));
                            break;
                        case 1:
                            System.out.println("Going forwards: " + rs.getInt(8));
                            break;
                        default:
                            System.out.println("Only re-entries switch");
                            break;
                    }
                }
            } else {
                System.out.println("Invalid line or block.");
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<Integer> getDefaultGreenLine() {

        ArrayList<Integer> blocks = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT BLOCK FROM CONNECTIONS WHERE LINE='Green' AND SWITCH_BLOCK=-1 AND SWITCH_VALID=-2");

            int swit = 0;
            int valid = 0;
            int cur = rs.getInt(1);
            int prev = 0;

            while (swit != -1 || valid != 1) {
                rs = stat.executeQuery("SELECT * FROM CONNECTIONS WHERE LINE='Green' AND BLOCK=" + cur);
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
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return blocks;
    }

    public void registerTrain(TrainModel tm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void configureTrackController(TrackController tc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
