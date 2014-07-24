package pw.ollie.craftprofiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ProfileStore {
	public static final String pt = "profiletable";

	private final String url, database, username, password;

	ProfileStore(final String url, final String database,
			final String username, final String password) {
		this.url = url;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public void initialise() {
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// TODO: STATEMENT - create if not exists player table and all that jazz

			throw new SQLException();
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

	public void commitProfileData(final Profile data) {
		final Connection connection = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("UPDATE " + pt + " SET "
					+ "name = ?, " + "about = ?," + "interests = ?,"
					+ "gender = ?," + "location = ?," + "WHERE uuid = ?");

			ps.setString(1, data.getName());
			ps.setString(2, data.getAbout());
			ps.setString(3, data.getInterests());
			ps.setString(4, data.getGender());
			ps.setString(5, data.getLocation());
			ps.setString(6, data.getPlayerId().toString());

			ps.execute();
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
