package com.danix43.LoggedOn;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.danix43.LoggedOn.commands.AdminChangePassCommand;
import com.danix43.LoggedOn.commands.ChangePassCommand;
import com.danix43.LoggedOn.commands.LoginCommand;
import com.danix43.LoggedOn.commands.RegisterCommand;
import com.danix43.LoggedOn.db.Datasource;
import com.danix43.LoggedOn.listeners.PlayerListener;

/**
 * TODO: - Switch some operations to asyncronous threads
 */
public class Main extends JavaPlugin {
    private final Logger log = getLogger();

    private Connection connetion;
    private Datasource datasource = new Datasource();

    @Override
    public void onEnable() {
	log.info("Plugin starting up");

	datasource.createDbConnection();
	connetion = datasource.getConnection();

	System.out.println(connetion);

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
