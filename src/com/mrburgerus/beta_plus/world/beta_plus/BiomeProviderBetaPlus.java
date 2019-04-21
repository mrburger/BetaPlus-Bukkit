package com.mrburgerus.beta_plus.world.beta_plus;

import com.mrburgerus.beta_plus.util.MathHelper;
import com.mrburgerus.beta_plus.world.beta_plus.sim.BetaPlusClimate;
import com.mrburgerus.beta_plus.world.beta_plus.sim.BetaPlusSimulator;
import com.mrburgerus.beta_plus.world.biome.BetaPlusBiomeSelector;
import com.mrburgerus.beta_plus.world.biome.EnumBetaPlusBiome;
import com.mrburgerus.beta_plus.world.noise.NoiseGeneratorOctavesBeta;
import com.mrburgerus.beta_plus.world.noise.NoiseGeneratorOctavesBiome;
import com.sun.tools.javac.util.Pair;
import org.bukkit.block.Biome;

import java.util.Random;

import static com.sun.javafx.util.Utils.clamp;

/* Creates Biome Values */
/* Oceans are not a part of Beta proper, so I'm injecting them */
public class BiomeProviderBetaPlus
{
	// Fields
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;
	public double[] temperatures;
	public double[] humidities;
	public double[] noise;

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

	// The simulator for Y-heights.
	private BetaPlusSimulator simulator;

	public BiomeProviderBetaPlus(long seedLong)
	{
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(seedLong * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(seedLong * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(seedLong * 543321), 2);
		scaleVal = 0.015;
		mult = 1.75;

		/*
		octaves12 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves22 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		octaves32 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 8);
		octaves62 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 10);
		octaves72 = new NoiseGeneratorOctavesBeta(new Random(world.getSeed()), 16);
		*/

		simulator = new BetaPlusSimulator(seedLong);
		climateSim = new BetaPlusClimate(seedLong, scaleVal, mult);
	}

	public Biome[] generateBiome(int startX, int startZ, int xSize, int zSize)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
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
				temperatureVal = clamp(temperatureVal, 0.0, 1.0);
				humidityVal = clamp(humidityVal, 0.0, 1.0);
				temperatures[counter] = temperatureVal;
				humidities[counter] = humidityVal;
				biomeArr[counter] = EnumBetaPlusBiome.getBiomeFromLookup(temperatureVal, humidityVal);
				counter++;
			}
		}
		return biomeArr;
	}

	/* Could be possible that a conversion Z,X to X,Z needed */
	private Biome[] generateBiomeWithOceans(int startX, int startZ, int xSize, int zSize, final boolean useAverage)
	{
		Biome[] biomeArr = new Biome[xSize * zSize];
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
				double var9 = noise[counter] * 1.1 + 0.5;
				double oneHundredth = 0.01;
				double point99 = 1.0 - oneHundredth;
				double temperatureVal = (temperatures[counter] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
				oneHundredth = 0.002;
				point99 = 1.0 - oneHundredth;
				double humidityVal = (humidities[counter] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
				temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
				temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
				humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
				temperatures[counter] = temperatureVal;
				humidities[counter] = humidityVal;
				biomeArr[counter] = EnumBetaPlusBiome.getBiomeFromLookup(temperatureVal, humidityVal);
				/* Add Oceans by Simulation */
				if (useAverage) //useAverage only TRUE if we're searching in a square pattern, such as spawn, Ocean Monuments, or Mansions.
				{
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(x + startX, z + startZ);
					// Tried 56, 58, 57
					if (avg.fst < 64 - 5) // Usually 58
					{
						// Inversion was the intent, so false is supposed to be "all values below sea level"
						if (!avg.snd)
						{
							biomeArr[counter] = this.getOceanBiome(x + startX, z + startZ, true);
						}
						else
						{
							biomeArr[counter] = this.getOceanBiome(x + startX, z + startZ, false);
						}
					}
					// Commented out due to overhead. this is only used for spawn, and it isn't worth it.
					/*
					else if (avg.getFirst() >= settings.getSeaLevel() - 2 && avg.getFirst() <= settings.getSeaLevel())
					{
						// Now, add Beaches
						if (simulator.isMaterialandSim(pos))
						{
							biomeArr[counter] = this.getBeachBiome(pos);
						}
					}
					*/

				}
				else // Called when searching for "small" structures
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(x + startX, z + startZ);
					if (avg.fst < 64 - 1) // 62 usually
					{
						biomeArr[counter] = this.getOceanBiome(x + startX, z + startZ, false);
					}
					else if (avg.fst >= 64 - 2 && avg.fst <= 64 + 1)
					{
						/* Now, add Beaches */
						if (simulator.isMaterialandSim(x + startX, z + startZ))
						{
							biomeArr[counter] = this.getBeachBiome(x + startX, z + startZ);
						}
					}
				}

				/* Finally, increment counter! */
				counter++;
			}
		}
		return biomeArr;
	}


	//BEGIN OVERRIDES
	public Biome[] getBiome(int x, int z, int width, int depth)
	{
		return getBiome(x, z, width, depth, true);
	}

	public Biome[] getBiome(int x, int z, int width, int length, boolean cacheFlag)
	{
		//Modified from simple generateBiome to test how this assigns Biome
		return generateBiome(x, z, width, length);
	}

	/* Provides Ocean Biome appropriate to temperature */
	public Biome getOceanBiome(int xPos, int zPos, boolean isDeep)
	{
		double[] climate = climateSim.getClimateValuesatPos(xPos, zPos);
		double temperature = climate[0];
		//return EnumBetaPlusBiome.getBiomeFromLookup(temperature, climate[1]);
		if (temperature < BetaPlusBiomeSelector.FROZEN_VALUE)
		{
			if(isDeep)
			{
				return Biome.DEEP_FROZEN_OCEAN;
			}
			return Biome.FROZEN_OCEAN;
		}
		else if (temperature > BetaPlusBiomeSelector.VERY_HOT_VAL && climate[1] >= 0.735) //Was 0.725
		{
			return Biome.WARM_OCEAN;
		}
		else if (temperature > BetaPlusBiomeSelector.WARM_VAL)
		{
			if(isDeep)
			{
				return Biome.DEEP_LUKEWARM_OCEAN;
			}
			return Biome.LUKEWARM_OCEAN;

		}
		else
		{
			if(isDeep)
			{
				return Biome.DEEP_COLD_OCEAN;
			}
			return Biome.COLD_OCEAN;
		}
	}

	public Biome getBeachBiome(int xPos, int zPos)
	{
		double[] climate = climateSim.getClimateValuesatPos(xPos, zPos);
		if (climate[0] < BetaPlusBiomeSelector.FROZEN_VALUE)
		{
			return Biome.SNOWY_BEACH;
		}
		return Biome.BEACH;
	}


}
