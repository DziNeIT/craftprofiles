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
	private final String url, database, username, password;
	/**
	 * The date format used for storing times / dates in the database
	 */
	private final DateFormat dateFormat;

	ProfileStore(final String url, final String database,
			final String username, final String password) {
		this.url = url;
		this.database = database;
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
							+ "name TEXT, nameupdated VARCHAR(19),"
							+ "about TEXT aboutupdated VARCHAR(19),"
							+ "interests TEXT interestsupdated VARCHAR(19),"
							+ "gender TEXT genderupdated VARCHAR(19),"
							+ "location TEXT locationupdated VARCHAR(10))");

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

			callback.setName(rs.getString(1));
			callback.setAbout(rs.getString(2));
			callback.setInterests(rs.getString(3));
			callback.setGender(rs.getString(4));
			callback.setLocation(rs.getString(5));
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			close(ps, rs);
		}
	}

	/**
	 * Commits the given profile data to the profile table in the database
	 * 
	 * @param player
	 *            The unique identifier of the player we are storing data for
	 * @param name
	 *            The current name of the player we are storing data for
	 * @param field
	 *            The field in the database which is being updated
	 * @param value
	 *            What to set the given field's value to
	 * @param timeModified
	 *            The time of modification to set for the given field
	 */
	public void commitSpecificProfileData(final UUID player, final String name,
			final String field, final String value, final Date timeModified) {
		if (!field.toLowerCase().equals(field)) {
			throw new IllegalArgumentException(field);
		}

		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("UPDATE profiletable SET " + field
					+ " = ?, " + field + "modified = "
					+ dateFormat.format(timeModified) + ", username = " + name
					+ " WHERE uuid = " + player.toString());

			ps.setString(1, value);

			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			close(ps, rs);
		}
	}

	private Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(url
					+ (url.endsWith("/") ? "" : "/") + database, username,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
