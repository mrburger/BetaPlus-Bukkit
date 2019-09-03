package com.mrburgerus.betaplus.world.beta_new.biome;

import com.mrburgerus.betaplus.util.BiomeReplaceUtil;
import net.minecraft.server.v1_14_R1.BiomeBase;
import net.minecraft.server.v1_14_R1.MathHelper;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import static org.bukkit.Material.*;

public enum EnumBetaPlusBiome implements IBetaPlusBiome
{
	//Enums
	rainforest(Biome.JUNGLE),
	swampland(Biome.SWAMP),
	seasonalForest(Biome.DARK_FOREST),
	forest(Biome.FOREST),
	savanna(Biome.SAVANNA),
	shrubland(Biome.PLAINS),
	taiga(Biome.SNOWY_TAIGA),
	desert(Biome.DESERT, SAND, SAND),
	plains(Biome.PLAINS),
	tundra(Biome.SNOWY_TUNDRA),
	//New Enums
	warmOcean(Biome.WARM_OCEAN),
	lukewarmOcean(Biome.LUKEWARM_OCEAN),
	deepLukewarmOcean(Biome.DEEP_LUKEWARM_OCEAN),
	coldOcean(Biome.COLD_OCEAN),
	deepColdOcean(Biome.DEEP_COLD_OCEAN),
	beach(Biome.BEACH),
	roofForest(Biome.DARK_FOREST),
	mountain(Biome.WOODED_MOUNTAINS),
	iceSpikes(Biome.ICE_SPIKES),
	megaTaiga(Biome.GIANT_SPRUCE_TAIGA),
	birchForest(Biome.BIRCH_FOREST),
	flowerPlains(Biome.SUNFLOWER_PLAINS),
	flowerForest(Biome.FLOWER_FOREST),
	defaultB(Biome.PLAINS);

	//Overrides
	@Override
	public Biome getHandle()
	{
		return handle;
	}

	@Override
	public void setHandle(Biome biomeHandle)
	{
		handle = biomeHandle;
	}

	//Fields
	public Biome handle;
	public final Material topBlock;
	public final Material fillerBlock;
	private static final Biome[] BIOME_LOOKUP_TABLE;

	//Constructors
	EnumBetaPlusBiome(Biome handle)
	{
		this(handle, GRASS_BLOCK, DIRT);
	}

	EnumBetaPlusBiome(Biome biomeHandle, Material top, Material filler)
	{
		handle = biomeHandle;
		topBlock = top;
		fillerBlock = filler;
	}

	//Initialize
	static
	{
		BIOME_LOOKUP_TABLE = new Biome[4096];
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
	public static Biome getBiomeFromLookup(double temperature, double humidity)
	{
		int i = (int) (MathHelper.a(temperature, 0.0, 1.0) * 63.0);
		int j = (int) (MathHelper.a(humidity, 0.0, 1.0) * 63.0);
		return BIOME_LOOKUP_TABLE[i + j * 64];
	}

	// Convert Biome map to Enum Map, Respective.
	public static EnumBetaPlusBiome[] convertBiomeTable(Biome[] biomeLookupTable)
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

	// Convert Biome map to Enum Map, Respective.
	public static EnumBetaPlusBiome[] convertBiomeTable(BiomeBase[] biomeLookupTable)
	{
		// Create Equal Length Array
		EnumBetaPlusBiome[] biomePlus = new EnumBetaPlusBiome[biomeLookupTable.length];
		Biome[] newBiomes = BiomeReplaceUtil.toBukkitBiome(biomeLookupTable);

		for (int i = 0; i < biomeLookupTable.length; i++)
		{
			for (EnumBetaPlusBiome biomeGenBetaPlus : EnumBetaPlusBiome.values())
			{
				if (biomeGenBetaPlus.handle == newBiomes[i])
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
