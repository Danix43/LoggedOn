package com.danix43.LoggedOn.listeners;

import static com.danix43.LoggedOn.tools.PlayerToolkit.freezePlayer;
import static com.danix43.LoggedOn.tools.PlayerToolkit.saveInventoryToDb;

import java.sql.Connection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private Connection connection;

    public PlayerListener(Connection dbConnection) {
	this.connection = dbConnection;
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
	saveInventoryToDb(player, connection);
    }
}
