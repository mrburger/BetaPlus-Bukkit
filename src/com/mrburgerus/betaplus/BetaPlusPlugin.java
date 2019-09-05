package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.api.ReflectionUtil;
import com.mrburgerus.betaplus.bukkit.DummyBukkitChunkGenerator;
import com.mrburgerus.betaplus.world.beta.ChunkGeneratorBetaPlus;
import com.mrburgerus.betaplus.world.beta.WorldChunkManagerBeta;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Must implement WorldGeneratorApi, as it is not independent
public class BetaPlusPlugin extends JavaPlugin implements Listener
{
	public static Logger LOGGER;
	public static final int seaLevel = 63;
	public static final int seaDepth = 20;
	public static final double oceanYScale = 3.25;
	public static final String KEYWORD = "BETA_PLUS";
	private ChunkGeneratorBetaPlus injected;

	private final Map<String, String> worldGenerators = new HashMap<>();


	@Override
	public void onLoad()
	{
		LOGGER = this.getLogger();
	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public org.bukkit.generator.ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		/*
		World world = Bukkit.getWorld(worldName);

		if (world != null)
		{
			this.worldGenerators.put(world.getUID(), KEYWORD);
		}
		*/
		this.worldGenerators.put(worldName, KEYWORD);
		return new DummyBukkitChunkGenerator(this);
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event)
	{
		this.getForWorld(event.getWorld());
	}

	public void getForWorld(World world)
	{
		//if(worldGenerators.get(world.getUID()).equals(KEYWORD))
		if (worldGenerators.get(world.getName()).equals(KEYWORD))
		{
			WorldServer serverWorld = ((CraftWorld) world).getHandle();
			this.replaceChunkGenerator(serverWorld);
		}
	}

	private Class<?> nmsClass(String simpleName) throws ClassNotFoundException {
		Class<?> exampleNmsClass = ChunkGenerator.class;
		String name = exampleNmsClass.getName().replace(exampleNmsClass.getSimpleName(), simpleName);
		return Class.forName(name);
	}

	private void replaceChunkGenerator(WorldServer worldServer)
	{
		//ChunkGeneratorBetaPlus injected = new ChunkGeneratorBetaPlus(worldServer, new WorldChunkManagerOverworldBeta(worldServer));
		ChunkGeneratorBetaPlus injected = new ChunkGeneratorBetaPlus(worldServer, new WorldChunkManagerBeta(worldServer));
		ChunkProviderServer chunkProvider = worldServer.getChunkProvider();

		try
		{
			Field chunkGeneratorField = ReflectionUtil.getFieldOfType(chunkProvider, ChunkGenerator.class);
			chunkGeneratorField.set(chunkProvider, injected);
			chunkGeneratorField = ReflectionUtil.getFieldOfType(chunkProvider.playerChunkMap, ChunkGenerator.class);
			chunkGeneratorField.set(chunkProvider.playerChunkMap, injected);

			try
			{
				Field chunkTaskSchedulerField = ReflectionUtil.getFieldOfType(chunkProvider, this.nmsClass("ChunkTaskScheduler"));
				Object scheduler = chunkTaskSchedulerField.get(chunkProvider);
				chunkGeneratorField = ReflectionUtil.getFieldOfType(scheduler, ChunkGenerator.class);
				chunkGeneratorField.set(scheduler, injected);
			}
			catch (ClassNotFoundException e)
			{
				;
			}

			this.injected = injected;
			// Why this?
			worldServer.generator = null;
		}
		catch (ReflectiveOperationException var8)
		{
			throw new RuntimeException("Failed to inject chunk generator", var8);
		}
	}

	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event)
	{
		this.worldGenerators.remove(event.getWorld().getName());
	}
}