package com.danix43.LoggedOn.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/*
 * TODO: - saving armor and inventory doesn't work at the moment
 */
public class SQLiteAccess extends DatabaseAccess {
    private static final Logger log = Bukkit.getLogger();

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS lo_users(" + "id integer PRIMARY KEY,"
            + "username text NOT NULL UNIQUE," + "password text," + "ip text," + "registerdate datetime,"
            + "armor blob," + "inventory blob)";

    private File dataFolder;

    public SQLiteAccess(ConfigurationSection config, File dataFolder) {
	super(config);
	this.dataFolder = dataFolder;
    }

    @Override
    public void createDbConnection() {
	String dbFile = createDbFile(dataFolder);
	try {
	    this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbFile), DB_USERNAME,
	            DB_PASSWORD);
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

    private String createDbFile(File pathToStorage) {
	if (pathToStorage.exists()) {
	    log.info("The folder exists, returning path");
	} else {
	    log.info("The folder doesn't exists, creating");
	    try {
		Files.createDirectories(pathToStorage.toPath());
	    } catch (IOException e) {
		log.warning(String.format("Error creating the folder containg the database: %s", e.getMessage()));
	    }
	}
	return pathToStorage + File.separator + "storage.db";
    }

}
