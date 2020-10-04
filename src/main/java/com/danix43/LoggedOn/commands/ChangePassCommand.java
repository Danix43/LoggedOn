package com.danix43.LoggedOn.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import com.danix43.LoggedOn.tools.TextServer;

public class ChangePassCommand implements CommandExecutor {
    private static final String PASSWORD_PLAYER_QUERY = "SELECT password FROM lo_users WHERE username = ?";
    private static final String PASSWORD_UPDATE_QUERY = "UPDATE lo_users SET password = ? WHERE username = ?";
    private static final Logger log = Bukkit.getLogger();

    private final Connection connection;
    private final TextServer text;

    public ChangePassCommand(Connection connection, TextServer text) {
	this.connection = connection;
	this.text = text;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
	    Player player = (Player) sender;

	    if (args[0] == null || args[1] == null) {
		player.sendMessage(text.getChangePassErrorNotEnoughArgsText());
		return false;
	    }

	    if (checkIfPasswordAreSame(player.getName(), args[0])) {
		changePassword(player.getName(), args[1]);
		player.sendMessage(ChatColor.GREEN + text.getChangePassSuccessText());
		return true;
	    } else {
		player.sendMessage(ChatColor.RED + text.getChangePassFailText());
		return true;
	    }
        }
        return false;
    }

    private void changePassword(String playerName, String newPassword) {
	try (PreparedStatement query = connection.prepareStatement(PASSWORD_UPDATE_QUERY)) {
	    query.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
	    query.setString(2, playerName);

	    query.executeUpdate();
	} catch (SQLException e) {
	    log.warning(text.getChangePassErrorUpdateText());
	    e.printStackTrace();
	}
    }

    private String retrievePasswordFromDb(String playerName) {
	try (PreparedStatement query = connection.prepareStatement(PASSWORD_PLAYER_QUERY)) {
	    query.setString(1, playerName);
	    try (ResultSet result = query.executeQuery()) {
		result.next();
		return result.getString("password");
	    }
	} catch (SQLException e) {
	    log.warning(text.getChangePassErrorRetrieveText());
	    e.printStackTrace();
	    return "";
	}
    }

    private boolean checkIfPasswordAreSame(String playerName, String inputPassword) {
	return BCrypt.checkpw(inputPassword, retrievePasswordFromDb(playerName));
    }
}
