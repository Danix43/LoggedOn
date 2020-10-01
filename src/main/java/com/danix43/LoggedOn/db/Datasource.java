package com.danix43.LoggedOn.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class Datasource {
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS lo_users("
            + "id int NOT NULL AUTO_INCREMENT," + "username varchar(255) NOT NULL UNIQUE,"
            + "password varchar(255) NOT NULL," + "ip varchar(255)," + "registerdate datetime,"
            + "armor BLOB, inventory BLOB," + "PRIMARY KEY(id)" + ");";

    private static final Logger log = Bukkit.getLogger();

    private Connection connection;

    public Connection getConnection() {
	return connection;
    }

    // read db connection data from config file and create the connection object
    public void createDbConnection() {
	try {
	    this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loggedon?useSSL=false",
	            "root",
	            "danix1923");
	    log.info("Connected to db, creating table");
	    System.out.println("Connected to db, creating table");
	    createTable();
	} catch (SQLException e) {
	    log.log(Level.SEVERE, "Can't connect to the database. Error: " + e.getMessage());
	}

    }

    private void createTable() {
	try (PreparedStatement query = connection.prepareStatement(CREATE_TABLE_QUERY)) {
	    log.info("Creating table query: " + CREATE_TABLE_QUERY);
	    query.execute();
	    System.out.println("Table created");
	} catch (SQLException e) {
	    log.log(Level.SEVERE, "Can't create the table. Error: " + e.getMessage());
	}
    }

    public void disconnectFromDb() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
	    log.log(Level.SEVERE, "Can't close the connection to the database. Error: " + e.getMessage());
        }
    }

}
