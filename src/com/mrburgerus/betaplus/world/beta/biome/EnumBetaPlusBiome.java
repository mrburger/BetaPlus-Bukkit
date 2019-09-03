package com.mrburgerus.betaplus.world.beta.biome;

import net.minecraft.server.v1_14_R1.BiomeBase;
import net.minecraft.server.v1_14_R1.Biomes;
import net.minecraft.server.v1_14_R1.Block;
import net.minecraft.server.v1_14_R1.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;

public enum EnumBetaPlusBiome implements IBetaPlusBiome
{
	//Enums
	rainforest(Biomes.JUNGLE),
	swampland(Biomes.SWAMP),
	seasonalForest(Biomes.FLOWER_FOREST),
	forest(Biomes.FOREST),
	savanna(Biomes.SAVANNA),
	shrubland(Biomes.PLAINS),
	taiga(Biomes.SNOWY_TAIGA),
	desert(Biomes.DESERT, Bukkit.createBlockData(Material.SAND), Bukkit.createBlockData(Material.SAND)),
	plains(Biomes.PLAINS),
	tundra(Biomes.SNOWY_TUNDRA),
	//New Enums
	warmOcean(Biomes.WARM_OCEAN),
	lukewarmOcean(Biomes.LUKEWARM_OCEAN),
	deepLukewarmOcean(Biomes.DEEP_LUKEWARM_OCEAN),
	coldOcean(Biomes.COLD_OCEAN),
	deepColdOcean(Biomes.DEEP_COLD_OCEAN),
	beach(Biomes.BEACH),
	roofForest(Biomes.DARK_FOREST),
	mountain(Biomes.WOODED_MOUNTAINS),
	iceSpikes(Biomes.ICE_SPIKES),
	megaTaiga(Biomes.GIANT_SPRUCE_TAIGA),
	mesa(Biomes.BADLANDS, Bukkit.createBlockData(Material.TERRACOTTA), Bukkit.createBlockData(Material.TERRACOTTA)),
	birchForest(Biomes.BIRCH_FOREST),
	flowerPlains(Biomes.SUNFLOWER_PLAINS),
	newForest(Biomes.FLOWER_FOREST),
	defaultB(Biomes.PLAINS);

	//Overrides
	@Override
	public BiomeBase getHandle()
	{
		return handle;
	}

	@Override
	public void setHandle(BiomeBase biomeHandle)
	{
		handle = biomeHandle;
	}

	//Fields
	public BiomeBase handle;
	public final BlockData topBlock;
	public final BlockData fillerBlock;
	private static final BiomeBase[] BIOME_LOOKUP_TABLE;

	//Constructors
	EnumBetaPlusBiome(BiomeBase handle)
	{
		this(handle, Bukkit.createBlockData(Material.GRASS_BLOCK), Bukkit.createBlockData(Material.DIRT));
	}

	EnumBetaPlusBiome(BiomeBase biomeHandle, BlockData top, BlockData filler)
	{
		handle = biomeHandle;
		topBlock = top;
		fillerBlock = filler;
	}

	//Initialize
	static
	{
		BIOME_LOOKUP_TABLE = new BiomeBase[4096];
		EnumBetaPlusBiome.generateBiomeLookup();
	}

	//Methods
	public static void generateBiomeLookup()
	{
		for (int i = 0; i < 64; ++i)
		{
			for (int j = 0; j < 64; ++j)
			{
				//EDITED
				BIOME_LOOKUP_TABLE[i + j * 64] = BetaPlusBiomeSelector.getBiome((float) i / 63.0f, (float) j / 63.0f);
			}
		}
	}

	//Gets Value
	public static BiomeBase getBiomeFromLookup(double temperature, double humidity)
	{
		int i = (int) (temperature * 63.0);
		int j = (int) (humidity * 63.0);
		return BIOME_LOOKUP_TABLE[i + j * 64];
	}

	// Convert Biome map to Enum Map, Respective.
	public static EnumBetaPlusBiome[] convertBiomeTable(BiomeBase[] biomeLookupTable)
	{
		// Create Equal Length Array
		EnumBetaPlusBiome[] biomePlus = new EnumBetaPlusBiome[biomeLookupTable.length];

		for (int i = 0; i < biomeLookupTable.length; i++)
		{
			for (EnumBetaPlusBiome biomeGenBetaPlus : EnumBetaPlusBiome.values())
			{
				if (biomeGenBetaPlus.handle == biomeLookupTable[i])
				{
					//System.out.println("FOUND: " + biomeGenBetaPlus.handle.toString());
					biomePlus[i] = biomeGenBetaPlus;
				}
			}
			if (biomePlus[i] == null)
			{
				biomePlus[i] = EnumBetaPlusBiome.defaultB;
			}
		}
		return biomePlus;
	}

}
