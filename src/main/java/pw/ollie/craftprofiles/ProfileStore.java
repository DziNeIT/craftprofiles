package pw.ollie.craftprofiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Deals with all of the MySQL code for the plugin, i.e storing profiles in a
 * database and retrieving data for players
 */
public final class ProfileStore {
	/**
	 * Database connection information
	 */
	private final String url, username, password;
	/**
	 * The date format used for storing times / dates in the database
	 */
	private final DateFormat dateFormat;

	ProfileStore(final String url, final String username, final String password) {
		this.url = url;
		this.username = username;
		this.password = password;

		dateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
	}

	/**
	 * Initialises the database by creating the profile table if it doesn't
	 * exist
	 */
	public void initialise() {
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("CREATE TABLE IF NOT EXISTS profiletable ("
							+ "uuid VARCHAR(40) PRIMARY KEY,"
							+ "username VARCHAR(16) NOT NULL,"
							+ "name TEXT,"
							+ "about TEXT,"
							+ "interests TEXT,"
							+ "gender TEXT,"
							+ "location TEXT, lastupdated VARCHAR(19))");

			ps.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			close(ps, rs);
		}
	}

	/**
	 * Requests data for the given profile by retrieving the profile of the
	 * player the given profile belongs to
	 * 
	 * @param callback
	 *            The profile object to store the retrieving profile data in
	 */
	public void requestProfileData(final Profile callback) {
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("SELECT name, about, interests, gender, location FROM profiletable WHERE uuid = ?");

			ps.setString(1, callback.getPlayerId().toString());

			rs = ps.executeQuery();

			if (rs.next()) {
				callback.setName(rs.getString(1));
				callback.setAbout(rs.getString(2));
				callback.setInterests(rs.getString(3));
				callback.setGender(rs.getString(4));
				callback.setLocation(rs.getString(5));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			close(ps, rs);
		}
	}

	// TODO: CHANGE
	public void commitProfileData(final Profile data, final String playername,
			final Date timeModified) {
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("UPDATE profiletable SET "
							+ "username = ?, name = ?, about = ?, interests = ?, gender = ?, location = ?, lastupdated = ?"
							+ " WHERE uuid = ?");

			ps.setString(1, playername);
			ps.setString(2, data.getName());
			ps.setString(3, data.getAbout());
			ps.setString(4, data.getInterests());
			ps.setString(5, data.getGender());
			ps.setString(6, data.getLocation());
			ps.setString(7, dateFormat.format(timeModified));
			ps.setString(8, data.getPlayerId().toString());

			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			close(ps, rs);
		}
	}

	private Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Closes the given statement and the given results without throwing any
	 * exceptions
	 * 
	 * @param ps
	 *            The PreparedStatement to close
	 * @param rs
	 *            The ResultSet to close
	 */
	protected void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			ps.close();
			rs.close();
		} catch (Exception e) {
		}
	}
}
