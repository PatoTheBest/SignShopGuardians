package com.minebone.signshopguardian.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minebone.signshopguardian.GuardianManager;

public class SQLImporter implements CommandExecutor {

	private Connection sqlConnection;
	private GuardianManager manager;

	public SQLImporter(GuardianManager manager) {
		this.manager = manager;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!sender.isOp()) {
			return false;
		}
		if (args.length != 4) {
			sender.sendMessage(ChatColor.RED + "Uso: /sqlimport <ip> <usuario> <clave> <base de datos>");
			return false;
		}
		importFromDatabase(args);
		return true;
	}

	public void importFromDatabase(String[] args) {
		try {
			connect(args);
			ResultSet set = query("select * from PlayerMeta");
			while (set.next()) {
				String player = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("Playername"))).getName();
				int guardians = Integer.parseInt(set.getString("Metavalue"));
				manager.addGuardians(player, guardians);
			}
			sqlConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(String[] args) {
		try {
			sqlConnection = DriverManager.getConnection("jdbc:mysql://" + args[0] + ":3306/" + args[3] + "?autoReconnect=true", args[1], args[2]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet query(String qry) {
		ResultSet rs = null;
		try {
			Statement st = sqlConnection.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			System.err.println(e);
		}
		return rs;
	}

}
