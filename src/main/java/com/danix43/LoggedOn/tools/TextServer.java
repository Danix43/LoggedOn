package com.danix43.LoggedOn.tools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

/**
 * TextServer as in text been served
 * Basically the translation interface 
 */
public class TextServer {

    private static TextServer instance;

    private final Map<String, String> texts;

    /**
     * Retrieve the text from the config file
     * @param config
     */
    private TextServer(ConfigurationSection config) {
	texts = populateList(config);
    }

    private static Map<String, String> populateList(ConfigurationSection config) {
	Map<String, String> map = new HashMap<>();

	map.put("player-enter",
	        config.getString("player-enter"));

	map.put("register-already-registered", config.getString("register-already-registered"));
	map.put("register-registered", config.getString("register-registered"));
	map.put("register-not-player", config.getString("register-not-player"));

	map.put("login-not-registered",
	        config.getString("login-not-registered"));
	map.put("login-not-player", config.getString("login-not-player"));
	map.put("login-loggedon", config.getString("login-loggedon"));
	map.put("login-wrong-password", config.getString("login-wrong-password"));
	map.put("login-failed-login", config.getString("login-failed-login"));

	return map;
    }

    public static synchronized TextServer getInstance(ConfigurationSection config) {
	if (instance == null) {
	    instance = new TextServer(config);
	}
	return instance;
    }

    public String getPlayerEnterText() {
	return texts.get("player-enter");
    }

    public String getRegisterAlreadyRegisterd() {
	return texts.get("register-already-registered");
    }

    public String getRegisterRegisteredText() {
	return texts.get("register-registered");
    }

    public String getRegisterNotPlayerText() {
	return texts.get("register-not-player");
    }

    public String getLoginNotRegisteredText() {
	return texts.get("login-not-registered");
    }

    public String getLoginNotPlayerText() {
	return texts.get("login-not-player");
    }

    public String getLoginLoggedOnText() {
	return texts.get("login-loggedon");
    }

    public String getLoginWrongPasswordText() {
	return texts.get("login-wrong-password");
    }

    public String getLoginFailedLoginText() {
	return texts.get("login-failed-login");
    }

}
