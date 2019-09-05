package com.mrburgerus.betaplus.world.beta.beta_api;

import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlusPlugin;
import com.mrburgerus.betaplus.world.beta.select.AbstractBiomeSelector;
import com.mrburgerus.betaplus.world.beta.select.BetaPlusBiomeSelector;
import com.mrburgerus.betaplus.world.beta.sim.BetaPlusSimulator;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBiome;
import com.mrburgerus.betaplus.world.noise.PerlinNoise;
import com.mrburgerus.betaplus.world.noise.VoronoiNoiseGenerator;
import com.mrburgerus.betaplus.util.TerrainType;
import net.minecraft.server.v1_14_R1.*;
import nl.rutgerkok.worldgeneratorapi.BiomeGenerator;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;

import java.util.Random;
import java.util.logging.Level;

import static com.mrburgerus.betaplus.world.beta.beta_api.BetaPlusTerrainGenerator.CHUNK_SIZE;

public class BetaPlusBiomeGenerator implements BiomeGenerator
{
	// FIELDS //

	// Biome List, possibly a good injection point for Biomes O' Plenty stuff.
	private static final BiomeBase[] BIOMES_LIST = buildBiomesList();
	// Biome Layer? New for 1.14, testing now.
	//private final Layer biomeLayer; // Could end up disabled.
	// The simulator for Y-heights.
	public final BetaPlusSimulator simulator;
	// Voronoi Cell Generator.
	private VoronoiNoiseGenerator voronoi;
	// Perlin Generator
	private PerlinNoise biomeNoise;

	// Voronoi Cell offset
	// Too Big?: 1024
	// Too Small?: 512, 768
	private double offsetVoronoi = 1024; //420.69; // HE HE.
	// Biome Selector object
	private AbstractBiomeSelector selector;

	// Required for legacy operations
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	public final double scaleVal;
	public final double mult;

