package com.minebone.signshopguardian.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class GuardianPlayer implements ConfigurationSerializable {

	private String player;
	private int guardians;
	
	public GuardianPlayer(String player) {
		this.player = player;
		this.guardians = 0;
	}
	
	public GuardianPlayer(Map<String, Object> map) {
		player = (String) map.get("player");
		guardians = (Integer) map.get("guardians");
	}
	
	public String getPlayer() {
		return player;
	}
	
	public int getGuardians() {
		return guardians;
	}
	
	public void setGuardians(int amount) {
		this.guardians = amount;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("player", player);
		result.put("guardians", guardians);
		return result;
	}
	
	public void eat() {
		this.player = null;
	}

}
