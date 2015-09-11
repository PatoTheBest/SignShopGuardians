package com.minebone.signshopguardian;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.player.SignShopPlayer;
import org.wargamer2010.signshop.util.signshopUtil;

import com.minebone.signshopguardian.util.GuardianPlayer;

public class CommandHandler implements Listener {

	public CommandHandler() {

	}

	private static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(message);
	}

	public static void handleGuardianQuery(CommandSender sender, String args[]) {
		GuardianPlayer gPlayer;
		String player;

		if (args.length > 0) {
			gPlayer = SignShopGuardian.getManager().getPlayer(args[0]);
			player = args[0];
		} else {
			if (!(sender instanceof Player)) {
				sendMessage(sender, "Specify a player to use this command on the console");
				return;
			}
			gPlayer = SignShopGuardian.getManager().getPlayer(sender.getName());
			player = sender.getName();
		}

		Map<String, String> parts = new HashMap<String, String>();
		parts.put("!player", player);
		parts.put("!guardians", gPlayer == null ? 0 + "": gPlayer.getGuardians() + "");

		if (args.length > 0)
			sendMessage(sender, SignShopConfig.getError("other_player_has_guardians_left", parts));
		else
			sendMessage(sender, SignShopConfig.getError("player_has_guardians_left", parts));
	}

	public static boolean handleAddGuardians(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			SignShopPlayer player = new SignShopPlayer((Player) sender);
			if (!signshopUtil.hasOPForCommand(player))
				return true;
		}

		if (args.length < 1)
			return false;

		String playername;
		if (args.length > 1) {
			playername = args[0];

		} else {
			if (!(sender instanceof Player)) {
				sendMessage(sender, "Specify a player to use this command on the console");
				return true;
			}
			playername = ((Player) sender).getName();
		}

		try {
			GuardianPlayer dude = SignShopGuardian.getManager().getPlayer(playername);
			int index = (args.length == 1 ? 0 : 1);
			int count = Integer.parseInt(args[index]);
			// Taking away guardians should be possible by passing negatives
			if (count < 0 && dude.getGuardians() < Math.abs(count)) {
				Map<String, String> temp = new LinkedHashMap<String, String>();
				temp.put("!guardians", dude.getGuardians() + "");
				sendMessage(sender, SignShopConfig.getError("other_player_has_insufficient_guardians", temp));
				return true;
			}

			SignShopGuardian.getManager().addGuardians(playername, count);

			Map<String, String> temp = new LinkedHashMap<String, String>();
			temp.put("!guardians", Integer.toString(count));

			if (args.length == 1)
				sendMessage(sender, SignShopConfig.getError("added_guardians_for_self", temp));
			else
				sendMessage(sender, SignShopConfig.getError("added_guardians_for_player", temp));
		} catch (NumberFormatException ex) {
			return false;
		}

		return true;
	}
}
