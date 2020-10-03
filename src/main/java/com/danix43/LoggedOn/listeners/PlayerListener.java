package com.danix43.LoggedOn.listeners;

import static com.danix43.LoggedOn.tools.PlayerToolkit.freezePlayer;
import static com.danix43.LoggedOn.tools.PlayerToolkit.saveInventoryToDb;

import java.sql.Connection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.danix43.LoggedOn.tools.TextServer;

public class PlayerListener implements Listener {

    private Connection connection;
    private TextServer text;

    public PlayerListener(Connection dbConnection, TextServer texts) {
	this.connection = dbConnection;
	this.text = texts;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	Player player = event.getPlayer();
	freezePlayer(player);
	player.sendMessage(text.getPlayerEnterText());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
	Player player = event.getPlayer();
	saveInventoryToDb(player, connection);
    }
}
