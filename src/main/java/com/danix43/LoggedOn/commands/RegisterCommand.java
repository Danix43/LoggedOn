package com.danix43.LoggedOn.commands;

import static com.danix43.LoggedOn.tools.PlayerToolkit.unfreezePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import com.danix43.LoggedOn.tools.TextServer;

public class RegisterCommand implements CommandExecutor {
    private static final String ALREADY_EXISTS_QUERY = "SELECT * FROM lo_users WHERE username = ?;";
    private static final String REGISTER_USER_QUERY = "INSERT INTO lo_users (username, password, ip, registerdate) VALUES(?, ?, ?, ?);";

    private static final Logger log = Logger.getGlobal();

    private final Connection connection;

    private TextServer text;

    public RegisterCommand(Connection dbConnection, TextServer text) {
	this.connection = dbConnection;
	this.text = text;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    if (!checkIfPlayerAlreadyRegistered(player)) {
		registerNewPlayer(player, BCrypt.hashpw(args[0], BCrypt.gensalt()));
		return true;
	    } else {
		player.sendMessage(text.getRegisterAlreadyRegisterd());
		return true;
	    }
	}
	sender.sendMessage(text.getRegisterNotPlayerText());
	return true;
    }

    private void registerNewPlayer(Player player, String password) {
	try (PreparedStatement query = connection.prepareStatement(REGISTER_USER_QUERY)) {
	    query.setString(1, player.getName());
	    query.setString(2, password);
	    query.setString(3, player.getAddress().toString());
	    query.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

	    query.execute();

	    player.sendMessage(text.getRegisterRegisteredText());
	    unfreezePlayer(player);
	} catch (SQLException e) {
	    log.warning("Error registering the new player. Error: " + e.getMessage());
	}

    }

    /**
     * This return true if a record with this name already exists in the datebase or
     * else false
     * 
     * @param checkedPlayer
     * @return true - if a player is already registered
     */
    private boolean checkIfPlayerAlreadyRegistered(Player checkedPlayer) {
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
