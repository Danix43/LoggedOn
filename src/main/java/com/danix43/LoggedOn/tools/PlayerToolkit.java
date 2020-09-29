package com.danix43.LoggedOn.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class PlayerToolkit {

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
	public static void saveInventoryToDb(Player player, Connection connection) {
		String sql = "UPDATE lo_users SET armor = ?, inventory = ? WHERE username = ?;";
		try (PreparedStatement query = connection.prepareStatement(sql)) {

			Blob armorBLOB = connection.createBlob();
			armorBLOB.setBytes(1L, convertItemsToByteArr(player.getInventory().getArmorContents()));

			Blob inventoryBLOB = connection.createBlob();
			inventoryBLOB.setBytes(1L, convertItemsToByteArr(player.getInventory().getStorageContents()));

			query.setBlob(1, armorBLOB);
			query.setBlob(2, inventoryBLOB);
			query.setString(3, player.getName());

			System.out.println("Rows updated: " + query.executeUpdate());
		} catch (SQLException e) {
			System.err.println("Error saving the inventory to database. Error: " + e.getMessage());
		}
	}

	private static byte[] convertItemsToByteArr(ItemStack[] items) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
			boos.writeInt(items.length);

			for (int i = 0; i < items.length; i++) {
				try {
					boos.writeObject(items[i]);
					System.out.println("Item converted: " + items[i].toString());
				} catch (NullPointerException e) {
					items[i] = null;
				}
			}

			return baos.toByteArray();

		} catch (IOException e) {
			System.err.println("Error converting from items to byte arr: " + e.getMessage());
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
					System.out.println("Item converted: " + items[i].toString());
				} catch (NullPointerException e) {
					items[i] = null;
				}
			}

			return items;

		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error converting from byte arr to items: " + e.getMessage());
			return new ItemStack[1];
		}
	}

}
