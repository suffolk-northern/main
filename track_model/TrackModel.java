/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private static final DbHelper dbHelper = new DbHelper();

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
//        for (int i = 1; i < 151; i++) {
//            TrackBlock tb = getBlock("Green", i);
//            System.out.println(tb.toString());
//            System.out.println("-------------------");
//        }
    }

    /**
     * Checks if database tables exist.
     *
     * @return
     */
    protected static boolean doTablesExist() {
        dbHelper.connect();
        boolean exist = dbHelper.tableExists("BLOCKS")
                && dbHelper.tableExists("CONNECTIONS")
                && dbHelper.tableExists("CROSSINGS")
                && dbHelper.tableExists("STATIONS");
        dbHelper.close();
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
        try {
            dbHelper.connect();
            ResultSet rs = dbHelper.query("SELECT * FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
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

                rs = dbHelper.query("SELECT NEXT_BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                rs = dbHelper.query("SELECT X, Y FROM BLOCKS WHERE LINE='" + line + "' AND BLOCK=" + rs.getInt(1) + ";");
                tb.setEndCoordinates(rs.getDouble(1), rs.getDouble(2));

                rs = dbHelper.query("SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + " AND SWITCH_BLOCK;");
                tb.setIsSwitch(rs.next());
                rs = dbHelper.query("SELECT * FROM CROSSINGS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                tb.setIsCrossing(rs.next());
                rs = dbHelper.query("SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
                tb.setIsStation(rs.next());

                dbHelper.close();
            } else {
                System.out.println("Invalid block.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tb;
    }

    public static void flipSwitch(String line, int block) {
        try {
            dbHelper.connect();
            ResultSet rs = dbHelper.query("SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block + " AND SWITCH_BLOCK;");
            if (rs.next()) {
                int mainBlock = rs.getInt(9) < 0 ? rs.getInt(4) : rs.getInt(6);
                int switchBlock = rs.getInt(8);

                String query = "UPDATE CONNECTIONS SET CURRENT_SETTING=? WHERE LINE=? AND BLOCK=?";
                if (rs.getInt(10) == mainBlock) {
                    Object[] values = {switchBlock, line, block};
                    dbHelper.execute(query, values);
                } else {
                    Object[] values = {mainBlock, line, block};
                    dbHelper.execute(query, values);
                }

                tmf.populateTables();
            } else {
                System.out.println("Not a switch.");
            }
            dbHelper.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Station getStation(String line, int block) {
        if (!doTablesExist()) {
            return null;
        }
        Station s = null;
        try {
            dbHelper.connect();
            ResultSet rs = dbHelper.query("SELECT * FROM STATIONS WHERE LINE='" + line + "' AND BLOCK=" + block + ";");
            if (rs.next()) {
                s = new Station(line, block);
                s.setSection(rs.getString(2).charAt(0));
                s.setName(rs.getString(4));
                s.setBeacon(rs.getString(6));
            } else {
                System.out.println("Invalid station.");
            }
            dbHelper.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return s;
    }

    public static void setBlockMessage(String line, int block, String message) {
        if (doTablesExist()) {
            dbHelper.connect();
            String query = "UPDATE BLOCKS SET MESSAGE=? WHERE LINE=? AND BLOCK=?";
            Object[] values = {message, line, block};
            dbHelper.execute(query, values);
            dbHelper.close();

            tmf.populateTables();
        }
    }

    public static void setOccupancy(String line, int block, boolean occupied) {
        if (doTablesExist()) {
            dbHelper.connect();
            String query = "UPDATE BLOCKS SET OCCUPIED=? WHERE LINE=? AND BLOCK=?";
            Object[] values = {occupied, line, block};
            dbHelper.execute(query, values);
            dbHelper.close();

            tmf.populateTables();
        }
    }

    public static void setPower(String line, int block, boolean on) {
        if (doTablesExist()) {
            dbHelper.connect();
            String query = "UPDATE BLOCKS SET POWER=? WHERE LINE=? AND BLOCK=?";
            Object[] values = {on, line, block};
            dbHelper.execute(query, values);
            dbHelper.close();

            tmf.populateTables();
        }
    }

    public static void setHeater(String line, int block, boolean on) {
        if (doTablesExist()) {
            dbHelper.connect();
            String query = "UPDATE BLOCKS SET HEATER=? WHERE LINE=? AND BLOCK=?";
            Object[] values = {on, line, block};
            dbHelper.execute(query, values);
            dbHelper.close();

            tmf.populateTables();
        }
    }

    /*
    * FOR EXPERIMENTAL PURPOSES AT THE MOMENT
     */
    public static void getConnections(String line, int block) {
        System.out.println(block);
        try {
            dbHelper.connect();
            ResultSet rs = dbHelper.query("SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + block);
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
            dbHelper.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static TrackBlock getFirstBlock(String line) {
        TrackBlock tb = null;
        try {
            dbHelper.connect();
            ResultSet rs = dbHelper.query("SELECT BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND SWITCH_BLOCK=-1 AND SWITCH_VALID=-2");
            tb = getBlock(line, rs.getInt(1));
            dbHelper.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrackModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tb;
    }

    public static ArrayList<Integer> getDefaultLine(String line) {
        ArrayList<Integer> blocks = new ArrayList<>();
        try {
            dbHelper.connect();
            ResultSet rs = dbHelper.query("SELECT BLOCK FROM CONNECTIONS WHERE LINE='" + line + "' AND SWITCH_BLOCK=-1 AND SWITCH_VALID=-2");

            int swit = 0;
            int valid = 0;
            int cur = rs.getInt(1);
            int prev = 0;

            while (swit != -1 || valid != 1) {
                rs = dbHelper.query("SELECT * FROM CONNECTIONS WHERE LINE='" + line + "' AND BLOCK=" + cur);
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
            dbHelper.close();
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
    public void update(int time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
