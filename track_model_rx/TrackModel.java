/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trackmodel;

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
        setBlockMessage("Green", -6, "Jews");
//        for (int i = 1; i < 2; i++) {
//            TrackBlock tb = getBlock("Green", 2);
//            System.out.println(tb.toString());
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
            rs = dbm.getTables(null, null, "SWITCHES", null);
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
                tb.setDirection(rs.getInt(7));
                tb.setSpeedLimit(rs.getInt(8));
                tb.setIsUnderground(rs.getBoolean(9));
                tb.setIsPowerOn(rs.getBoolean(10));
                tb.setIsOccupied(rs.getBoolean(11));
                tb.setIsHeaterOn(rs.getBoolean(12));
                tb.setMessage(rs.getString(13));

                rs = stat.executeQuery("SELECT * FROM SWITCHES WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
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
    public static String getConnections(String line, int block) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT DIRECTION FROM BLOCKS "
                    + "WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
            if (rs.next()) {
                if (rs.getInt(1) == 0 || rs.getInt(1) == 1) {
                    System.out.println(block + 1);
                }
                if (rs.getInt(1) == 0 || rs.getInt(1) == -1) {
                    System.out.println(block - 1);
                }
                String swit = "SELECT * FROM SWITCHES WHERE LINE='%s' AND (BLOCK=%s OR BLOCK_OUT_A=%s OR BLOCK_OUT_B = %s);";
                swit = String.format(swit, line, block, block, block);
                rs = stat.executeQuery(swit);
                if (rs.next()) {
                    if (rs.getInt(1) == block && rs.getInt(9) == 1) {
                        System.out.println("OUT B: " + rs.getInt(8));
                    } else if (rs.getInt(8) == block) {
                        System.out.println("CAN GO TO: " + rs.getInt(1));
                    } else {
                        System.out.println("UNAFFECTED");
                    }
                } else {
                    System.out.println("no switch");
                }
            } else {
                System.out.println("problem");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
