package com.mrburgerus.betaplus.world.beta.select;


import com.mojang.datafixers.util.Pair;
import com.mrburgerus.betaplus.util.TerrainType;
import net.minecraft.server.v1_15_R1.BiomeBase;
import net.minecraft.server.v1_15_R1.Biomes;

import java.util.Optional;

// Yet another BiomeBase Selector, does some cool injection of modern biomes.

// BiomeDictionary.class?


// NOTES
// Temperature is from -0.5 (SNOWY TAIGA) to 2.0 (DESERT / MESA) in vanilla
// Range 2.5
// [0, 1) to [-0.5, 2.0]


public class BetaPlusBiomeNew extends AbstractBiomeSelector
{
	// Fields
	private static BiomeBase defaultBiome = Biomes.PLAINS;
	private static final double TEMP_RANGE = 2.5; // Temperature range of all Biomes

	public BetaPlusBiomeNew()
	{
		// Spawn on sand, or coastal Biomes. HOPEFULLY IT WORKS
		// Causes Chunk issues....
		//super(Lists.asList(Biomes.DESERT, Support.coastBiomes.toArray(new BiomeBase[0])));
		super(Support.coastBiomes);
	}

	@Override
	public BiomeBase getBiome(double temperature, double humidity, double ran, TerrainType terrainType)
	{


		BiomeBase select;
		switch (terrainType)
		{
			case land:
				select = getLandBiome(temperature, humidity, ran).getFirst();
				break;
			case hillyLand:
				select = getHillyBiome(temperature, humidity, ran, getLandBiome(temperature, humidity, ran).getSecond());
				break;
			case mountains:
				select = Support.getBiomeFromParams(temperature, humidity, ran, Support.mountainBiomes);
				break;
			case sea:
				select = getOceanBiome(temperature, humidity, ran, false);
				break;
			case deepSea:
				select = getOceanBiome(temperature, humidity, ran, true);
				break;
			case coastal:
				select = Support.getBiomeFromParams(temperature, humidity, ran, Support.coastBiomes);
				break;
			case island:
				select = Support.getBiomeFromParams(temperature, humidity, ran, Support.islandBiomes);
				break;
			case generic:
				select = defaultBiome;
				break;
			default:
				select = defaultBiome;
		}
		return select;
	}

	// HELPERS

	private static Pair<BiomeBase, Optional<BiomeBase>> getLandBiome(double temperature, double humidity, double selectNoise)
	{
		return Support.getLandBiomeFromParams(temperature, humidity, selectNoise, Support.landBiomes);
	}

	private static BiomeBase getHillyBiome(double temperature, double humidity, double selectNoise, Optional<BiomeBase> hillBiome)
	{
		BiomeBase select;
		select = hillBiome.orElseGet(() -> Support.getBiomeFromParams(temperature, humidity, selectNoise, Support.mountainBiomes));
		return select;
	}

	private static BiomeBase getOceanBiome(double temperature, double humidity, double selectNoise, boolean isDeep)
	{
		if (isDeep)
		{
			return Support.getOceanBiomePair(temperature, humidity, selectNoise, Support.oceanBiomes).getSecond();
		}
		return Support.getOceanBiomePair(temperature, humidity, selectNoise, Support.oceanBiomes).getFirst();
	}

}
