package com.mrburgerus.betaplus.world.beta.select;

import com.google.common.collect.Lists;
import com.mrburgerus.betaplus.world.util.TerrainType;
import net.minecraft.server.v1_14_R1.BiomeBase;
import net.minecraft.server.v1_14_R1.Biomes;
import org.bukkit.block.Biome;

// Ripped straight from Beta, minimally processed.
// Should Provide the "Closest" Experience
public class BetaPlusBiomeSelector extends AbstractBiomeSelector
{
	// Mappings //
	private static final BiomeBase tundra = Biomes.SNOWY_TUNDRA;
	private static final BiomeBase savanna = Biomes.SAVANNA;
	private static final BiomeBase desert = Biomes.DESERT;
	private static final BiomeBase swampland = Biomes.SWAMP;
	private static final BiomeBase taiga = Biomes.SNOWY_TAIGA;
	private static final BiomeBase shrubland = Biomes.SAVANNA;
	private static final BiomeBase plains = Biomes.PLAINS;
	private static final BiomeBase seasonalForest = Biomes.BIRCH_FOREST;
	private static final BiomeBase rainforest = Biomes.FLOWER_FOREST;
	private static final BiomeBase forest = Biomes.FOREST;
	// Hill declarations
	private static final BiomeBase tundraHills = Biomes.SNOWY_MOUNTAINS;
	private static final BiomeBase savannaHills = Biomes.SAVANNA_PLATEAU;
	private static final BiomeBase desertHills = Biomes.DESERT_HILLS;
	private static final BiomeBase swamplandHills = Biomes.SWAMP_HILLS;
	private static final BiomeBase taigaHills = Biomes.SNOWY_TAIGA_HILLS;
	private static final BiomeBase shrublandHills = Biomes.SAVANNA_PLATEAU;
	private static final BiomeBase plainsHills = Biomes.FOREST; // The only Non-Beta Type. Confusing though because plains don't have hills.
	private static final BiomeBase seasonalForestHills = Biomes.BIRCH_FOREST_HILLS;
	private static final BiomeBase rainforestHills = Biomes.FLOWER_FOREST;
	private static final BiomeBase forestHills = Biomes.FOREST;
	// Ocean and other declarations
	private static final BiomeBase ocean = Biomes.OCEAN;
	private static final BiomeBase deepOcean = Biomes.DEEP_OCEAN;
	private static final BiomeBase defaultBiome = Biomes.PLAINS; // I don't know how to handle, oops
	private static final BiomeBase beachBiome = Biomes.BEACH;


	// CONSTRUCTOR //

	public BetaPlusBiomeSelector()
	{
		super(Lists.newArrayList(beachBiome, desert, desertHills));
	}


	// noiseSelect is NOT used for this.
	@Override
	public BiomeBase getBiome(double temperature, double humidity, double noiseSelect, TerrainType type)
	{
		// Ripped from Beta, This is the basis for all other functions
		// Added some switch statement for hills and the like
		switch (type)
		{
			case land:
				return temperature < 0.1f ? tundra : (humidity < 0.2f ? (temperature < 0.5f ? tundra : (temperature < 0.95f ? savanna : desert)) : (humidity > 0.5f && temperature < 0.7f ? swampland : (temperature < 0.5f ? taiga : (temperature < 0.97f ? (humidity < 0.35f ? shrubland : forest) : (humidity < 0.45f ? plains : ((humidity *= temperature) < 0.9f ? seasonalForest : rainforest))))));
			case hillyLand:
				return temperature < 0.1f ? tundraHills : (humidity < 0.2f ? (temperature < 0.5f ? tundraHills : (temperature < 0.95f ? savannaHills : desertHills)) : (humidity > 0.5f && temperature < 0.7f ? swamplandHills : (temperature < 0.5f ? taigaHills : (temperature < 0.97f ? (humidity < 0.35f ? shrublandHills : forestHills) : (humidity < 0.45f ? plainsHills : ((humidity *= temperature) < 0.9f ? seasonalForestHills : rainforestHills))))));
			// SAME AS ABOVE
			case mountains:
				return temperature < 0.1f ? tundraHills : (humidity < 0.2f ? (temperature < 0.5f ? tundraHills : (temperature < 0.95f ? savannaHills : desertHills)) : (humidity > 0.5f && temperature < 0.7f ? swamplandHills : (temperature < 0.5f ? taigaHills : (temperature < 0.97f ? (humidity < 0.35f ? shrublandHills : forestHills) : (humidity < 0.45f ? plainsHills : ((humidity *= temperature) < 0.9f ? seasonalForestHills : rainforestHills))))));
			case sea:
				return ocean;
			case deepSea:
				return deepOcean;
			case coastal:
				return beachBiome;
			case island:
				return beachBiome; // Update later.
			case generic:
				return defaultBiome;
			default:
				return defaultBiome;
		}
	}
}
