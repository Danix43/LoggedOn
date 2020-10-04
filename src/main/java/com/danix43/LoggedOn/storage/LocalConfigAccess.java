package com.danix43.LoggedOn.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocalConfigAccess {

    private FileConfiguration config;

    public LocalConfigAccess(JavaPlugin plugin) {
	this.config = plugin.getConfig();

	addDefaultDbValues(config);
	addDefaultLanguageValues(config);

	config.options().copyDefaults(true);
	plugin.saveConfig();
    }

    private static void addDefaultDbValues(FileConfiguration config) {
	if (config.getConfigurationSection("database") == null) {
	    ConfigurationSection section = config.createSection("database");
	    section.addDefault("is-in-memory", true);
	    section.addDefault("host-name", "replace me");
	    section.addDefault("port", 3306);
	    section.addDefault("database-name", "replace me");
	    section.addDefault("database-username", "replace me");
	    section.addDefault("database-password", "replace me");
	}
    }

    private static void addDefaultLanguageValues(FileConfiguration config) {
	if (config.getConfigurationSection("language") == null) {
	    ConfigurationSection section = config.createSection("language");
	    section.addDefault("player-enter",
	            "Use '/register [yourpassword]' to make an account or use '/login [yourpassword]' to log into yours!");

	    section.addDefault("register-already-registered", "There is a another account registered with your name!");
	    section.addDefault("register-registered", "You have been registered!");
	    section.addDefault("register-not-player", "You can't register as a console or a command block!");

	    section.addDefault("login-not-registered", "You don't have an account registered.\n"
	            + "Use the command '/register [yourpassword]' to register");
	    section.addDefault("login-not-player", "You can't log in from console or a command block!");
	    section.addDefault("login-loggedon", "You have been logged on! Enjoy!");
	    section.addDefault("login-wrong-password", "Wrong password. Try again");
	    section.addDefault("login-failed-login", "Failed to login in time!");

	    section.addDefault("changepass-success", "Password changed successfully");
	    section.addDefault("changepass-fail", "The old password isn't the same with the one that you logged in!");
	    section.addDefault("changepass-error-update", "Can't update the password in the db. Error ");
	    section.addDefault("changepass-error-retrieve", "Can't retrieve the password from the db. Error ");
	    section.addDefault("changepass-error-notenoughargs", "Not enough arguments provided!");
	}
    }

    public FileConfiguration getConfig() {
	return config;
    }

}
