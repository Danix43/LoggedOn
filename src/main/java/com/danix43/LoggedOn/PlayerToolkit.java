package com.danix43.LoggedOn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
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
		// player.getInventory().clear();
		player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(999999999, 999999));
		player.addPotionEffect(PotionEffectType.JUMP.createEffect(999999999, 128));
		player.setWalkSpeed(0.0F);
		player.setGameMode(GameMode.ADVENTURE);
		player.closeInventory();
		player.setCanPickupItems(false);
		player.getPlayer().setVelocity(new Vector().zero());
		player.teleport(player.getLocation());
	}

	public static void unfreezePlayer(Player player) {
		player.removePotionEffect(PotionEffectType.BLINDNESS);
		player.removePotionEffect(PotionEffectType.JUMP);
		player.removePotionEffect(PotionEffectType.SLOW);
		player.setCanPickupItems(true);
		player.setWalkSpeed(0.2F);
		player.setGameMode(GameMode.SURVIVAL);
	}

	public static String itemStackArrayToBase64(ItemStack[] items) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);) {
			boos.writeInt(items.length);

			for (int i = 0; i < items.length; i++) {
				boos.writeObject(items[i]);
			}

			return Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (IOException e) {
			System.err.println("Error converting item stack to base64. Error: " + e.getMessage());
			return "";
		}

	}

	public static ItemStack[] base64ToItemStack(String itemStacksBase64) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(itemStacksBase64));
				BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);) {

			ItemStack[] items = new ItemStack[bois.readInt()];

			for (int i = 0; i <= items.length; i++) {
				items[i] = (ItemStack) bois.readObject();
			}

			return items;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error converting the base64 to items stack. Error: " + e.getMessage());
			return new ItemStack[0];
		}

	}

}
