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

public final class ProfileStore {
	private final String url, database, username, password;
	private final DateFormat dateFormat;

	ProfileStore(final String url, final String database,
			final String username, final String password) {
		this.url = url;
		this.database = database;
		this.username = username;
		this.password = password;

		dateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
	}

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

	public void requestProfileData(final Profile callback) {
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("SELECT name, about, interests, gender, location FROM ? WHERE uuid = ?");

			ps.setString(1, "profiletable");
			ps.setString(2, callback.getPlayerId().toString());

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

	public void commitSpecificProfileData(final UUID player, final String name,
			final String field, final String value, final Date timeModified) {
		if (!field.toLowerCase().equals(field)) {
			throw new IllegalArgumentException(field);
		}

		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("UPDATE profiletable SET ? = ?, ? = ?, ? = ? WHERE uuid = ?");

			ps.setString(1, field);
			ps.setString(2, value);
			ps.setString(3, field + "modified");
			ps.setString(4, dateFormat.format(timeModified));
			ps.setString(5, "username");
			ps.setString(6, name);
			ps.setString(7, player.toString());

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

	protected void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			ps.close();
			rs.close();
		} catch (Exception e) {
		}
	}
}
