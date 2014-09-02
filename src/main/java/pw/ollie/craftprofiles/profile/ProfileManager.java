package pw.ollie.craftprofiles.profile;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import pw.ollie.craftprofiles.CraftProfiles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ProfileManager {
    private final CraftProfiles plugin;
    private final Set<Profile> profiles = new HashSet<Profile>();

    public ProfileManager(CraftProfiles plugin) {
        this.plugin = plugin;
    }

    public Profile getPlayerProfile(UUID player) {
        for (Profile profile : profiles) {
            if (profile.getPlayerId().equals(player)) {
                return profile;
            }
        }

        Player p = Bukkit.getPlayer(player);
        Profile profile;
        if (p != null) {
            profile = createProfile(player, p.getName());
        } else {
            profile = createProfile(player, "");
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new FetchTask(plugin.getProfileStore(), profile));
        return profile;
    }

    public void unloadProfileData(UUID player) {
        for (Profile profile : profiles) {
            if (profile.getPlayerId().equals(player)) {
                profiles.remove(profile);
                return;
            }
        }
    }

    private Profile createProfile(UUID player, String name) {
        Profile profile = new Profile(player, name);
        if (profiles.add(profile)) {
            return profile;
        } else {
            return null;
        }
    }

    public static final class FetchTask implements Runnable {
        private final ProfileStore profileStore;
        private final Profile profile;

        public FetchTask(ProfileStore ps, Profile p) {
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
        private final Profile profile;
        private final String playername;

        public CommitTask(ProfileStore store, Profile profile, String playername) {
            this.store = store;
            this.profile = profile;
            this.playername = playername;
        }

        @Override
        public void run() {
            store.commitProfileData(profile, playername, new Date());
        }
    }
}
