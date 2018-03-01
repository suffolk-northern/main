/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trackmodel;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TrackModelFrame tmf = new TrackModelFrame();
        tmf.setLocationRelativeTo(null);
        tmf.setVisible(true);
        if (doTablesExist()) {
            TestFrame tf = new TestFrame(tmf);
            tf.setVisible(true);
        }
//        for (int i = 1; i < 2; i++) {
//            getBlock("Green", 2);
//            System.out.println("-------------------");
//        }
//        for (int i = 1; i < 77; i++) {
//            getBlock("Red", i);
//            System.out.println("-------------------");
//        }
    }

    public static boolean doTablesExist() {
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

    public static TrackBlock getBlock(String line, int block) {
        TrackBlock tb = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM BLOCKS "
                    + "WHERE LINE='" + line + "' AND ID=" + block + ";");
            if (rs.next()) {
                System.out.println("HI I'M BLOCK: " + block);
                tb = new TrackBlock(line, block);
                tb.setSection(rs.getString(3).charAt(0));
                tb.setLength(rs.getFloat(4));
                tb.setCurvature(rs.getFloat(5));
                tb.setGrade(rs.getFloat(6));
                tb.setDirection(rs.getInt(7));
                tb.setSpeedLimit(rs.getInt(8));
                tb.setIsUnderground(rs.getBoolean(9));
                tb.setIsPowerOn(rs.getBoolean(10));
                tb.setIsOccupied(rs.getBoolean(11));
                tb.setIsHeaterOn(rs.getBoolean(12));
                
                rs = stat.executeQuery("SELECT * FROM SWITCHES " + "WHERE LINE='" + line + "' AND ID=" + block + ";");
                tb.setIsSwitch(rs.next());
                rs = stat.executeQuery("SELECT * FROM CROSSINGS " + "WHERE LINE='" + line + "' AND ID=" + block + ";");
                tb.setIsCrossing(rs.next());
                rs = stat.executeQuery("SELECT * FROM STATIONS " + "WHERE LINE='" + line + "' AND ID=" + block + ";");
                tb.setIsStation(rs.next());
                
                getConnections(line, block);
            } else {
                System.out.println("Invalid block.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tb;
    }

    public static String getConnections(String line, int block) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT DIRECTION FROM BLOCKS "
                    + "WHERE LINE='" + line + "' AND ID=" + block + ";");
            if (rs.next()) {
                if (rs.getInt(1) == 0 || rs.getInt(1) == 1) {
                    System.out.println(block + 1);
                }
                if (rs.getInt(1) == 0 || rs.getInt(1) == -1) {
                    System.out.println(block - 1);
                }
                String swit = "SELECT * FROM SWITCHES WHERE LINE='%s' AND (ID=%s OR BLOCK_OUT_A=%s OR BLOCK_OUT_B = %s);";
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
