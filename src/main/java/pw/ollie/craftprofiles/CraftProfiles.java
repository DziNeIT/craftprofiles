package pw.ollie.craftprofiles;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pw.ollie.craftprofiles.ProfileManager.CommitTask;

public final class CraftProfiles extends JavaPlugin implements CommandExecutor {
	private ProfileManager profileManager;
	private ProfileStore profileStore;

	@Override
	public void onEnable() {
		final File df = getDataFolder();
		final File conf = new File(df, "config.yml");

		if (!df.exists()) {
			df.mkdirs();
		}
		if (!conf.exists()) {
			try {
				conf.createNewFile();
			} catch (IOException e) {
			}
		}

		final YamlConfiguration config = YamlConfiguration
				.loadConfiguration(conf);
		final String url = config.getString("url",
				"jdbc:mysql://localhost:3306"), db = config.getString(
				"database", "cprofiles"), user = config.getString("db-user",
				"admin"), pass = config.getString("db-pass", "password");

		try {
			config.save(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		profileManager = new ProfileManager(this);
		profileStore = new ProfileStore(url, db, user, pass);

		getCommand("profile").setExecutor(this);
		getServer().getPluginManager().registerEvents(new CPListener(this),
				this);
	}

	@Override
	public void onDisable() {
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public ProfileStore getProfileStore() {
		return profileStore;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String lbl, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("profile")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Only players can have profiles!");
				return true;
			}
			if (!(sender.hasPermission("craftprofiles.use"))) {
				sender.sendMessage(ChatColor.DARK_RED
						+ "You don't have permission to do that!");
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GOLD + "--- Profile Commands ---");
				sender.sendMessage(ChatColor.GRAY
						+ "/profile name <name> - Sets the name in your profile");
				sender.sendMessage(ChatColor.GRAY
						+ "/profile about <about> - Sets the description of you in your profile");
				sender.sendMessage(ChatColor.GRAY
						+ "/profile interests <interests> - Sets your interests in your profile");
				sender.sendMessage(ChatColor.GRAY
						+ "/profile gender <m/f> - Sets your gender in your profile");
				sender.sendMessage(ChatColor.GRAY
						+ "/profile location <location> - Sets the location in your profile");
				sender.sendMessage(ChatColor.GRAY
						+ "/profile view <playername> - Views the profile of the given player");
				return true;
			}

			final String subcommand = args[0].toLowerCase();
			if (args.length < 2) {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Invalid syntax, /profile " + subcommand + " <"
						+ subcommand + " / playername>");
			}

			if (subcommand.equals("view")) {
				if (sender.hasPermission("craftprofiles.view")) {
					final String other = args[1];
					final Player pl = Bukkit.getPlayer(other);
					final Profile profile;
					if (pl != null) {
						profile = profileManager.getPlayerProfile(pl
								.getUniqueId());
					} else {
						final OfflinePlayer p = Bukkit.getOfflinePlayer(other);
						profile = profileManager.getPlayerProfile(p
								.getUniqueId());
					}

					sender.sendMessage(ChatColor.GOLD + other + "'s Profile");
					sender.sendMessage(ChatColor.GRAY + "About - "
							+ profile.getAbout());
					sender.sendMessage(ChatColor.GRAY + "Interests - "
							+ profile.getInterests());
					sender.sendMessage(ChatColor.GRAY + "Gender - "
							+ profile.getGender());
					sender.sendMessage(ChatColor.GRAY + "Location - "
							+ profile.getLocation());
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}

				return true;
			}

			final Player pl = (Player) sender;
			final String remaining = toString(Arrays.copyOfRange(args, 1,
					args.length - 1));
			final Profile profile = profileManager.getPlayerProfile(pl
					.getUniqueId());
			final UUID player = pl.getUniqueId();
			final String name = pl.getName(), field, value = remaining;

			if (subcommand.equals("name")) {
				profile.setName(remaining);
				field = "name";
			} else if (subcommand.equals("about")) {
				profile.setAbout(remaining);
				field = "about";
			} else if (subcommand.equals("interests")) {
				profile.setInterests(remaining);
				field = "interests";
			} else if (subcommand.equals("gender")) {
				profile.setGender(remaining);
				field = "gender";
			} else if (subcommand.equals("location")) {
				profile.setLocation(remaining);
				field = "location";
			} else {
				sender.sendMessage(ChatColor.DARK_RED
						+ "That subcommand doesn't exist!");
				field = null;
			}

			if (name != null && field != null && value != null) {
				sender.sendMessage(ChatColor.GRAY + "You updated your " + field
						+ "!");

				getServer().getScheduler().runTaskAsynchronously(
						this,
						new CommitTask(profileStore, player, name, field,
								value, new Date()));
			}
		}

		return true;
	}

	private String toString(final String[] array) {
		final StringBuilder builder = new StringBuilder();
		for (final String string : array) {
			builder.append(string).append(" ");
		}
		builder.setLength(builder.length() - 1);
		return builder.toString();
	}
}