	public BetaPlusBiomeGenerator(long seed)
	{
		scaleVal = (1.0D / 39.999999404);
		mult = 2;
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 543321), 2);
		simulator = new BetaPlusSimulator(seed, this);
		selector = new BetaPlusBiomeSelector();

		voronoi = new VoronoiNoiseGenerator(seed, (short) 0);
		biomeNoise = new PerlinNoise(seed);
	}

	private static BiomeBase[] buildBiomesList()
	{
		// Possibly a for-each would be better.
		// Initialize all enabled Biome.
		// Check for Biomes o' Plenty, and if so, get the enabled biome list.
		// Determines which structures are allowed

		// I copied this from Overworld. Deal with it
		return new BiomeBase[]{Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU};

	}

	public Biome getBiome(int x, int z)
	{
		// Get for a specific X and Z. This is typically used for structures, such as Buried Treasure
		//return getBiomes(x, z, 1, 1, false)[0];
		return this.generateBiomes(x, z, 1, 1, false)[0];
	}

	public Biome[] getBiomes(int x, int z, int xSize, int zSize, boolean cacheFlag)
	{
		return this.generateBiomes(x, z, xSize, zSize, false);
	}


	private Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize, final boolean useAverage)
	{
		// Required for legacy operations
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);

		double vNoise;
		double offsetX;
		double offsetZ;
		double noiseVal;
		Biome[] biomeArr = new Biome[xSize * zSize];
		int counter = 0;
		BiomeBase selected;
		// First, get initial terrain and simulate sand
		Pair<BlockPosition, TerrainType>[][] pairArr = this.getInitialTerrain(startX, startZ, xSize, zSize);
		// Moved up here.

		// THIS WILL NOT WORK WITHOUT ADAPTATION. IT NEEDS MORE SAMPLES OF SURROUNDING AREAS
		TerrainType[][] terrainTypes = TerrainType.processTerrain(pairArr);

		// Process
		for (int x = 0; x < xSize; ++x)
		{
			for (int z = 0; z < zSize; ++z)
			{
				// REQUIRED
				double var9 = noise[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
				// Replaced clamp with a
				temperatureVal = MathHelper.a(temperatureVal, 0.0, 1.0);
				humidityVal = MathHelper.a(humidityVal, 0.0, 1.0);
				temperatures[counter] = temperatureVal;
				humidities[counter] = humidityVal;

				// Begin New Declarations
				BlockPosition pos = new BlockPosition(x + startX, 0, z + startZ);

				// Frequency is 1, Amplitude is halved and then offset for absolute.
				//vNoise = (voronoi.noise((x + startX + offsetVoronoi) / offsetVoronoi, (z + startZ + offsetVoronoi) / offsetVoronoi, 1) * 0.5) + 0.5;
				//vNoise = voronoi.noise2((float) (startX + x / scaleVal), (float) (startZ + z / scaleVal));
				//double noiseVal = MathHelper.clamp(vNoise, 0.0, 0.99999999999999);

				// An 0.3 throwback.
				offsetX = biomeNoise.noise2((float) ((startX + x) / scaleVal), (float) ((startZ + z) / scaleVal)) * 80 + biomeNoise.noise2((startX + x) / 7, (startZ + z) / 7) * 20;
				offsetZ = biomeNoise.noise2((float) ((startX + x) / scaleVal), (float) ((startZ + z) / scaleVal)) * 80 + biomeNoise.noise2((startX + x - 1000) / 7, (startX + x) / 7) * 20;
				vNoise = (voronoi.noise((startX + x + offsetX + offsetVoronoi) / offsetVoronoi, (startZ + z - offsetZ) / offsetVoronoi, 1) * 0.5f) + 0.5f;
				noiseVal = (voronoi.noise((startX + x + offsetX + 2000) / 180, (startZ + z - offsetZ) / 180, 1) * 0.5) + 0.5;
				noiseVal = MathHelper.a(noiseVal, 0, 0.9999999);


				// If Average used, we only cared about a very top-level view, and will operate as such.
				// Typically used for Ocean Monuments
				if (useAverage)
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(pos);

					// OCEAN MONUMENT CATCHER
					if (avg.getFirst() < BetaPlusPlugin.seaLevel - 1)
					{
						if (avg.getFirst() < MathHelper.floor(BetaPlusPlugin.seaLevel - (BetaPlusPlugin.seaDepth / BetaPlusPlugin.oceanYScale)))
						{
							// Overwrite.
							terrainTypes[x][z] = TerrainType.deepSea;
						}
						else
						{
							terrainTypes[x][z] = TerrainType.sea;
						}
					}
				}
				selected = selector.getBiome(temperatureVal, humidityVal, noiseVal, terrainTypes[x][z]);


				biomeArr[counter] = CraftBlock.biomeBaseToBiome(selected);
				counter++;
			}
		}
		return biomeArr;
	}

	// Since Layers will most likely not work in any capacity, I will fall back on the land simulator I developed.
	// Also should add beaches.
	public Pair<BlockPosition, TerrainType>[][] getInitialTerrain(int startX, int startZ, int xSize, int zSize)
	{
		// There will be issues detecting large islands. I may run into chunk runaway issues if I don't recheck my running block tally.
		// Also, I may have to expand search area on the fly to accomodate. Probably not, hopefully.
		// Possibly a BlockPosition, TerrainType Pair would be good?


		// Get chunk positions necessary, Added Ceiling to round up. Must also be a double.
		int xChunkSize = MathHelper.f(xSize / (CHUNK_SIZE * 1.0D));
		int zChunkSize = MathHelper.f(zSize / (CHUNK_SIZE * 1.0D));
		Pair<BlockPosition, TerrainType>[][] terrainPairs = new Pair[xChunkSize * CHUNK_SIZE][zChunkSize * CHUNK_SIZE];

		for (int xChunk = 0; xChunk < xChunkSize; xChunk++)
		{
			for (int zChunk = 0; zChunk < zChunkSize; zChunk++)
			{
				// Looks to be incorrect.
				//ChunkPos chunkPos = newP ChunkPos(startX + (xChunk * CHUNK_SIZE), startZ + (zChunk * CHUNK_SIZE));
				ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(new BlockPosition(startX + (xChunk * CHUNK_SIZE), 0, startZ + (zChunk * CHUNK_SIZE)));

				// Get simulated chunk
				int[][] yVals = simulator.simulateChunkYFull(chunkPos).getFirst();


				// Enter into initial Terrain list
				for (int x = 0; x < CHUNK_SIZE; x++)
				{
					for (int z = 0; z < CHUNK_SIZE; z++)
					{
						// Block Position in world
						BlockPosition pos = new BlockPosition(x + chunkPos.d(), 0 ,z + chunkPos.e());
						if ((simulator.isBlockBeach(pos))
								&& yVals[x][z] <= BetaPlusPlugin.seaLevel + 1 && yVals[x][z] >= BetaPlusPlugin.seaLevel - 1)
						{
							terrainPairs[x + xChunk * CHUNK_SIZE][z + zChunk * CHUNK_SIZE] = Pair.of(pos, TerrainType.coastal);
						}
						else
						{
							terrainPairs[x + xChunk * CHUNK_SIZE][z + zChunk * CHUNK_SIZE] = Pair.of(pos, TerrainType.getTerrainNoIsland(yVals, x, z));
						}
					}
				}
			}
		}
		// Now, find isolated "Land" spots and declare as islands
		// Also find "Land" Spots surrounded with "Hilly" and fill with hills.
		return terrainPairs;
	}

	@Override
	public Biome[] getBiomes(int minX, int minZ, int xSize, int zSize)
	{
		BetaPlusPlugin.LOGGER.log(Level.ALL, "GENERATE");
		int zoomedOutXSize = xSize / 4;
		int zoomedOutZSize = zSize / 4;
		Biome[] zoomedOut = this.getZoomedOutBiomes(minX / 4, minZ / 4, zoomedOutXSize, zoomedOutZSize);
		Biome[] normalScale = new Biome[xSize * zSize];

		for(int i = 0; i < normalScale.length; ++i) {
			int x = i % xSize;
			int z = i / xSize;
			normalScale[i] = zoomedOut[z / 4 * zoomedOutZSize + x / 4];
		}

		return normalScale;
	}

	@Override
	public Biome[] getZoomedOutBiomes(int startX, int startZ, int xSize, int zSize)
	{
		return this.generateBiomes(startX, startZ, xSize, zSize, true);

	}
}
