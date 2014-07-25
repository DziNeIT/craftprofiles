package pw.ollie.craftprofiles;

import java.util.Date;
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

	private Profile createProfile(final UUID player, final String name) {
		final Profile profile = new Profile(player, name);
		if (profiles.add(profile)) {
			return profile;
		} else {
			return null;
		}
	}

	void unloadProfileData(final UUID player) {
		for (final Profile profile : profiles) {
			if (profile.getPlayerId().equals(player)) {
				profiles.remove(profile);
				return;
			}
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

	public static final class CommitTask implements Runnable {
		private final ProfileStore store;
		private final UUID id;
		private final String name;
		private final String field;
		private final String value;
		private final Date timeModified;

		public CommitTask(final ProfileStore store, final UUID id,
				final String name, final String field, final String value,
				final Date timeModified) {
			this.store = store;
			this.id = id;
			this.name = name;
			this.field = field;
			this.value = value;
			this.timeModified = timeModified;
		}

		@Override
		public void run() {
			store.commitSpecificProfileData(id, name, field, value,
					timeModified);
		}
	}
}
