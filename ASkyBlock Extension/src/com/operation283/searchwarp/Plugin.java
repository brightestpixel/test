package com.operation283.searchwarp;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.wasteofplastic.askyblock.ASkyBlockAPI;

public class Plugin extends JavaPlugin {
	public static Plugin Instance;

	@Override
	public void onEnable() {
		createConfig();
		
		getServer().getPluginManager().registerEvents(new Events(), this);

		Instance = this;

		getLogger().log(Level.INFO, "SearchWarp has initialized successfully.");
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "SearchWarp has shut down successfully.");
	}

	private void createConfig() {
		FileConfiguration config = getConfig();
		config.addDefault("messages.teleporting", "'&eTeleporting to %s's island.'");
		config.addDefault("messages.invalid-permissions", "'&cYou do not have permission.'");
		config.addDefault("messages.island-not-found", "'&cNo island match found.'");
		config.options().copyDefaults(true);
		saveConfig();

		if (!getDataFolder().exists())
			getDataFolder().mkdirs();

		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists())
			saveDefaultConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player) || args.length == 0)
			return false;
		
		Player player = (Player) sender;

		if (player.hasPermission("searchwarp.search")) {
			UUID[] warps = searchThroughWarps(String.join(" ", args), false);
			if (warps.length == 0) {
				warps = searchThroughWarps(String.join(" ", args), true);
				if (warps.length == 0) {
					player.sendMessage(getColored(getConfig().getString("messages.island-not-found")));
					return true;
				}
			}
			UUID chosenWarp = warps[new Random().nextInt(warps.length)];
			player.sendMessage(getColored(String.format(getConfig().getString("messages.teleporting"), Bukkit.getOfflinePlayer(chosenWarp).getName())));
			player.performCommand("is warp " + Bukkit.getOfflinePlayer(chosenWarp).getName());
		} else {
			player.sendMessage(getColored(getConfig().getString("messages.invalid-permissions")));
		}

		return true;
	}

	private UUID[] searchThroughWarps(String term, boolean separateTerms) {
		Set<UUID> warps = ASkyBlockAPI.getInstance().listWarps();
		ArrayList<UUID> matches = new ArrayList<UUID>();

		String[] terms = term.split(" ");

		for (UUID player : warps) {
			String desc = getDesc(player);

			if (!separateTerms)
				if (desc.toLowerCase().contains(term.toLowerCase()))
					matches.add(player);
				else
					for (String termSplit : terms)
						if (desc.toLowerCase().contains(termSplit.toLowerCase()))
							matches.add(player);
		}

		return matches.toArray(new UUID[matches.size()]);
	}

	private String getDesc(UUID player) {
		Location loc = ASkyBlockAPI.getInstance().getWarp(player);

		Block block = loc.getBlock();

		if (!block.getType().equals(Material.SIGN_POST) && !block.getType().equals(Material.WALL_SIGN)) {
			return "";
		}

		Sign sign = (Sign) block.getState();

		StringBuilder desc = new StringBuilder();
		for (int i = 1; i <= 3; i++) {
			desc.append(sign.getLine(i));
			if (i < 3)
				desc.append(" ");
		}
		return desc.toString();
	}
	
	private String getColored(String str) {
		return ChatColor.translateAlternateColorCodes('&', str).substring(1, str.length() - 1);
	}
}
