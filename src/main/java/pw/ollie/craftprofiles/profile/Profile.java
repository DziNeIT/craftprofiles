package pw.ollie.craftprofiles.profile;

import java.util.UUID;

/**
 * A player's profile, containing some information about the player, which they
 * can choose specify if they wish
 */
public final class Profile {
	/**
	 * The player whose profile this is' unique identifier
	 */
	private final UUID playerId;

	/**
	 * The name of the player who owns this profile
	 */
	private String name = "";
	/**
	 * A description of the player who owns this profile
	 */
	private String about = "";
	/**
	 * The interests of the player who owns this profile
	 */
	private String interests = "";
	/**
	 * The gender of the player who owns this profile
	 */
	private String gender = "";
	/**
	 * The location of the player who owns this profile
	 */
	private String location = "";

	/**
	 * Used internally - do not touch
	 */
	boolean loaded;

	Profile(UUID playerId, String name) {
		this.playerId = playerId;
		this.name = name;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public String getName() {
		return name;
	}

	public String getAbout() {
		return about;
	}

	public String getInterests() {
		return interests;
	}

	public String getGender() {
		return gender;
	}

	public String getLocation() {
		return location;
	}

	/**
	 * IF THIS IS FALSE DO NOT USE THE OBJECT
	 * 
	 * @return Whether the profile data has been loaded from MySQL yet
	 */
	public boolean isLoaded() {
		return loaded;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
