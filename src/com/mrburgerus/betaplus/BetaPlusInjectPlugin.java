package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.world.beta.beta_new.ChunkGeneratorBetaPlus;
import com.mrburgerus.betaplus.world.beta.beta_new.WorldChunkManagerOverworldBeta;
import com.mrburgerus.betaplus.world.bukkit.DummyBukkitChunkGeneratorWCM;
import net.minecraft.server.v1_14_R1.ChunkGenerator;
import net.minecraft.server.v1_14_R1.ChunkProviderServer;
import net.minecraft.server.v1_14_R1.WorldChunkManager;
import net.minecraft.server.v1_14_R1.WorldServer;
import nl.rutgerkok.worldgeneratorapi.internal.ReflectionUtil;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BetaPlusInjectPlugin extends JavaPlugin implements Listener
{
	public static Logger LOGGER;
	public static final int seaLevel = 63;
	public static final int seaDepth = 20;
	public static final double oceanYScale = 3.25;
	private ChunkGeneratorBetaPlus injected;

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
		return new DummyBukkitChunkGeneratorWCM(this);
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event)
	{
		this.getForWorld(event.getWorld());
	}


	// ALWAYS REPLACES. THAT IS BAD.
	public void getForWorld(World world)
	{
		WorldServer serverWorld = ((CraftWorld) world).getHandle();
		this.replaceChunkGenerator(serverWorld);
	}

	private Class<?> nmsClass(String simpleName) throws ClassNotFoundException {
		Class<?> exampleNmsClass = ChunkGenerator.class;
		String name = exampleNmsClass.getName().replace(exampleNmsClass.getSimpleName(), simpleName);
		return Class.forName(name);
	}

	private void replaceChunkGenerator(WorldServer worldServer)
	{

		ChunkGeneratorBetaPlus injected = new ChunkGeneratorBetaPlus(worldServer, new WorldChunkManagerOverworldBeta(worldServer));
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
			LOGGER.log(Level.INFO, "Finished replace");
		}
		catch (ReflectiveOperationException var8)
		{
			throw new RuntimeException("Failed to inject chunk generator", var8);
		}
	}

	private void replaceBiomeProvider2(WorldServer worldServer)
	{
		WorldChunkManagerOverworldBeta wcm = injected.biomeProviderS;
		ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
		try
		{
			Field chunkGeneratorField = ReflectionUtil.getFieldOfType(chunkProvider, ChunkGenerator.class);
			ChunkGeneratorBetaPlus chunkGenerator = (ChunkGeneratorBetaPlus) chunkGeneratorField.get(chunkProvider); //was wcm
			//LOGGER.log(Level.INFO, "TEST : " + chunkGenerator.toString());
			// Testing
			chunkGenerator.biomeProviderS = wcm;
			//LOGGER.log(Level.INFO, "TEST2 : " + wcm);
			//LOGGER.log(Level.INFO, "TEST3 : " + chunkGenerator.biomeProviderS);


			//chunkGeneratorField.set(wcm, wcm);
			//Field biomeField = ReflectionUtil.getFieldOfType(chunkGeneratorField)

			//LOGGER.log(Level.INFO, "Success! Injected (supposedly)");

		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to inject biome generator", e);

		}
	}

	// Added, TESTING (EHHHH NOT WORKING)
	private void replaceBiomeProvider(WorldServer worldServer)
	{
		if (injected != null)
		{
			WorldChunkManager wcm = injected.biomeProviderS;
			ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
			try
			{
				//ChunkProviderServer chunkProvider = worldServer.getChunkProvider();
				// WATCH OUT

				//worldServer.generator is NULL!
				LOGGER.log(Level.INFO, injected.biomeProviderS.toString());

				// Not ChunkGenerator.class, or worldServer.generator
				//Field biomeProviderField = ReflectionUtil.getFieldOfType(injected, WorldChunkManager.class);
				//Field biomeProviderField = ReflectionUtil.getFieldOfType(injected.biomeProviderS, ChunkGenerator.class);
				//Field biomeProviderField = ReflectionUtil.getFieldOfType(ChunkGenerator.class, wcm.getClass());
				Field biomeProviderField = ReflectionUtil.getFieldOfType(ChunkGenerator.class, WorldChunkManager.class);
				biomeProviderField.setAccessible(true);

				// set = TYPE of variable, set to variable.


				//biomeProviderField.set(wcm, injected.biomeProviderS);
				//biomeProviderField.set(wcm, wcm);
				//biomeProviderField.set(biomeProviderField.getType(), wcm);
				//biomeProviderField.set(worldServer.getChunkProvider().chunkGenerator.getWorldChunkManager(), wcm);
				//biomeProviderField.set(chunkProvider.chunkGenerator.getWorldChunkManager(), wcm);
				//biomeProviderField.set(WorldChunkManager, wcm);
				//biomeProviderField.set(newP WorldChunkManagerBetaPlus(worldServer.getSeed()), wcm);

				// was wcm.
				//final Object oldValue = biomeProviderField.get(Class.forName("WorldChunkManager"));

				// Gets the object in type inside parentheses.
				final Object oldValue = (WorldChunkManager) chunkProvider.chunkGenerator.getWorldChunkManager();
						//biomeProviderField.get(newP WorldChunkManagerBetaPlus(worldServer.getSeed()))
						// biomeProviderField.get(newP Object())
						//biomeProviderField.getType()
						;
				biomeProviderField.set(oldValue, wcm);

				LOGGER.log(Level.INFO, "Success! Injected (supposedly)");
			}
			catch (Exception e)
			{
				throw new RuntimeException("Failed to inject biome generator", e);
			}
		}
	}


}
