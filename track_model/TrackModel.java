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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gowest
 */
public class TrackModel {

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
        for (int i = 1; i < 151; i++) {
            TrackBlock tb = getBlock("Green", i);
//            System.out.println(tb.toString());
            System.out.println("-------------------");
        }
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

    public int[] getDefaultGreenLine() {
        return new int[]{
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
            79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
            96, 97, 98, 99, 100, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 101,
            102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115,
            116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
            130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
            144, 145, 146, 147, 148, 149, 150, 29, 28, 27, 26, 25, 24, 23, 22, 21,
            20, 19, 18, 17, 16, 15, 14, 13, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
            12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
            29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58
        };
    }

}
