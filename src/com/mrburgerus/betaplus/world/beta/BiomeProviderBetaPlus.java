package com.mrburgerus.betaplus.world.beta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.world.beta.biome.BetaPlusBiomeSelector;
import com.mrburgerus.betaplus.world.beta.biome.EnumBetaPlusBiome;
import com.mrburgerus.betaplus.world.beta.sim.BetaPlusClimate;
import com.mrburgerus.betaplus.world.beta.sim.BetaPlusSimulator;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBiome;
import net.minecraft.server.v1_14_R1.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/* Creates Biome Values */
/* Oceans are not a part of Beta proper, so I'm injecting them */
public class BiomeProviderBetaPlus extends WorldChunkManager
{
	// Fields
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;
	private static final BiomeBase[] BIOMES_LIST = buildBiomesList();

	// New Fields
	private final double scaleVal;
	private final double mult;
	private static int chunkSize = 16;

	NoiseGeneratorOctavesBeta octaves12;
	NoiseGeneratorOctavesBeta octaves22;
	NoiseGeneratorOctavesBeta octaves32;
	NoiseGeneratorOctavesBeta octaves62;
	NoiseGeneratorOctavesBeta octaves72;
	private static double octaveArr42[];
	private static double octaveArr52[];
	private static double octaveArr12[];
	private static double octaveArr22[];
	private static double octaveArr32[];
	private long seedLong;
	private BetaPlusClimate climateSim;
	// Testing
	protected final Map<StructureGenerator<?>, Boolean> hasStructureCache = Maps.newHashMap();


	// The simulator for Y-heights.
	private BetaPlusSimulator simulator;

	public BiomeProviderBetaPlus(World world)
	{
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(world.getSeed() * 543321), 2);
		scaleVal = 60;
		mult = 2;

		/*
		octaves12 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves22 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves32 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 8);
		octaves62 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 10);
		octaves72 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		*/

		seedLong = world.getSeed();

