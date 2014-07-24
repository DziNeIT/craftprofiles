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
		final Profile profile;
		if (p != null) {
			profile = createProfile(player, p.getName());
		} else {
			profile = createProfile(player, "");
		}

		plugin.getServer()
				.getScheduler()
				.runTaskAsynchronously(plugin,
						new FetchTask(plugin.getProfileStore(), profile));

		return profile;
	}

	void savePlayerProfile(final UUID player) {
		for (final Profile profile : profiles) {
			if (profile.getPlayerId().equals(player)) {
				plugin.getProfileStore().commitProfileData(profile);
				profiles.remove(profile);
				return;
			}
		}
	}

	private Profile createProfile(final UUID player, final String name) {
		final Profile profile = new Profile(player, name);
		if (profiles.add(profile)) {
			return profile;
		} else {
			return null;
		}
	}

	private Profile createProfile(final UUID player, final String name,
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

	void storeProfiles() {
		for (final Profile profile : profiles) {
			plugin.getProfileStore().commitProfileData(profile);
		}
	}

	public static final class FetchTask implements Runnable {
		private final ProfileStore profileStore;
		private final Profile profile;

		public FetchTask(final ProfileStore ps, final Profile p) {
			profileStore = ps;
			profile = p;
		}

		@Override
		public void run() {
			profileStore.requestProfileData(profile);
			profile.loaded = true;
		}
	}
}
