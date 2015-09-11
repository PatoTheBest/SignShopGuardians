package com.minebone.signshopguardian.listeners;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.configuration.SignShopConfig;

import com.minebone.signshopguardian.SignShopGuardian;

public class SignShopGuardianListener implements Listener {

	private List<String> players;

	public SignShopGuardianListener() {
		players = new ArrayList<String>();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (!SignShopGuardian.isEnabledForWorld(event.getEntity().getWorld()))
			return;

		if (!event.getDrops().isEmpty()) {
			Player player = event.getEntity();
			if (SignShopGuardian.getManager().getGuardians(player.getName()) > 0) {
				players.add(player.getName());
				event.setKeepInventory(true);
				if (SignShopGuardian.isEnableSaveXP()) {
					event.setKeepLevel(true);
					event.setDroppedExp(0);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerSpawn(PlayerRespawnEvent event) {
		if (!SignShopGuardian.isEnabledForWorld(event.getPlayer().getWorld()))
			return;

		Player player = event.getPlayer();
		if (players.contains(player.getName())) {
			if (SignShopGuardian.getManager().getGuardians(player.getName().toLowerCase()) > 0) {
				int guardiansLeft = SignShopGuardian.getManager().removeGuardians(player.getName().toLowerCase(), 1);
				String message;
				Map<String, String> messageParts = new LinkedHashMap<String, String>();
				messageParts.put("!guardians", guardiansLeft + "");
				if (guardiansLeft == 0) 
					message = SignShopConfig.getError("player_used_last_guardian", messageParts);
				else
					message = SignShopConfig.getError("player_has_guardians_left", messageParts);

				DelayedGiver delay;
				delay = new DelayedGiver(player, message);

				Bukkit.getServer().getScheduler().runTaskLater(SignShop.getInstance(), delay, 20 * 2);
			} else {
				player.sendMessage(SignShopConfig.getError("player_has_no_guardian", null));
			}
			players.remove(player.getName());
		}
	}

	private class DelayedGiver implements Runnable {

		private Player player;
		private String message;

		private DelayedGiver(Player player, String message) {
			this.player = player;
			this.message = message;
		}

		@Override
		public void run() {
			if (message != null)
				player.sendMessage(message);
			message = null;
			player = null;
		}
	}
}
