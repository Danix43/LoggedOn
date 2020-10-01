package com.danix43.LoggedOn.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class PlayerToolkit {
    private static final Logger log = Logger.getGlobal();

    private PlayerToolkit() {

    }

    public static void freezePlayer(Player player) {
	player.getInventory().clear();
	player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(999999999, 999999));
	player.addPotionEffect(PotionEffectType.JUMP.createEffect(999999999, 128));
	player.setWalkSpeed(0.0F);
	player.setGameMode(GameMode.ADVENTURE);
	player.closeInventory();
	player.setCanPickupItems(false);
	player.getPlayer().setVelocity(new Vector().zero());
    }

    public static void unfreezePlayer(Player player) {
	player.removePotionEffect(PotionEffectType.BLINDNESS);
	player.removePotionEffect(PotionEffectType.JUMP);
	player.removePotionEffect(PotionEffectType.SLOW);
	player.setCanPickupItems(true);
	player.setWalkSpeed(0.2F);
	player.setGameMode(GameMode.SURVIVAL);
    }

    public static void loadInventory(Player player, Connection dbConnection) {
	String sql = "SELECT armor, inventory FROM lo_users WHERE username = ?;";

	try (PreparedStatement query = dbConnection.prepareStatement(sql)) {
	    query.setString(1, player.getName());

	    try (ResultSet result = query.executeQuery()) {
		result.next();

		Blob armor = result.getBlob("armor");
		Blob inventory = result.getBlob("inventory");

		try {
		    player.getInventory().setArmorContents(
		            convertByteArrToItems(armor.getBytes(1L, Math.toIntExact(armor.length()))));
		    player.getInventory().setStorageContents(
		            convertByteArrToItems(inventory.getBytes(1L, Math.toIntExact(inventory.length()))));
		} catch (NullPointerException e) {
		    System.out.println("The armor or inventory blob is empty");
		}

	    } catch (SQLException e) {
		log.warning("Error retrieving the inventory from database. Error: " + e.getMessage());
	    }
	} catch (SQLException e) {
	    log.warning("Error loading the inventory. Error: " + e.getMessage());
	}
    }

    /**
     * Saves the player's full inventory, divided as armor and storage contents, in
     * different databases rows as BLOBs
     * 
     * @param playerName
     * @param playerArmor     - The player's armor in the four slots
     * @param playerInventory - The player's inventory in all slots, excluding the
     *                        armor slots
     * @param connection      - A connection to the database
     */
    public static void saveInventoryToDb(Player player, Connection dbConnection) {
	String sql = "UPDATE lo_users SET armor = ?, inventory = ? WHERE username = ?;";
	try (PreparedStatement query = dbConnection.prepareStatement(sql)) {

	    Blob armorBLOB = dbConnection.createBlob();
	    armorBLOB.setBytes(1L, convertItemsToByteArr(player.getInventory().getArmorContents()));

	    Blob inventoryBLOB = dbConnection.createBlob();
	    inventoryBLOB.setBytes(1L, convertItemsToByteArr(player.getInventory().getStorageContents()));

	    System.out.println("armor Blob: " + armorBLOB);
	    System.out.println("inventory Blob: " + inventoryBLOB);

	    query.setBlob(1, armorBLOB);
	    query.setBlob(2, inventoryBLOB);
	    query.setString(3, player.getName());

	    query.executeUpdate();
	} catch (SQLException e) {
	    log.warning("Error saving the inventory to database. Error: " + e.getMessage());
	}
    }

    private static byte[] convertItemsToByteArr(ItemStack[] items) {
	try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
	    boos.writeInt(items.length);

	    for (int i = 0; i < items.length; i++) {
		try {
		    boos.writeObject(items[i]);
		} catch (NullPointerException e) {
		    items[i] = null;
		}
	    }

	    return baos.toByteArray();

	} catch (IOException e) {
	    log.warning("Error converting from items to byte arr: " + e.getMessage());
	    return new byte[1];
	}
    }

    public static ItemStack[] convertByteArrToItems(byte[] itemArr) {
	try (ByteArrayInputStream bais = new ByteArrayInputStream(itemArr);
	        BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {

	    ItemStack[] items = new ItemStack[bois.readInt()];

	    for (int i = 0; i < items.length; i++) {
		try {
		    items[i] = (ItemStack) bois.readObject();
		} catch (NullPointerException e) {
		    items[i] = null;
		}
	    }

	    return items;

	} catch (IOException | ClassNotFoundException e) {
	    log.warning("Error converting from byte arr to items: " + e.getMessage());
	    return new ItemStack[1];
	}
    }

}
