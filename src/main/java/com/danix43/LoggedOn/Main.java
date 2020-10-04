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
import com.danix43.LoggedOn.tools.TextServer;

/**
 * TODO: - Switch some operations to asyncronous threads
 * 	 - Implement rest of commands: /changepass, /adminchangepass, etc
 */
public class Main extends JavaPlugin {
    private final Logger log = getLogger();

    private Connection connection;
    private DatabaseAccess datasource;

    @Override
    public void onEnable() {
	log.info("Plugin starting up");

	LocalConfigAccess config = new LocalConfigAccess(this);

	TextServer text = TextServer.getInstance(config.getConfig().getConfigurationSection("language"));

	if (config.getConfig().getConfigurationSection("database").getBoolean("is-in-memory")) {
	    datasource = new SQLiteAccess(config.getConfig().getConfigurationSection("database"), getDataFolder());
	} else {
	    datasource = new MySQLAccess(config.getConfig().getConfigurationSection("database"));
	}

	datasource.createDbConnection();
	connection = datasource.getConnection();

	getCommand("login").setExecutor(new LoginCommand(connection, text));
	getCommand("register").setExecutor(new RegisterCommand(connection, text));
	getCommand("changepass").setExecutor(new ChangePassCommand(connection, text));
	getCommand("admin.changepass").setExecutor(new AdminChangePassCommand(connection, text));

	getServer().getPluginManager().registerEvents(new PlayerListener(connection, text), this);

	log.info("Plugin fully started");
    }

    @Override
    public void onDisable() {
	log.info("Plugin stopping down");

	datasource.disconnectFromDb();

	log.info("Plugin fully stopped");
    }

}
