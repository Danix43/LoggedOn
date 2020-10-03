package com.danix43.LoggedOn.commands;

import static com.danix43.LoggedOn.tools.PlayerToolkit.loadInventory;
import static com.danix43.LoggedOn.tools.PlayerToolkit.unfreezePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

public class LoginCommand implements CommandExecutor {
    private static final String ALREADY_EXISTS_QUERY = "SELECT * FROM lo_users WHERE username = ?;";
    private static final String LOGIN_PLAYER_QUERY = "SELECT password, inventory FROM lo_users WHERE username = ?;";

    private static final Logger log = Logger.getGlobal();
    private final Connection connection;

    public LoginCommand(Connection dbConnection) {
	this.connection = dbConnection;
    }

    /*
     * Quick wiki: The label represents the actual command The args represents the
     * inputs after the command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    if (playerHasAccount(player)) {
		logInPlayer(player, args[0]);
		return true;
	    } else {
		player.sendRawMessage(
		        "You don't have an account registered.\nUse the command '/register [yourpassword]' to register");
		return true;
	    }
	}

	sender.sendMessage("You can't log in from console or a command block!");
	return true;
    }

    private void logInPlayer(Player player, String password) {
	int loggingAttempts = 0;
	try (PreparedStatement query = connection.prepareStatement(LOGIN_PLAYER_QUERY)) {
	    query.setString(1, player.getName());
	    try (ResultSet result = query.executeQuery()) {
		result.next();
		String dbPassword = result.getString("password");
		if (BCrypt.checkpw(password, dbPassword)) {
		    player.sendMessage("You have been logged on! Enjoy!");
		    loadInventory(player, connection);
		    unfreezePlayer(player);
		} else {
		    player.sendMessage("Wrong password. Try again");
		    loggingAttempts++;
		    if (loggingAttempts == 3) {
			player.kickPlayer("Failed to login in time!");
		    }
		}
	    }
	} catch (SQLException e) {
	    log.warning("Error when logging on. Error: " + e.getMessage());
	}
    }

    private boolean playerHasAccount(Player checkedPlayer) {
	try (PreparedStatement query = connection.prepareStatement(ALREADY_EXISTS_QUERY)) {
	    query.setString(1, checkedPlayer.getName());
	    try (ResultSet results = query.executeQuery()) {
		return results.next();
	    } catch (SQLException e) {
		log.warning("Error when checking if the player already exists. Error: " + e.getMessage());
		return true;
	    }
	} catch (SQLException e) {
	    log.warning("Error when checking if the player already exists. Error: " + e.getMessage());
	    return true;
	}
    }

}
