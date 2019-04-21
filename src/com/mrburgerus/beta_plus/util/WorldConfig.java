package com.mrburgerus.beta_plus.util;

import com.mrburgerus.beta_plus.world.beta_plus.BetaPlusGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldConfig {
	public static int heightLimit = 128;

	public JavaPlugin plugin;
	public BetaPlusGenerator chunkProvider;
	public boolean isInit = false;
	public String eyeOfEnderMsg;
	public boolean
			oldTreeGrowing = true,
			generateEmerald = false,
			noswamps = true,
			nofarlands = false;
	private final String worldName;

	private WorldConfig(String worldName) {
		this.worldName = worldName;
	}

	public WorldConfig(JavaPlugin plug, String worldName) {
		this.plugin = plug;
		this.worldName = worldName;

	}

	public static WorldConfig emptyConfig(String name) {
		return new WorldConfig(name);
	}
}