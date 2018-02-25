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
//        TrackModelFrame tmf = new TrackModelFrame();
//        tmf.setLocationRelativeTo(null);
//        tmf.setVisible(true);
//        if (doTablesExist()) {
//            TestFrame tf = new TestFrame(tmf);
//            tf.setVisible(true);
//        }
//        for (int i = 1; i < 151; i++) {
//            getBlock("Green", i);
//            System.out.println("-------------------");
//        }
        for (int i = 1; i < 77; i++) {
            getBlock("Red", i);
            System.out.println("-------------------");
        }
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

    public static String getBlock(String line, int block) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery("SELECT * FROM BLOCKS "
                    + "WHERE LINE='" + line + "' AND ID=" + block + ";");
            if (rs.next()) {
                Object rowData[] = {rs.getString(2), rs.getString(3), rs.getInt(1), rs.getFloat(4), rs.getFloat(5), rs.getFloat(6), rs.getInt(7), rs.getInt(8), rs.getBoolean(9), rs.getString(10), rs.getBoolean(11),
                    (boolean) rs.getBoolean(11) ? "ON" : "OFF", rs.getString(12)};
                System.out.println("HI I'M BLOCK: " + rowData[2]);
                getConnections(line, block);
            } else {
                System.out.println("PENIS");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
                        System.out.println(rs.getInt(8));
                    } else if (rs.getInt(8) == block) {
                        System.out.println(rs.getInt(1));
                    } else {
                        System.out.println("MY ANUS ITCHES");
                    }
                } else {
                    System.out.println("PENIX");
                }
            } else {
                System.out.println("PENIS");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
