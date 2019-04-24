package com.mrburgerus.beta_plus;

import com.mrburgerus.beta_plus.world.AbstractOldChunkGenerator;
import com.mrburgerus.beta_plus.world.alpha_plus.AlphaPlusGenSettings;
import com.mrburgerus.beta_plus.world.alpha_plus.BiomeProviderAlphaPlus;
import com.mrburgerus.beta_plus.world.alpha_plus.ChunkGeneratorAlphaPlus;
import com.mrburgerus.beta_plus.world.beta_plus.BetaPlusGenSettings;
import com.mrburgerus.beta_plus.world.beta_plus.BiomeProviderBetaPlus;
import com.mrburgerus.beta_plus.world.beta_plus.ChunkGeneratorBetaPlus;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.generator.CustomChunkGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotWorldConfig;

import java.util.HashMap;

public class BetaPlusPlugin extends JavaPlugin
{

	@Override
	public void onEnable()
	{
		super.onEnable();
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
		// Creates NPE
		//World world = ((CraftWorld)Bukkit.getWorld(worldName)).getHandle();
		//World world = ((CraftWorld)this.getServer().getWorld(worldName)).getHandle();
		World world = ((CraftWorld) this.getServer().createWorld(new WorldCreator(worldName))).getHandle();
		if (id != null && id.equals("ALPHA") && false)
		{
			AlphaPlusGenSettings a = new AlphaPlusGenSettings();
			//return new CustomChunkGenerator(world, world.getSeed(), new ChunkWrapper((AbstractOldChunkGenerator) new ChunkGeneratorAlphaPlus(world , new BiomeProviderAlphaPlus(world), a)));
		}
		else
		{
			System.out.println("Created Generator");
			BetaPlusGenSettings b = new BetaPlusGenSettings();
			//return new CustomChunkGenerator(world, world.getSeed(), new ChunkWrapper(new ChunkGeneratorBetaPlus(world , new BiomeProviderBetaPlus(world, b), b)));
			//return new CustomChunkGenerator(world , world.getSeed(), new ChunkGeneratorBetaPlus(world , new BiomeProviderBetaPlus(world, b), b));
			//return new CustomChunkGenerator(world, world.getSeed(), new ChunkWrapper((AbstractOldChunkGenerator) new ChunkGeneratorBetaPlus(world , new BiomeProviderBetaPlus(world, b), b)));
			return new ChunkWrapper(new ChunkGeneratorBetaPlus(world, new BiomeProviderBetaPlus(world, b), b));
		}
		return null;
	}
}
