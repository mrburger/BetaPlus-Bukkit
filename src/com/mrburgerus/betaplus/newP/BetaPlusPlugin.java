package com.mrburgerus.betaplus.newP;

import com.mrburgerus.betaplus.world.beta.beta_api.BetaPlusTerrainGenerator;
import com.mrburgerus.betaplus.world.beta.beta_new.WorldChunkManagerOverworldBeta;
import net.minecraft.server.v1_14_R1.WorldServer;
import nl.rutgerkok.worldgeneratorapi.BaseTerrainGenerator;
import nl.rutgerkok.worldgeneratorapi.WorldGenerator;
import nl.rutgerkok.worldgeneratorapi.WorldGeneratorApi;
import nl.rutgerkok.worldgeneratorapi.WorldRef;
import nl.rutgerkok.worldgeneratorapi.internal.PropertyRegistryImpl;
import nl.rutgerkok.worldgeneratorapi.internal.WorldGeneratorImpl;
import nl.rutgerkok.worldgeneratorapi.internal.bukkitoverrides.InjectedChunkGenerator;
import nl.rutgerkok.worldgeneratorapi.property.PropertyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BetaPlusPlugin extends JavaPlugin implements Listener
{
	// Fields
	public static Logger LOGGER;
	public static final int seaLevel = 63;
	public static final int seaDepth = 20;
	public static final double oceanYScale = 3.25;

	// New fields
	private final Map<UUID, WorldGenerator> worldGenerators = new HashMap<>();
	private final PropertyRegistry propertyRegistry = new PropertyRegistryImpl();
	private final Map<WorldRef, Consumer<WorldGenerator>> worldGeneratorModifiers = new HashMap<>();

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
		LOGGER.log(Level.INFO, "Using Beta+");

		return WorldGeneratorApi.getInstance(this, 0, 1)
				.createCustomGenerator(WorldRef.ofName(worldName), worldGenerator ->
				{
					try
					{
						World world = Bukkit.getWorld(worldName);

						BaseTerrainGenerator b = new BetaPlusTerrainGenerator(world);
						worldGenerator.setBaseTerrainGenerator(b);
						net.minecraft.server.v1_14_R1.World world1 = ((CraftWorld) world).getHandle();
						//InjectedChunkGenerator chunkGenerator = new InjectedChunkGenerator((WorldServer) world1, new WorldChunkManagerOverworldBeta(world1), b);
						((WorldGeneratorImpl) worldGenerator).injected = new InjectedChunkGeneratorBeta((WorldServer) world1, new WorldChunkManagerOverworldBeta(world1), b);

						BetaPlusPlugin.LOGGER.log(Level.INFO, ((WorldGeneratorImpl) worldGenerator).injected.toString());

						/*
						Field f = worldGenerator.getClass().getDeclaredField("injected");
						f.setAccessible(true);
						f.set(chunkGenerator, chunkGenerator);
						*/
						/*
						World world = Bukkit.getWorld(worldName);
						Class clazz = Class.forName("nl.rutgerkok.worldgeneratorapi.internal.WorldGeneratorImpl");
						Constructor<?> constructor = clazz.getDeclaredConstructor(World.class);
						constructor.setAccessible(true);
						WorldGeneratorImpl worldGen = (WorldGeneratorImpl) constructor.newInstance(world);
						net.minecraft.server.v1_14_R1.World world1 = ((CraftWorld) world).getHandle();
						InjectedChunkGenerator chunkGenerator = new InjectedChunkGenerator((WorldServer) world1, new WorldChunkManagerOverworldBeta(world1), new DummyBaseTerrainGenerator());
						//new InjectedChunkGenerator((WorldServer) world1, new DummyBaseTerrainGenerator());

						worldGen.injected = chunkGenerator;
						Field f = worldGen.getClass().getDeclaredField("injected");
						f.setAccessible(true);
						f.set(chunkGenerator, chunkGenerator);
						*/


					}
					catch (Exception e)
					{
						e.printStackTrace();
						throw new IllegalStateException("Could Not Create Beta+!")	;
					}
				});
	}

	/*
	@Override
	public ChunkGenerator createCustomGenerator(WorldRef worldRef, Consumer<WorldGenerator> consumer)
	{
		this.worldGeneratorModifiers.putIfAbsent(worldRef, consumer);
		return new DummyBukkitChunkGenerator(this);
	}

	@Override
	public Version getApiVersion()
	{
		return new VersionImpl(this.getDescription().getVersion());
	}

	@Override
	public WorldGenerator getForWorld(World world)
	{
		return (WorldGenerator)this.worldGenerators.computeIfAbsent(world.getUID(), (uuid) -> {
			try
			{
				Class clazz = Class.forName("nl.rutgerkok.worldgeneratorapi.internal.WorldGeneratorImpl");
				Constructor<?> constructor = clazz.getConstructor(World.class);
				// Implicit
				WorldGenerator worldGenerator = (WorldGenerator) constructor.newInstance(world);
				Consumer<WorldGenerator> worldGeneratorModifier = (Consumer)this.worldGeneratorModifiers.get(worldGenerator.getWorldRef());
				if (worldGeneratorModifier != null)
				{
					worldGeneratorModifier.accept(worldGenerator);

					try {
						worldGenerator.getBaseTerrainGenerator();
					} catch (UnsupportedOperationException var6) {
						throw new IllegalStateException("The custom world generator forgot to set a base chunk generator. If the custom world generator does not intend to replace the base terrain, it should modify the world using the WorldGeneratorInitEvent instead of using ");
					}
				}

				this.getServer().getPluginManager().callEvent(new WorldGeneratorInitEvent(worldGenerator));
				return worldGenerator;
			}
			catch (Exception e)
			{
				throw new IllegalStateException("Generic Errors BOYO");
			}
		});
	}

	@Override
	public PropertyRegistry getPropertyRegistry()
	{
		return this.propertyRegistry;
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		this.getForWorld(event.getWorld());
	}

	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event) {
		this.worldGenerators.remove(event.getWorld().getUID());
	}
	*/
}
