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

public class DbHelper {

    private String driver = "org.sqlite.JDBC";
    private String url = "jdbc:sqlite:TrackModel.db";
    private Connection connection = null;

    public DbHelper() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean tableExists(String tableName) {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet rs = dbm.getTables(null, null, tableName, null);

            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ResultSet query(String query) {
        ResultSet rs = null;
        try {
            Statement stat = connection.createStatement();
            rs = stat.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
    
    public void execute(String query)   {
        try {
            Statement stat = connection.createStatement();
            stat.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void execute(String query, Object... values) {
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++)    {
                ps.setObject(i + 1, values[i]);
            }
            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setAutoCommit(boolean autoCommit)   {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
