package com.danix43.LoggedOn.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class DatabaseAccess {
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS lo_users("
            + "id int NOT NULL AUTO_INCREMENT," + "username varchar(255) NOT NULL UNIQUE,"
            + "password varchar(255) NOT NULL," + "ip varchar(255)," + "registerdate datetime,"
            + "armor BLOB, inventory BLOB," + "PRIMARY KEY(id)" + ");";

    private final String DB_HOST;
    private final int DB_PORT;
    private final String DB_NAME;
    private final String DB_USERNAME;
    private final String DB_PASSWORD;

    private static final Logger log = Bukkit.getLogger();

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

    private Connection connection;

    public Connection getConnection() {
	return connection;
    }

    // read db connection data from config file and create the connection object
    public void createDbConnection() {
	try {
	    this.connection = DriverManager.getConnection(
	            String.format("jdbc:mysql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME), DB_USERNAME, DB_PASSWORD);
	    log.info("Connected to db, creating table");
	    System.out.println("Connected to db, creating table");
	    createTable();
	} catch (SQLException e) {
	    log.log(Level.SEVERE, String.format("Can't connect to the database. Error: %s", e.getMessage()));
	}

    }

    private void createTable() {
	try (PreparedStatement query = connection.prepareStatement(CREATE_TABLE_QUERY)) {
	    log.info("Creating table query: " + CREATE_TABLE_QUERY);
	    query.execute();
	    System.out.println("Table created");
	} catch (SQLException e) {
	    log.log(Level.SEVERE, String.format("Can't create the table. Error: %s", e.getMessage()));
	}
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
