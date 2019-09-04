package com.mrburgerus.betaplus;

import com.mrburgerus.betaplus.world.beta.beta_api.BetaPlusTerrainGenerator;
import com.mrburgerus.betaplus.world.beta.beta_api.WorldGeneratorBetaPlus;
import com.mrburgerus.betaplus.world.bukkit.DummyBukkitChunkGenerator;
import nl.rutgerkok.worldgeneratorapi.Version;
import nl.rutgerkok.worldgeneratorapi.WorldGenerator;
import nl.rutgerkok.worldgeneratorapi.WorldGeneratorApi;
import nl.rutgerkok.worldgeneratorapi.WorldRef;
import nl.rutgerkok.worldgeneratorapi.event.WorldGeneratorInitEvent;
import nl.rutgerkok.worldgeneratorapi.internal.PropertyRegistryImpl;
import nl.rutgerkok.worldgeneratorapi.internal.VersionImpl;
import nl.rutgerkok.worldgeneratorapi.property.PropertyRegistry;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;


// TODO
// Inject both the Biome Provider and Chunk Generator into the world.



public class BetaPlusPlugin extends JavaPlugin implements WorldGeneratorApi, Listener
{
	// Fields
	public static Logger LOGGER;
	public static final int seaLevel = 63;
	public static final int seaDepth = 20;
	public static final double oceanYScale = 3.25;

	// Copied
	public final Map<WorldRef, Consumer<WorldGenerator>> worldGeneratorModifiers = new HashMap();
	private final Map<UUID, WorldGeneratorBetaPlus> worldGenerators = new HashMap();
	private final PropertyRegistry propertyRegistry = new PropertyRegistryImpl();



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
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		//return new BetaChunkGenerator(this);

		return WorldGeneratorBetaPlus.getInstance(this, 0, 3).createCustomGenerator(WorldRef.ofName(worldName), generator ->
			{
				generator.setBaseTerrainGenerator(new BetaPlusTerrainGenerator(generator.getWorld()));
			});
	}

	public Version getApiVersion() {
		return new VersionImpl(this.getDescription().getVersion());
	}

	public ChunkGenerator createCustomGenerator(WorldRef world, Consumer<WorldGenerator> consumer) {
		this.worldGeneratorModifiers.putIfAbsent(world, consumer);
		//return new DummyBukkitChunkGenerator(WorldGeneratorBetaPlus.getInstance(this, 0, 3));
		return new DummyBukkitChunkGenerator(this);
	}

	public WorldGenerator getForWorld(World world) {
		return (WorldGenerator)this.worldGenerators.computeIfAbsent(world.getUID(), (uuid) -> {
			WorldGeneratorBetaPlus worldGenerator = new WorldGeneratorBetaPlus(world);
			Consumer<WorldGenerator> worldGeneratorModifier = (Consumer)this.worldGeneratorModifiers.get(worldGenerator.getWorldRef());
			if (worldGeneratorModifier != null) {
				worldGeneratorModifier.accept(worldGenerator);

				try {
					worldGenerator.getBaseChunkGenerator();
				} catch (UnsupportedOperationException var6) {
					throw new IllegalStateException("The custom world generator forgot to set a base chunk generator. If the custom world generator does not intend to replace the base terrain, it should modify the world using the WorldGeneratorInitEvent instead of using ");
				}
			}

			this.getServer().getPluginManager().callEvent(new WorldGeneratorInitEvent(worldGenerator));
			return worldGenerator;
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
}
