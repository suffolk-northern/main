package track_model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteDataSource;

public class DbHelper {

    private SQLiteDataSource ds = new SQLiteDataSource();
    private String url = "jdbc:sqlite:TrackModel.db";

    public DbHelper() {
        ds.setUrl(url);
    }

    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean tableExists(Connection conn, String tableName) {
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, tableName, null);

            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet query(Connection conn, String query) {
        ResultSet rs = null;
        try {
            Statement stat = conn.createStatement();
            rs = stat.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
    
//    public void execute(Connection conn, String query)   {
//        try {
//            Statement stat = conn.createStatement();
//            stat.execute(query);
//        } catch (SQLException ex) {
//            Logger.getLogger(DbHelper2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void execute(Connection conn, String query, Object... values) {
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < values.length; i++)    {
                ps.setObject(i + 1, values[i]);
            }
            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
