package com.minebone.signshopguardian;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.minebone.signshopguardian.util.GuardianPlayer;
import com.minebone.signshopguardian.util.GuardiansFile;

public class GuardianManager {
	
	private GuardiansFile file;
	private Map<String, GuardianPlayer> players;
	
	public GuardianManager(GuardiansFile file) {
		this.file = file;
		this.players = new HashMap<String, GuardianPlayer>();
	}
	
	public void loadPlayers() {
		if(file.getYamlConfiguration().get("players") == null) {
			return;
		}
		for(String string : file.getYamlConfiguration().getConfigurationSection("players").getKeys(false)) {
			ConfigurationSection configurationSection = file.getYamlConfiguration().getConfigurationSection("players." + string);
			players.put(string.toLowerCase(), new GuardianPlayer(configurationSection.getValues(true)));
		}
	}
	
	public int removeGuardians(String player, int amount) {
		return addGuardians(player.toLowerCase(), -amount);
	}
	
	public int addGuardians(String player, int amount) {
		if(player == null || player.isEmpty()) {
			return 0;
		}
		GuardianPlayer gPlayer = getPlayer(player.toLowerCase(), true);
		if(gPlayer.getGuardians() + amount <= 0) {
			players.remove(player.toLowerCase());
			gPlayer.eat();
			update();
			return 0;
		}
		gPlayer.setGuardians(gPlayer.getGuardians() + amount);
		update();
		return gPlayer.getGuardians();
	}
	
	public int getGuardians(String player) {
		return getPlayer(player.toLowerCase()) == null ? 0 : getPlayer(player.toLowerCase()).getGuardians();
	}
	
	public GuardianPlayer getPlayer(String player) {
		return getPlayer(player, false);
	}
	
	public GuardianPlayer getPlayer(String player, boolean b) {
		return b ? (players.get(player.toLowerCase()) == null ? getNewPlayer(player.toLowerCase()) : players.get(player.toLowerCase())) : players.get(player.toLowerCase());
	}
	
	public GuardianPlayer getNewPlayer(String player) {
		GuardianPlayer newPlayer = new GuardianPlayer(player);
		players.put(player.toLowerCase(), newPlayer);
		update();
		return newPlayer;
	}
	
	public void update() {
		file.getYamlConfiguration().set("players", serializePlayers());
		file.save();
	}
	
	public Map<String, Object> serializePlayers() {
		Map<String, Object> result = new HashMap<String, Object>();
		for(int index = 0; index < players.size(); index++) {
			String player = (String) players.keySet().toArray()[index];
			GuardianPlayer gPlayer = players.get(player);
			players.remove(player);
			players.put(gPlayer.getPlayer().toLowerCase(), gPlayer);
			result.put(gPlayer.getPlayer(), gPlayer.serialize());
		}
		return result;
	}

}
