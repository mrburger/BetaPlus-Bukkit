package com.mrburgerus.beta_plus;

import com.mrburgerus.beta_plus.world.alpha_plus.AlphaPlusGenSettings;
import com.mrburgerus.beta_plus.world.alpha_plus.BiomeProviderAlphaPlus;
import com.mrburgerus.beta_plus.world.alpha_plus.ChunkGeneratorAlphaPlus;
import com.mrburgerus.beta_plus.world.beta_plus.BetaPlusGenSettings;
import com.mrburgerus.beta_plus.world.beta_plus.BiomeProviderBetaPlus;
import com.mrburgerus.beta_plus.world.beta_plus.ChunkGeneratorBetaPlus;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.generator.CustomChunkGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotWorldConfig;

import java.util.HashMap;

public class BetaPlusPlugin extends JavaPlugin
{
	private HashMap<String, SpigotWorldConfig> worlds;


	@Override
	public void onEnable()
	{
		super.onEnable();
		this.worlds = new HashMap<String, SpigotWorldConfig>(2);
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		System.out.println("Creating Chunk Generator");
		World world = (World) Bukkit.getWorld(worldName);
		if (id.equals("ALPHA"))
		{
			AlphaPlusGenSettings a = new AlphaPlusGenSettings();
			return new CustomChunkGenerator(world, world.getSeed(), new ChunkWrapper(new ChunkGeneratorAlphaPlus(world , new BiomeProviderAlphaPlus(world), a)));
		}
		else
		{
			BetaPlusGenSettings b = new BetaPlusGenSettings();
			return new CustomChunkGenerator(world, world.getSeed(), new ChunkWrapper(new ChunkGeneratorBetaPlus(world , new BiomeProviderBetaPlus(world, b), b)));
		}
	}
}
