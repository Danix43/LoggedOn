package com.danix43.LoggedOn.commands;

import static com.danix43.LoggedOn.tools.PlayerToolkit.loadInventory;
import static com.danix43.LoggedOn.tools.PlayerToolkit.unfreezePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import com.danix43.LoggedOn.tools.TextServer;

public class LoginCommand implements CommandExecutor {
    private static final String ALREADY_EXISTS_QUERY = "SELECT * FROM lo_users WHERE username = ?;";
    private static final String LOGIN_PLAYER_QUERY = "SELECT password FROM lo_users WHERE username = ?;";

    private static final Logger log = Logger.getGlobal();
    private final Connection connection;

    private TextServer text;

    public LoginCommand(Connection dbConnection, TextServer ts) {
	this.connection = dbConnection;
	this.text = ts;
    }

    /*
     * Quick wiki: The label represents the actual command The args represents the
     * inputs after the command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;

	    if (args[0] == null) {
		player.sendMessage(ChatColor.RED + text.getChangePassErrorNotEnoughArgsText());
		return false;
	    }

	    if (playerHasAccount(player)) {
		logInPlayer(player, args[0]);
		return true;
	    } else {
		player.sendMessage(ChatColor.RED + text.getLoginNotRegisteredText());
		return true;
	    }
	}

	sender.sendMessage(text.getLoginNotPlayerText());
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
		    player.sendMessage(ChatColor.GREEN + text.getLoginLoggedOnText());
		    loadInventory(player, connection);
		    unfreezePlayer(player);
		} else {
		    player.sendMessage(ChatColor.RED + text.getLoginWrongPasswordText());
		    loggingAttempts++;
		    if (loggingAttempts == 3) {
			player.kickPlayer(ChatColor.RED + text.getLoginFailedLoginText());
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
