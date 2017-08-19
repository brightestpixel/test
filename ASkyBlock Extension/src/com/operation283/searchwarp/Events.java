package com.operation283.searchwarp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.WarpListEvent;

public class Events implements Listener {
	@EventHandler
	public void OnWarpListUpdate(WarpListEvent event) {
		ArrayList<UUID> warps = (ArrayList<UUID>) event.getWarps();
		Collections.sort(warps, new Comparator<UUID>() {

			@Override
			public int compare(UUID a, UUID b) {
				int alvl = ASkyBlockAPI.getInstance().getIslandLevel(a);
				int blvl = ASkyBlockAPI.getInstance().getIslandLevel(b);

				long alogin = Calendar.getInstance().getTimeInMillis() - Bukkit.getOfflinePlayer(a).getLastPlayed();
				long blogin = Calendar.getInstance().getTimeInMillis() - Bukkit.getOfflinePlayer(b).getLastPlayed();

				float aval = alvl - alogin / 3600000f;
				float bval = blvl - blogin / 3600000f;

				return Float.compare(aval, bval);
			}
			
		});
	}
}