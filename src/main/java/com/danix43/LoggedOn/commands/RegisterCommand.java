package com.danix43.LoggedOn.commands;

import static com.danix43.LoggedOn.tools.PlayerToolkit.unfreezePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.google.gson.Gson;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterCommand implements CommandExecutor {

	private static final Gson gson = new Gson();

	private static final String ALREADY_EXISTS_QUERY = "SELECT * FROM lo_users WHERE username = ?;";
	private static final String REGISTER_USER_QUERY = "INSERT INTO lo_users (username, password, ip, lastconnect) VALUES(?, ?, ?, ?);";
	private final Connection connection;

	public RegisterCommand(Connection connection) {
		this.connection = connection;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!checkIfPlayerAlreadyRegistered(player)) {
				registerNewPlayer(player, BCrypt.hashpw(args[0], BCrypt.gensalt()));
				return true;
			} else {
				player.sendMessage("There is a another account registered with your name!");
				return true;
			}
		}
		sender.sendMessage("You can't register as a console or a command block!");
		return true;
	}

	/**
	 * TODO: - Add hashing method to password
	 */
	private void registerNewPlayer(Player player, String password) {
		try (PreparedStatement query = connection.prepareStatement(REGISTER_USER_QUERY)) {
			query.setString(1, player.getName());
			query.setString(2, password);
			query.setString(3, player.getAddress().toString());
			query.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

			query.execute();

			player.sendMessage("You have been registered!");
			unfreezePlayer(player);
		} catch (SQLException e) {
			System.err.println("Error registering the new player. Error: " + e.getMessage());
		}

	}

	/**
	 * This return true if a record with this name already exists in the datebase or
	 * else false
	 * 
	 * @param checkedPlayer
	 */
	private boolean checkIfPlayerAlreadyRegistered(Player checkedPlayer) {
		try (PreparedStatement query = connection.prepareStatement(ALREADY_EXISTS_QUERY)) {
			query.setString(1, checkedPlayer.getName());
			try (ResultSet results = query.executeQuery()) {
				return results.next();
			} catch (SQLException e) {
				System.err.println("Error when checking if the player already exists. Error: " + e.getMessage());
				return true;
			}
		} catch (SQLException e) {
			System.err.println("Error when checking if the player already exists. Error: " + e.getMessage());
			return true;
		}
	}
}
