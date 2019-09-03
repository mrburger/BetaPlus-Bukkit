package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.world.beta_new.BetaPlusGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class BetaPlus extends JavaPlugin
{
	// Fields
	public static Logger LOGGER;
	public static final int seaLevel = 64;
	public static final int seaDepth = 20;

	@Override
	public void onLoad()
	{
		LOGGER = this.getLogger();

	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		this.saveDefaultConfig();
		this.saveDefaultConfig();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		return new BetaPlusGenerator();
		//return injectBetaPlusWorld(world);
	}

	/*
	private BetaPlusGenerator injectBetaPlusWorld(World world)
	{
		LOGGER.LOGGER(Level.SEVERE, "Start Inject");
		if (world == null)
		{
			LOGGER.LOGGER(Level.SEVERE, "WORLD NOT LOADED");
			//Bukkit.createWorld(new WorldCreator().generator())
		}

		WorldServer serverWorld = ((CraftWorld) world).getHandle();
		ChunkProviderServer chunkProvider = serverWorld.getChunkProvider();
		BetaPlusGenerator generator = new BetaPlusGenerator();
		// COULD BE ERRORS
		if (chunkProvider.chunkGenerator instanceof CustomChunkGenerator)
			ReflectionHelper.setValueInFieldOfType(chunkProvider, net.minecraft.server.v1_14_R1.ChunkGenerator.class, new BetaPlusInternalGenerator(serverWorld, generator, new BiomeProviderBetaPlus(serverWorld)));
		return generator;
	}
	*/
}
