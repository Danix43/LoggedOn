package com.danix43.LoggedOn;

import static com.danix43.LoggedOn.PlayerToolkit.freezePlayer;
import static com.danix43.LoggedOn.PlayerToolkit.itemStackArrayToBase64;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.danix43.LoggedOn.commands.AdminChangePassCommand;
import com.danix43.LoggedOn.commands.ChangePassCommand;
import com.danix43.LoggedOn.commands.LoginCommand;
import com.danix43.LoggedOn.commands.RegisterCommand;
import com.google.gson.Gson;

/**
 * TODO: - Add connection to the database - Write all the commands - plm
 */
public class Main extends JavaPlugin implements Listener {

	private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS lo_users("
			+ "id int NOT NULL AUTO_INCREMENT," + "username varchar(255) NOT NULL UNIQUE,"
			+ "password varchar(255) NOT NULL," + "ip varchar(255)," + "lastconnect datetime,"
			+ "inventory LONGTEXT," + "PRIMARY KEY(userid)" + ");";

	private static final Gson gson = new Gson();

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
		player.sendMessage(
				"Use '/register [yourpassword]' to make an account or use '/login [yourpassword]' to log into yours!");
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		saveInventory(player);
	}

	private void saveInventory(Player player) {
		ItemStack[] armor = player.getInventory().getArmorContents();
		ItemStack[] items = player.getInventory().getStorageContents();

		String armorJson = itemStackArrayToBase64(armor);
		String itemsJson = itemStackArrayToBase64(items);

		String inventoryJson = armorJson + "-" + itemsJson;

		String sql = "UPDATE lo_users SET inventory = ? WHERE username = ?;";
		try (PreparedStatement query = connection.prepareStatement(sql)) {
			query.setString(1, inventoryJson);
			query.setString(2, player.getName());

			query.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error saving the inventory to database. Error: " + e.getMessage());
		}
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
