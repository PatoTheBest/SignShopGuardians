package com.minebone.signshopguardian;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.configuration.configUtil;
import org.wargamer2010.signshop.metrics.setupMetrics;

import com.minebone.signshopguardian.listeners.SignShopGuardianListener;
import com.minebone.signshopguardian.util.GuardiansFile;
import com.minebone.signshopguardian.util.SQLImporter;

public class SignShopGuardian extends JavaPlugin {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static SignShopGuardian instance = null;

	// Settings
	private static List<String> EnabledWorlds;
	private static boolean EnableSaveXP = false;
	private static GuardianManager manager;

	/**
	 * Log given message at given level for SignShopGuardian
	 * 
	 * @param message Message to log
	 * @param level Level to log at
	 */
	public static void log(String message, Level level) {
		if (!message.isEmpty())
			logger.log(level, ("[SignShopGuardian] " + message));
	}

	@Override
	public void onEnable() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if (!pm.isPluginEnabled("SignShop")) {
			log("SignShop is not loaded, can not continue.", Level.SEVERE);
			pm.disablePlugin(this);
			return;
		}
		GuardiansFile file = new GuardiansFile(this);
		file.reload();
		manager = new GuardianManager(file);
		manager.loadPlayers();
		getCommand("sqlimport").setExecutor(new SQLImporter(manager));
		saveConfig();
		pm.registerEvents(new CommandHandler(), this);
		pm.registerEvents(new SignShopGuardianListener(), this);
		createDir();

		String filename = "config.yml";
		FileConfiguration ymlThing = configUtil.loadYMLFromPluginFolder(this, filename);
		if (ymlThing != null) {
			configUtil.loadYMLFromJar(this, SignShopGuardian.class, ymlThing, filename);

			SignShopConfig.setupOperations(configUtil.fetchStringStringHashMap("signs", ymlThing), "org.wargamer2010.signshopguardian.operations");
			SignShopConfig.registerErrorMessages(configUtil.fetchStringStringHashMap("errors", ymlThing));
			for (Map.Entry<String, HashMap<String, String>> entry : configUtil.fetchHasmapInHashmap("messages", ymlThing).entrySet()) {
				SignShopConfig.registerMessages(entry.getKey(), entry.getValue());
			}

			getSettings(ymlThing);
		}

		setupMetrics metrics = new setupMetrics(this);
		if (!metrics.isOptOut()) {
			if (metrics.setup())
				log("Succesfully started Metrics, see http://mcstats.org for more information.", Level.INFO);
			else
				log("Could not start Metrics, see http://mcstats.org for more information.", Level.INFO);
		}

		setInstance(this);

		log("Enabled", Level.INFO);
	}

	@Override
	public void onDisable() {
		log("Disabled", Level.INFO);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		String commandName = cmd.getName().toLowerCase();
		if (commandName.equalsIgnoreCase("countguards") || commandName.equalsIgnoreCase("countguardians") || commandName.equalsIgnoreCase("getguardians") || commandName.equalsIgnoreCase("guardiansleft")) {
			CommandHandler.handleGuardianQuery(sender, args);
		} else if (commandName.equalsIgnoreCase("addguardians")) {
			CommandHandler.handleAddGuardians(sender, args);
		}
		return true;
	}

	/**
	 * Retrieves the settings from the SSGuardian config file
	 * This does not include the SignShop signs/messages/errors sections
	 * 
	 * @param ymlThing
	 */
	private void getSettings(FileConfiguration ymlThing) {
		EnabledWorlds = ymlThing.getStringList("EnabledWorlds"); // Empty list
																	// if not
																	// found
		EnableSaveXP = ymlThing.getBoolean("EnableSaveXP", EnableSaveXP);
	}

	private void createDir() {
		if (!this.getDataFolder().exists()) {
			if (!this.getDataFolder().mkdir()) {
				log("Could not create plugin folder!", Level.SEVERE);
			}
		}
	}

	private static void setInstance(SignShopGuardian newinstance) {
		instance = newinstance;
	}

	/**
	 * Gets the instance of SignShopGuardian
	 * 
	 * @return instance
	 */
	public static SignShopGuardian getInstance() {
		return instance;
	}

	/**
	 * Returns True if SignshopGuardian is enabled for the given World
	 *
	 * @param world
	 * @return
	 */
	public static boolean isEnabledForWorld(World world) {
		if (EnabledWorlds.isEmpty())
			return true;
		for (String sWorld : EnabledWorlds)
			if (sWorld.equalsIgnoreCase(world.getName()))
				return true;
		return false;
	}

	public static GuardianManager getManager() {
		return manager;
	}

	/**
	 * Returns True if XP levels should be saved on player death
	 *
	 * @return EnableSaveXP setting from config
	 */
	public static boolean isEnableSaveXP() {
		return EnableSaveXP;
	}
}
