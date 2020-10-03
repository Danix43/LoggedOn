package com.danix43.LoggedOn.storage;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class MySQLAccess extends DatabaseAccess {
    private static final Logger log = Bukkit.getLogger();

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS lo_users("
            + "id int NOT NULL AUTO_INCREMENT," + "username varchar(255) NOT NULL UNIQUE,"
            + "password varchar(255) NOT NULL," + "ip varchar(255)," + "registerdate datetime,"
            + "armor BLOB, inventory BLOB," + "PRIMARY KEY(id)" + ");";

    public MySQLAccess(ConfigurationSection config) {
	super(config);
    }

    @Override
    public void createDbConnection() {
	try {
	    this.connection = DriverManager.getConnection(
	            String.format("jdbc:mysql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME), DB_USERNAME, DB_PASSWORD);
	    //	    log.info("Connected to db, creating table");
	    //	    System.out.println("Connected to db, creating table");
	    createTable();
	} catch (SQLException e) {
	    log.log(Level.SEVERE, String.format("Can't connect to the database. Error: %s", e.getMessage()));
	}
    }

    @Override
    public void createTable() {
	try (PreparedStatement query = connection.prepareStatement(CREATE_TABLE_QUERY)) {
	    //	    log.info(String.format("Creating table query: %s", CREATE_TABLE_QUERY));
	    query.execute();
	    //	    System.out.println("Table created");
	} catch (SQLException e) {
	    log.log(Level.SEVERE, String.format("Can't create the table. Error: %s", e.getMessage()));
	}
    }

}
