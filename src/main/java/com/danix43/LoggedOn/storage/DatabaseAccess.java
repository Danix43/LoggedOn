package com.danix43.LoggedOn.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public abstract class DatabaseAccess {
    private static final Logger log = Bukkit.getLogger();

    final String DB_HOST;
    final int DB_PORT;
    final String DB_NAME;
    final String DB_USERNAME;
    final String DB_PASSWORD;

    Connection connection;

    public DatabaseAccess(ConfigurationSection config) {
	this.DB_HOST = config.getString("host-name");
	this.DB_PORT = config.getInt("port");
	this.DB_NAME = config.getString("database-name");
	this.DB_USERNAME = config.getString("database-username");
	this.DB_PASSWORD = config.getString("database-password");

	// this means that the config is not modified and should be by the user
	if (DB_HOST.equals("replace me")) {
	    log.log(Level.SEVERE,
	            "Check your config file, you should provide your credentials for the database connection");
	}
    }

    // read db connection data from config file and create the connection object
    public abstract void createDbConnection();

    public abstract void createTable();

    public Connection getConnection() {
	return connection;
    }

    public void disconnectFromDb() {
	try {
	    if (connection != null && !connection.isClosed()) {
		connection.close();
	    }
	} catch (SQLException e) {
	    log.log(Level.SEVERE,
	            String.format("Can't close the connection to the database. Error: %s", e.getMessage()));
	}
    }

}
