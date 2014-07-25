package pw.ollie.craftprofiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public final class ProfileStore {
	public static final String pt = "profiletable";

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
			ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `?` ("
					+ "uuid VARCHAR(40) PRIMARY KEY,"
					+ "username VARCHAR(16) NOT NULL,"
					+ "content TEXT, VARCHAR(19) timeofcommand)");

			ps.setString(1, "name");
			ps.addBatch();
			ps.setString(1, "about");
			ps.addBatch();
			ps.setString(1, "interests");
			ps.addBatch();
			ps.setString(1, "gender");
			ps.addBatch();
			ps.setString(1, "location");
			ps.addBatch();

			ps.executeBatch();
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
			// TODO: STATEMENT
			ps = connection.prepareStatement("");
			rs = ps.executeQuery();

			final String name = rs.getString(1);
			final String about = rs.getString(2);
			final String interests = rs.getString(3);
			final String gender = rs.getString(4);
			final String location = rs.getString(5);

			if (name == null || about == null || interests == null
					|| gender == null || location == null) {
				throw new SQLException();
			}

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
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("INSERT INTO `?` VALUES (`?`, `?`, `?`, `?`)");

			ps.setString(1, player.toString());
			ps.setString(2, field);
			ps.setString(3, name);
			ps.setString(4, value);
			ps.setString(5, dateFormat.format(timeModified));

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
