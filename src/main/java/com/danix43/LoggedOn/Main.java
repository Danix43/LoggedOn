package com.danix43.LoggedOn;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.danix43.LoggedOn.commands.AdminChangePassCommand;
import com.danix43.LoggedOn.commands.ChangePassCommand;
import com.danix43.LoggedOn.commands.LoginCommand;
import com.danix43.LoggedOn.commands.RegisterCommand;
import com.danix43.LoggedOn.listeners.PlayerListener;
import com.danix43.LoggedOn.storage.DatabaseAccess;
import com.danix43.LoggedOn.storage.LocalConfigAccess;
import com.danix43.LoggedOn.storage.MySQLAccess;
import com.danix43.LoggedOn.storage.SQLiteAccess;

/**
 * TODO: - Switch some operations to asyncronous threads
 * 	 - Implement rest of commands: /changepass, /adminchangepass, etc
 * 	 - Add embeded SQLite database for testing and on same server storage
 * 	 - Add posibility to change chat output to different languages 
 */
public class Main extends JavaPlugin {
    private final Logger log = getLogger();

    private Connection connetion;
    private DatabaseAccess datasource;

    @Override
    public void onEnable() {
	log.info("Plugin starting up");

	LocalConfigAccess config = new LocalConfigAccess(this);

	if (config.getConfig().getConfigurationSection("database").getBoolean("is-in-memory")) {
	    datasource = new SQLiteAccess(config.getConfig().getConfigurationSection("database"), getDataFolder());
	} else {
	    datasource = new MySQLAccess(config.getConfig().getConfigurationSection("database"));
	}

	datasource.createDbConnection();
	connetion = datasource.getConnection();

	getCommand("login").setExecutor(new LoginCommand(connetion));
	getCommand("register").setExecutor(new RegisterCommand(connetion));
	getCommand("changepass").setExecutor(new ChangePassCommand());
	getCommand("admin.changepass").setExecutor(new AdminChangePassCommand());

	getServer().getPluginManager().registerEvents(new PlayerListener(connetion), this);

	log.info("Plugin fully started");
    }

    @Override
    public void onDisable() {
	log.info("Plugin stopping down");

	datasource.disconnectFromDb();

	log.info("Plugin fully stopped");
    }

}
