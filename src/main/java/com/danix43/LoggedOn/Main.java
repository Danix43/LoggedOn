package com.danix43.LoggedOn;

import static com.danix43.LoggedOn.tools.PlayerToolkit.freezePlayer;
import static com.danix43.LoggedOn.tools.PlayerToolkit.saveInventoryToDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.danix43.LoggedOn.commands.AdminChangePassCommand;
import com.danix43.LoggedOn.commands.ChangePassCommand;
import com.danix43.LoggedOn.commands.LoginCommand;
import com.danix43.LoggedOn.commands.RegisterCommand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * TODO: - Switch some operations to asyncronous threads
 * 		 - Setup log for every file
 */
public class Main extends JavaPlugin implements Listener {
	private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS lo_users("
			+ "id int NOT NULL AUTO_INCREMENT," + "username varchar(255) NOT NULL UNIQUE,"
			+ "password varchar(255) NOT NULL," + "ip varchar(255)," + "lastconnect datetime,"
			+ "armor BLOB(1000), inventory BLOB(1000)," + "PRIMARY KEY(userid)" + ");";

	private final Logger log = getLogger();

	private Connection connection;

	@Override
	public void onEnable() {
		log.info("Plugin starting up");

		createDbConnection();

		getCommand("login").setExecutor(new LoginCommand(connection));
		getCommand("register").setExecutor(new RegisterCommand(connection));
		getCommand("changepass").setExecutor(new ChangePassCommand());
		getCommand("admin.changepass").setExecutor(new AdminChangePassCommand());

		getServer().getPluginManager().registerEvents(this, this);

		log.info("Plugin fully started");
	}

	@Override
	public void onDisable() {
		log.info("Plugin stopping down");
		disconnectFromDb();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		freezePlayer(player);
		player.getInventory().clear();
		player.sendMessage(
				"Use '/register [yourpassword]' to make an account or use '/login [yourpassword]' to log into yours!");
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		saveInventoryToDb(player, connection);
	}

	// read db connection data from config file and create the connection object
	private void createDbConnection() {
		try {
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loggedon?useSSL=false", "root",
					"danix1923");
		} catch (SQLException e) {
			log.severe("Can't connect to the database. Error: " + e.getMessage());
		}
		try (PreparedStatement query = connection.prepareStatement(CREATE_TABLE_QUERY)) {
			query.execute();
		} catch (SQLException e) {
			log.severe("Can't create the table. Error: " + e.getMessage());
		}
	}

	private void disconnectFromDb() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			log.severe("Can't close the connection to the database. Error: " + e.getMessage());
		}
	}

}
