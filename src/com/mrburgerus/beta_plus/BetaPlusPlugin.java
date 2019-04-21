package com.mrburgerus.beta_plus;

import com.mrburgerus.beta_plus.util.WorldConfig;
import com.mrburgerus.beta_plus.world.beta_plus.BetaPlusGenerator;
import com.mrburgerus.beta_plus.world.beta_plus.BiomeProviderBetaPlus;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public class BetaPlusPlugin extends JavaPlugin
{
	private HashMap<String, WorldConfig> worlds;


	@Override
	public void onEnable()
	{
		super.onEnable();
		this.worlds = new HashMap<String, WorldConfig>(2);
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		// Does not work
		return new BetaPlusGenerator();
	}
}
