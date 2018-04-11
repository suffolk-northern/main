/*
 * Roger Xue
 *
 * Database access helper.
 */
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

	// Manages connection pool
	private SQLiteDataSource ds = new SQLiteDataSource();
	// Permanent address of track model database
	private String url = "jdbc:sqlite:TrackModel.db";

	/**
	 * Initializes DbHelper.
	 */
	public DbHelper() {
		ds.setUrl(url);
	}

	/**
	 * Gets connection to database.
	 *
	 * @return Connection
	 */
	public Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException ex) {
			Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Checks if a table exists in the database.
	 *
	 * @param conn
	 * @param tableName
	 * @return tableExists
	 */
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

	/**
	 * Executes a query.
	 *
	 * @param conn
	 * @param query
	 * @return ResultSet
	 */
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

	/**
	 * Execute a query with parameters.
	 *
	 * @param conn
	 * @param query
	 * @param values
	 */
	public void execute(Connection conn, String query, Object... values) {
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			for (int i = 0; i < values.length; i++) {
				ps.setObject(i + 1, values[i]);
			}
			ps.executeUpdate();

		} catch (SQLException ex) {
			Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
