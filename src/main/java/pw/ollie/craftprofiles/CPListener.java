package pw.ollie.craftprofiles;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class CPListener implements Listener {
	private final CraftProfiles plugin;

	CPListener(final CraftProfiles plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		final UUID id = event.getPlayer().getUniqueId();
		plugin.getProfileManager().getPlayerProfile(id);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final UUID id = event.getPlayer().getUniqueId();
		plugin.getProfileManager().savePlayerProfile(id);
	}
}