		simulator = new BetaPlusSimulator(world);
		climateSim = new BetaPlusClimate(world, scaleVal, mult);
	}

	/* Builds Possible Biome List */
	private static BiomeBase[] buildBiomesList()
	{
		EnumBetaPlusBiome[] betaPlusBiomes = EnumBetaPlusBiome.defaultB.getDeclaringClass().getEnumConstants();
		Set<BiomeBase> biomeSet = Sets.newHashSet();
		for (int i = 0; i < betaPlusBiomes.length; i++)
		{
			biomeSet.add(betaPlusBiomes[i].handle);
		}
		return biomeSet.toArray(new BiomeBase[biomeSet.size()]);
	}

	public BiomeBase[] generateBiomes(int startX, int startZ, int xSize, int zSize)
	{
	BiomeBase[] biomeArr = new BiomeBase[xSize * zSize];
	temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
	humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
	noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
	int counter = 0;
	for (int x = 0; x < xSize; ++x)
	{

		for (int z = 0; z < zSize; ++z)
		{
			double var9 = noise[counter] * 1.1 + 0.5;
			double oneHundredth = 0.01;
			double point99 = 1.0 - oneHundredth;
			double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
			oneHundredth = 0.002;
			point99 = 1.0 - oneHundredth;
			double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
			temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
			temperatureVal = MathHelper.a(temperatureVal, 0.0, 1.0);
			humidityVal = MathHelper.a(humidityVal, 0.0, 1.0);
			temperatures[counter] = temperatureVal;
			humidities[counter] = humidityVal;
			biomeArr[counter] = EnumBetaPlusBiome.getBiomeFromLookup(temperatureVal, humidityVal);
			counter++;
		}
	}
	return biomeArr;
}

	/* Could be possible that a conversion Z,X to X,Z needed */
	private BiomeBase[] generateBiomesWithOceans(int startX, int startZ, int xSize, int zSize, final boolean useAverage)
	{
		BiomeBase[] biomeArr = new BiomeBase[xSize * zSize];
		temperatures = temperatureOctave.generateOctaves(temperatures, (double) startX, (double) startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humidities = humidityOctave.generateOctaves(humidities, (double) startX, (double) startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise = noiseOctave.generateOctaves(noise, (double) startX, (double) startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);
		int counter = 0;
		// Aren't these values WRONG? Like Beta Generates Z, X
		// ANSWER: NO! The implementation below is correct
		for (int x = 0; x < xSize; ++x)
		{

			for (int z = 0; z < zSize; ++z)
			{
				// No, x + startX, z + startZ MUST BE USED, by the looks of it
				// Previously, I made an oops.
				BlockPosition pos = new BlockPosition(x + startX, 0 ,z + startZ);
				double var9 = noise[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
				temperatureVal = MathHelper.a(temperatureVal, 0.0, 1.0);
				humidityVal = MathHelper.a(humidityVal, 0.0, 1.0);
				temperatures[counter] = temperatureVal;
				humidities[counter] = humidityVal;
				biomeArr[counter] = EnumBetaPlusBiome.getBiomeFromLookup(temperatureVal, humidityVal);
				/* Add Oceans by Simulation */
				if (useAverage) //useAverage only TRUE if we're searching in a square pattern, such as spawn, Ocean Monuments, or Mansions.
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(pos);
					// Tried 56, 58, 57
					if (avg.getFirst() < BetaPlus.seaLevel - 5) // Usually 58
					{
						// Inversion was the intent, so false is supposed to be "all values below sea level"
						if (!avg.getSecond())
						{
							biomeArr[counter] = this.getOceanBiome(pos, true);
						}
						else
						{
							biomeArr[counter] = this.getOceanBiome(pos, false);
						}
					}
					// Commented out due to overhead. this is only used for spawn, and it isn't worth it.
					/*
					else if (avg.getFirst() >= settings.getSeaLevel() - 2 && avg.getFirst() <= settings.getSeaLevel())
					{
						// Now, add Beaches
						if (simulator.isBlockSandSim(pos))
						{
							biomeArr[counter] = this.getBeachBiome(pos);
						}
					}
					*/

				}
				else // Called when searching for "small" structures
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(pos);
					if (avg.getFirst() < BetaPlus.seaLevel - 1) // 62 usually
					{
						biomeArr[counter] = this.getOceanBiome(pos, false);
					}
					else if (avg.getFirst() >= BetaPlus.seaLevel - 2 && avg.getFirst() <= BetaPlus.seaLevel + 1)
					{
						/* Now, add Beaches */
						if (simulator.isBlockSandSim(pos))
						{
							biomeArr[counter] = this.getBeachBiome(pos);
						}
					}
				}

				/* Finally, increment counter! */
				counter++;
			}
		}
		return biomeArr;
	}


	public BiomeBase[] getBiomes(int x, int z, int width, int depth)
	{
		return a(x, z, width, depth, true);
	}

	@Override
	public BiomeBase[] a(int x, int z, int width, int length, boolean cacheFlag)
	{
		//Modified from simple generateBiomes to test how this assigns Biomes
		return generateBiomes(x, z, width, length);
	}

	@Override
	public Set<BiomeBase> a(int centerX, int centerZ, int sideLength)
	{
		// Like centerX - 4 if sideLength=16
		// like centerX - 7 if sidelength =29
		// AHH! 7,4 is the number I have seen as an error
		int startX = centerX - sideLength >> 2;
		int startZ = centerZ - sideLength >> 2;
		// probably end-X coordinate
		int endX = centerX + sideLength >> 2;
		int endZ = centerZ + sideLength >> 2;
		int xSize = endX - startX + 1;
		int zSize = endZ - startZ + 1;
		Set<BiomeBase> set = Sets.newHashSet();
		// Debug
		/*
		BetaPlus.LOGGER.info("Square: " + xSize + ", " + zSize + " ; " + sideLength);
		BetaPlus.LOGGER.info("Bounds: " + "[" + startX + "," + startZ + "]" + "x" + "[" +
				(startX + sideLength) + "," + (startZ + sideLength) + "]");
				*/
		// If there exists the Biome in question it does NOT care.
		// Test this with various Combos:
		// startX, startZ, xSize, zSize -> Weird Locations, not correct.
		// startX, startZ, sideLength, sideLength -> Same result as above
		// xSize, zSize, sideLength, sideLength -> Loops infinitely
		// endX, endZ, sideLength, sideLength -> Same result as first.
		// centerX, centerZ, sideLength, sideLength -> Working???
		Collections.addAll(set, this.generateBiomesWithOceans(centerX, centerZ, sideLength, sideLength, true));
		return set;
	}

	@Override
	public BlockPosition a(int x, int z, int range, List<BiomeBase> biomeList, Random random)
	{
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int xSize = k - i + 1;
		int zSize = l - j + 1;
		BiomeBase[] biomeArr = this.generateBiomes(i, j, xSize, zSize);

		BlockPosition blockpos = null;
		int k1 = 0;

		for(int counter = 0; counter < xSize * zSize; ++counter) {
			int i2 = i + counter % xSize << 2;
			int j2 = j + counter / xSize << 2;
			if (biomeList.contains(biomeArr[counter]))
			{
				if (blockpos == null || random.nextInt(k1 + 1) == 0) {
					blockpos = new BlockPosition(i2, 0, j2);
				}

				++k1;
			}
		}
		return blockpos;
	}

	@Override
	/* Copied from OverworldBiomeProvider.class */
	public boolean a(StructureGenerator<?> structure)
	{
		return this.hasStructureCache.computeIfAbsent(structure, (param1) -> {
			for (BiomeBase biome : BIOMES_LIST) // Go through list of declared Biomes
			{
				if (biome.a(param1))
				{
					return true;
				}
			}

			return false;
		});
	}

	/* Provides Ocean Biomes appropriate to temperature */
	public BiomeBase getOceanBiome(BlockPosition pos, boolean isDeep)
	{
		double[] climate = climateSim.getClimateValuesatPos(pos);
		double temperature = climate[0];
		//return EnumBetaPlusBiome.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusBiomeSelector.FROZEN_VALUE)
		{
			if(isDeep)
			{
				return Biomes.DEEP_FROZEN_OCEAN;
			}
			return Biomes.FROZEN_OCEAN;
		}
		else if (temperature > BetaPlusBiomeSelector.VERY_HOT_VAL && climate[1] >= 0.735) //Was 0.725
		{
			return Biomes.WARM_OCEAN;
		}
		else if (temperature > BetaPlusBiomeSelector.WARM_VAL)
		{
			if(isDeep)
			{
				return Biomes.DEEP_LUKEWARM_OCEAN;
			}
			return Biomes.LUKEWARM_OCEAN;

		}
		else
		{
			if(isDeep)
			{
				return Biomes.DEEP_COLD_OCEAN;
			}
			return Biomes.COLD_OCEAN;
		}
	}

	public BiomeBase getBeachBiome(BlockPosition pos)
	{
		double[] climate = climateSim.getClimateValuesatPos(pos);
		if (climate[0] < BetaPlusBiomeSelector.FROZEN_VALUE)
		{
			return Biomes.SNOWY_BEACH;
		}
		return Biomes.BEACH;
	}

	@Override
	public BiomeBase getBiome(int i, int i1)
	{
		return null;
	}

	@Override
	public Set<IBlockData> b()
	{
		//TODO
		return null;
	}
}
