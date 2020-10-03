package com.danix43.LoggedOn.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocalConfigAccess {

    private FileConfiguration config;

    public LocalConfigAccess(JavaPlugin plugin) {
	this.config = plugin.getConfig();

	addDefaultDbValues(config);

	config.options().copyDefaults(true);
	plugin.saveConfig();
    }

    /*
     * needed for config file - db: 
     * - host
     * - port
     * - schema name
     * - username
     * - password
     */
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

    public FileConfiguration getConfig() {
	return config;
    }

}
