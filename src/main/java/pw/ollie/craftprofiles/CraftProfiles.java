package pw.ollie.craftprofiles;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import pw.ollie.craftprofiles.profile.Profile;
import pw.ollie.craftprofiles.profile.ProfileManager;
import pw.ollie.craftprofiles.profile.ProfileManager.CommitTask;
import pw.ollie.craftprofiles.profile.ProfileStore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.ChatColor.*;

public final class CraftProfiles extends JavaPlugin implements CommandExecutor {
    private ProfileManager profileManager;
    private ProfileStore profileStore;

    @Override
    public void onEnable() {
        File df = getDataFolder();
        File conf = new File(df, "config.yml");

        if (!df.exists()) {
            df.mkdirs();
        }
        if (!conf.exists()) {
            try {
                conf.createNewFile();
            } catch (IOException e) {
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(conf);
        String url = config.getString("db-url", "jdbc:mysql://localhost:3306/cprofiles");
        String user = config.getString("db-user", "admin");
        String pass = config.getString("db-pass", "");

        try {
            config.save(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        profileManager = new ProfileManager(this);
        profileStore = new ProfileStore(url, user, pass);

        getCommand("profile").setExecutor(this);
        getServer().getPluginManager().registerEvents(new CPListener(this), this);

        profileStore.initialise();
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
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("profile")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(DARK_RED + "Only players can have profiles!");
            return true;
        }

        if (!(sender.hasPermission("craftprofiles.use"))) {
            sender.sendMessage(DARK_RED + "You don't have permission to do that!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(GOLD + "--- Profile Commands ---");
            sender.sendMessage(GRAY + "/profile name <name> - Sets the name in your profile");
            sender.sendMessage(GRAY + "/profile about <about> - Sets the description of you in your profile");
            sender.sendMessage(GRAY + "/profile interests <interests> - Sets your interests in your profile");
            sender.sendMessage(GRAY + "/profile gender <m/f> - Sets your gender in your profile");
            sender.sendMessage(GRAY + "/profile location <location> - Sets the location in your profile");
            sender.sendMessage(GRAY + "/profile view <playername> - Views the profile of the given player");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        if (args.length < 2) {
            sender.sendMessage(DARK_RED + "Invalid syntax, /profile " + subcommand + " <" + subcommand + ">");
            return true;
        }

        if (subcommand.equals("view")) {
            if (sender.hasPermission("craftprofiles.view")) {
                String other = args[1];
                Player pl = Bukkit.getPlayer(other);
                Profile profile;
                if (pl != null) {
                    profile = profileManager.getPlayerProfile(pl.getUniqueId());
                } else {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(other);
                    profile = profileManager.getPlayerProfile(p.getUniqueId());
                }

                sender.sendMessage(GOLD + other + "'s Profile");
                sender.sendMessage(GRAY + "About - " + profile.getAbout());
                sender.sendMessage(GRAY + "Interests - " + profile.getInterests());
                sender.sendMessage(GRAY + "Gender - " + profile.getGender());
                sender.sendMessage(GRAY + "Location - " + profile.getLocation());
            } else {
                sender.sendMessage(DARK_RED + "You don't have permission to do that!");
            }

            return true;
        }

        Player pl = (Player) sender;
        String remaining;

        if (args.length == 2) {
            remaining = args[1];
        } else {
            remaining = toString(Arrays.copyOfRange(args, 1, args.length));
        }

        Profile profile = profileManager.getPlayerProfile(pl.getUniqueId());
        UUID player = pl.getUniqueId();
        String name = pl.getName(), field, value = remaining;

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
            sender.sendMessage(DARK_RED + "That subcommand doesn't exist!");
            field = null;
        }

        if (name != null && field != null && value != null) {
            sender.sendMessage(GRAY + "You updated your " + field + "!");
            getServer().getScheduler().runTaskAsynchronously(this, new CommitTask(profileStore, profileManager.getPlayerProfile(player), name));
        }

        return true;
    }

    private String toString(String[] array) {
        if (array.length == 1) {
            return array[0];
        }
        StringBuilder builder = new StringBuilder();
        for (String string : array) {
            builder.append(string).append(" ");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
