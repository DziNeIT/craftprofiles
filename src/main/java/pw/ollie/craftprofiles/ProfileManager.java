package pw.ollie.craftprofiles;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ProfileManager {
	private final CraftProfiles plugin;
	private final Set<Profile> profiles = new HashSet<Profile>();

	public ProfileManager(final CraftProfiles plugin) {
		this.plugin = plugin;
	}

	public Profile getPlayerProfile(final UUID player) {
		for (final Profile profile : profiles) {
			if (profile.getPlayerId().equals(player)) {
				return profile;
			}
		}

		final Player p = Bukkit.getPlayer(player);
		if (p != null) {
			return createProfile(player, p.getName());
		}
		return createProfile(player, "");
	}

	public Profile createProfile(final UUID player, final String name) {
		final Profile profile = new Profile(player, name);
		if (profiles.add(profile)) {
			return profile;
		} else {
			return null;
		}
	}

	public Profile createProfile(final UUID player, final String name,
			final String about, final String interests, final String gender,
			final String location) {
		final Profile profile = createProfile(player, name);
		if (profile != null) {
			profile.setAbout(about);
			profile.setInterests(interests);
			profile.setGender(gender);
			profile.setLocation(location);
		}
		return profile;
	}
}
